package com.borets.pfa.report.equipext

import com.borets.pfa.entity.activity.JobType
import com.borets.pfa.entity.activity.WellTag
import com.borets.pfa.report.custom.Column
import com.borets.pfa.report.custom.CustomExcelReportWithMultipleBandsTemplate
import com.borets.pfa.report.equipext.dto.*
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.Scripting
import com.haulmont.yarg.formatters.impl.xlsx.Document
import com.haulmont.yarg.structure.BandData
import org.slf4j.LoggerFactory.getLogger
import org.xlsx4j.jaxb.Context
import org.xlsx4j.sml.*
import java.math.BigDecimal
import java.util.*

class EquipmentUtilizationReportExtTemplateImpl : CustomExcelReportWithMultipleBandsTemplate<EquipmentItem>() {
    private val LOGGER = getLogger(EquipmentUtilizationReportExtTemplateImpl::class.java)

    val equipmentList = mutableListOf<EquipmentItem>()
    val equipmentUtilizationList = mutableListOf<EquipmentUtilizationItem>()
    val countrySettingAllocationRemapList = mutableListOf<CountrySettingsAllocationRemapItem>()
    val activityStatsItemList = mutableListOf<ActivityStatItem>()
//    val activityStatsMap = mutableMapOf<String, Map<String, Int>>()
    val demandRulesList = mutableListOf<DemandRuleItem>()

    val scripting : Scripting = AppBeans.get(Scripting.NAME, Scripting::class.java)

    companion object {
        const val ALLOCATION_BAND_NAME = "SystemAllocationData"
        const val UTILIZATION_BAND_NAME = "EquipmentUtilization"
        const val COUNTRY_SETTINGS_ALLOCATION_REMAP_BAND_NAME = "CountrySettingsAllocationRemap"
        const val ACTIVITY_STATS_BAND_NAME = "ActivityStats"
        const val COUNTRY_SETTINGS_DEMAND_RULES_BAND_NAME = "SettingsDemandRules"

        //must presents it xlsx template with {stylename} syntax
        const val CELL_STYLE_TEXT = "textValue"
        const val CELL_STYLE_PERCENT = "percentValue"
        const val CELL_STYLE_PERCENT_COLORED = "colorPercentValue"
        const val CELL_STYLE_BOLD_BORDER = "textWithBoldBorderValue"
        const val CELL_STYLE_ACTIVITY_TYPE = "activityType"
    }

    override fun preProcessColumns(bandName: String, bandData: BandData) {
        when (bandName) {
            COUNTRY_SETTINGS_ALLOCATION_REMAP_BAND_NAME -> columns.add(getAllocationColumn(bandData))
            UTILIZATION_BAND_NAME -> columns.add(getUtilizationColumn(bandData))
//            ALLOCATION_BAND_NAME ->  columns.add(getAllocationColumn(bandData))
            COUNTRY_SETTINGS_DEMAND_RULES_BAND_NAME -> columns.addAll(getDemandColumnList(bandData))
        }

    }

    override fun processDataElement(bandName: String, bandData: BandData) {
        when (bandName) {
            DATA_BAND_NAME -> {
                getEquipment(bandData).let {
//                    it.equipmentAllocations = processSubBand(bandData)
                    equipmentList.add(it)
                }
            }
            COUNTRY_SETTINGS_ALLOCATION_REMAP_BAND_NAME -> {
                getCountrySettingsAllocationRemap(bandData).let { countrySettingAllocationRemapList.add(it) }
            }
            UTILIZATION_BAND_NAME -> {
                getUtilization(bandData).let { equipmentUtilizationList.add(it) }
            }
            ACTIVITY_STATS_BAND_NAME -> {
                getActivityStatItem(bandData).let { activityStatsItemList.add(it) }
            }
            COUNTRY_SETTINGS_DEMAND_RULES_BAND_NAME -> {
                getDemandRules(bandData).let { demandRulesList.add(it) }
            }
        }
    }


    override fun afterProcess() {
//        computeActivityStats()

        equipmentList.forEach {equipmentItem ->
            equipmentUtilizationList.filter { it.rowKey == equipmentItem.rowKey }
                .forEach {
                    it.equipmentItem = equipmentItem
                    equipmentItem.equipmentUtilizations.add(it)
                }
        }

        //process equipment allocation
        equipmentList.forEach {equipment ->
            countrySettingAllocationRemapList.map { remap ->
                EquipmentAllocation(remap.utilizationValueTypeItem, evaluateUsageScript(remap.remapScript, equipment))
            }.toList()
                .let {
                    equipment.equipmentAllocations = it
                }
        }

        equipmentList.forEach { equipment ->
            demandRulesList.flatMap { demandRule ->
                dates.map {
                    EquipmentDemandItem(demandRule.id, demandRule.name, it,
                        evaluateEquipmentDemandScript(demandRule.script, equipment, it))
                }.toList()
            }.let {
                equipment.equipmentDemands.addAll(it)
            }
        }

        //additional columns
        columns.add(Column(
            EquipmentUtilizationItem.REVENUE_TYPE_COLUMN_TYPE,
            "Revenue Mode",
            EquipmentUtilizationItem.REVENUE_TYPE_COLUMN_TYPE)
        )
    }



    override fun generateReport(sheetWrappers: List<Document.SheetWrapper>) {
        val sheetWrapper = sheetWrappers[0]
        val sheetName = sheetWrapper.name
        val worksheetPart = sheetWrapper.worksheet
        val contents = worksheetPart.contents
        val sheetData = contents.sheetData
        val rows = sheetData.row

        val ctSheetCalcPr = CTSheetCalcPr()
        ctSheetCalcPr.isFullCalcOnLoad = true
        contents.sheetCalcPr = ctSheetCalcPr

        val yearRow = rows[0]
        val headerRow = rows[1]

        // Initializing structure for merging cells
        var mergeCells = contents.mergeCells
        if (mergeCells == null) {
            mergeCells = CTMergeCells()
            contents.mergeCells = mergeCells
        }

        equipmentList.forEach { equipment ->
            val row = Context.getsmlObjectFactory().createRow()
            val refRow = rows.last()
            rows.add(row)
            row.r = rows.size.toLong()
            row.s = refRow.s
            row.isCustomFormat = refRow.isCustomFormat
            row.isPh = refRow.isPh
            row.outlineLevel = refRow.outlineLevel

            // print data
            addEquipmentItemInfo(row, equipment)

        }
        setupHeader(headerRow)
        setupAutoFilter(sheetName, contents, headerRow)
//        signReport(rows[0].c[2], Date(), RecordType.KPI)
    }


    private fun getActivityStatItem(bandData: BandData): ActivityStatItem {
        return ActivityStatItem(
            bandData.data[ActivityStatItem.ACCOUNT_ID_COLUMN].toString(),
            bandData.data[ActivityStatItem.ANALYTIC_COLUMN].toString(),
            bandData.data[ActivityStatItem.JOB_TYPE_COLUMN].toString(),
            bandData.data[ActivityStatItem.WELL_TAG_COLUMN].toString(),
            bandData.data[ActivityStatItem.VARIABLE_NAME_COLUMN] as String?,
            bandData.data[ActivityStatItem.YEAR_MONTH_COLUMN] as Date,
            bandData.data[ActivityStatItem.VALUE_COLUMN] as Int
        )
    }

//    private fun computeActivityStats() {
//        activityStatsItemList.groupBy { it.accountId }.entries
//            .associateTo(activityStatsMap) { entry ->
//                entry.key to
//                        entry.value.groupingBy { it.analyticId }
//                            .fold(0) { acc, el -> acc + el.value }
//            }
//    }

    private fun getDemandRules(bandData: BandData): DemandRuleItem {
        return DemandRuleItem(
            bandData.data[DemandRuleItem.DEMAND_TYPE_ID_COLUMN].toString(),
            bandData.data[DemandRuleItem.DEMAND_TYPE_NAME_COLUMN] as String,
            bandData.data[DemandRuleItem.DEMAND_SCRIPT_COLUMN] as String
        )
    }

    private fun evaluateUsageScript(remapScript: String, equipment: EquipmentItem): BigDecimal {
        val accountActivityStats = activityStatsItemList.filter {
            it.accountId == equipment.equipmentSystem.accountId &&
                    it.jobType == JobType.INSTALL.id //only installs
        }.toList()

        //Replace with analyticSet from Country Settings. Look at Country Settings -> Utilization Value Types
        val totalSum = accountActivityStats.sumOf { it.value }
        val firstInstallSum = accountActivityStats.filter { it.wellTag == WellTag.FIRST.id }.sumOf { it.value }
        val sequentInstallSum = accountActivityStats.filter { it.wellTag == WellTag.SEQUENT.id }.sumOf { it.value }

        return scripting.evaluateGroovy(remapScript,
            mutableMapOf<String, Any>(
                "value1stRun" to equipment.value1stRun,
                "valueNextRuns" to equipment.valueNextRuns,
                "totalSum" to totalSum,
                "firstInstallSum" to firstInstallSum,
                "sequentInstallSum" to sequentInstallSum
            )
        )
    }

    private fun evaluateEquipmentDemandScript(script: String, equipment: EquipmentItem, date: Date): BigDecimal {
        val activityVariableValueMap = activityStatsItemList
            .filter {
                it.accountId == equipment.equipmentSystem.accountId && it.yearMonth == date
            }
            .filter { !it.variableName.isNullOrBlank() }
            .associate { it.variableName!! to it.value }

        val usageVariableValueMap = equipment.equipmentAllocations
            .associate { it.allocationValueTypeItem.variableName!! to it.value }
        val utilizationVariableValueMap = equipment.equipmentUtilizations
            .associate { it.utilizationValueTypeItem.variableName!! to it.value }

        val bindingMap: MutableMap<String, Any?> = (activityVariableValueMap + usageVariableValueMap + utilizationVariableValueMap)
            .toMutableMap()

        bindingMap["revenueMode"] = equipment.equipmentUtilizations.getOrNull(0)?.revenueModeId
        bindingMap["qty"] = equipment.qty

        return scripting.evaluateGroovy(script, bindingMap)
    }

    private fun getCountrySettingsAllocationRemap(bandData: BandData): CountrySettingsAllocationRemapItem {
        return CountrySettingsAllocationRemapItem(
            bandData.data[CountrySettingsAllocationRemapItem.COUNTRY_ID_COLUMN].toString(),
            bandData.data[CountrySettingsAllocationRemapItem.UTILIZATION_VALUE_TYPE_ID_COLUMN].toString(),
            bandData.data[CountrySettingsAllocationRemapItem.UTILIZATION_VALUE_TYPE_NAME_COLUMN].toString(),
            bandData.data[CountrySettingsAllocationRemapItem.UTILIZATION_VALUE_TYPE_VARIABLE_NAME_COLUMN] as String?,
            bandData.data[CountrySettingsAllocationRemapItem.UTILIZATION_VALUE_TYPE_ORDER_COLUMN] as Int,
            bandData.data[CountrySettingsAllocationRemapItem.REMAP_SCRIPT_COLUMN] as String,
        )
    }

//    private fun processSubBand(subBandData: BandData): List<EquipmentAllocation> {
//        val subBandDataList = subBandData.findBandsRecursively(ALLOCATION_BAND_NAME)
//        return subBandDataList?.map { bandData -> getAllocation(bandData) }
//            ?.toList()
//            ?: emptyList()
//    }


    private fun getAllocationColumn(bandData: BandData) = Column(
        EquipmentAllocation.COLUMN_TYPE,
        bandData.data[CountrySettingsAllocationRemapItem.UTILIZATION_VALUE_TYPE_NAME_COLUMN].toString(),
        bandData.data[CountrySettingsAllocationRemapItem.UTILIZATION_VALUE_TYPE_ID_COLUMN].toString(),
        bandData.data[CountrySettingsAllocationRemapItem.UTILIZATION_VALUE_TYPE_ORDER_COLUMN] as Int,
        CELL_STYLE_ACTIVITY_TYPE,
        CELL_STYLE_PERCENT
    )

    private fun getUtilizationColumn(bandData: BandData) = Column(
        EquipmentUtilizationItem.COLUMN_TYPE,
        bandData.data[EquipmentUtilizationItem.UTIL_VALUE_TYPE_NAME_COLUMN] as String,
        bandData.data[EquipmentUtilizationItem.UTIL_VALUE_TYPE_ID_COLUMN] as String,
        bandData.data[EquipmentUtilizationItem.UTIL_VALUE_TYPE_ORDER_COLUMN] as Int * 10,
        CELL_STYLE_ACTIVITY_TYPE,
        CELL_STYLE_PERCENT_COLORED
    )

    var dateStartOrder = 0

    private fun getDemandColumnList(bandData: BandData): Collection<Column> {
        val columns = dates.map {
            return@map Column(
                EquipmentDemandItem.DEMAND_COLUMN_TYPE,
                EquipmentDemandItem.formatDate(it),
                EquipmentDemandItem.formatColumnId(bandData.data[DemandRuleItem.DEMAND_TYPE_ID_COLUMN].toString(), it),
                it.toInstant().epochSecond.toInt() + dateStartOrder,
                CELL_STYLE_ACTIVITY_TYPE,
                CELL_STYLE_TEXT
            )
        }.toList()

        // TODO: It's quickfix. implement order.
        dateStartOrder+=100000000

        return columns
    }

    private fun getEquipment(d: BandData): EquipmentItem {
        val system = EquipmentSystem(
            d.data[EquipmentSystem.REFERENCE_CODE] as String,
            d.data[EquipmentSystem.ACCOUNT_ID_COLUMN] as String,
            d.data[EquipmentSystem.CUSTOMER] as String,
            d.data[EquipmentSystem.CUSTOMER_ORDER] as Int,
            d.data[EquipmentSystem.RENTAL_OR_SALE] as String,
            d.data[EquipmentSystem.SYSTEM_NUMBER] as Long
        )
        val equipmentItem = EquipmentItem(
            system,
            d.data[EquipmentItem.APPLICATION_DATA_ID] as String,
            d.data[EquipmentItem.EQUIPMENT_TYPE_ID_COLUMN] as String,
            d.data[EquipmentItem.EQUIPMENT_TYPE] as String,
            d.data[EquipmentItem.PART_NUMBER] as String,
            d.data[EquipmentItem.PRODUCT_DESCRIPTION] as String,
            d.data[EquipmentItem.QTY] as BigDecimal,
            d.data[EquipmentItem.UOM] as String,
            d.data[EquipmentItem.FIRST_RUN] as BigDecimal,
            d.data[EquipmentItem.NEXT_RUNS] as BigDecimal,
            d.data[EquipmentItem.EQUIPMENT_TYPE_ORDER] as Int
        )
        system.equipmentItems.add(equipmentItem)
        return equipmentItem
    }
//    private fun getAllocation(bandData: BandData): EquipmentAllocation {
//        return EquipmentAllocation(
//            bandData.data[EquipmentAllocation.APPLICATION_DATA_ID_COLUMN].toString(),
//            bandData.data[EquipmentAllocation.SYSTEM_ID_COLUMN].toString(),
//            bandData.data[EquipmentAllocation.UTILIZATION_VALUE_TYPE_ID_COLUMN].toString(),
//            bandData.data[EquipmentAllocation.UTILIZATION_VALUE_TYPE_NAME_COLUMN].toString(),
//            bandData.data[EquipmentAllocation.UTILIZATION_VALUE_TYPE_VARIABLE_COLUMN] as String,
//            bandData.data[EquipmentAllocation.UTILIZATION_VALUE_TYPE_ORDER_COLUMN] as Int,
//            bandData.data[EquipmentAllocation.VALUE_COLUMN] as BigDecimal,
//        )
//    }

    private fun getUtilization(bandData: BandData): EquipmentUtilizationItem {
        return EquipmentUtilizationItem(
            bandData.data[EquipmentUtilizationItem.UTIL_ACCOUNT_ID_COLUMN] as String,
            bandData.data[EquipmentUtilizationItem.UTIL_EQUIPMENT_TYPE_ID_COLUMN] as String,
            bandData.data[EquipmentUtilizationItem.UTIL_REVENUE_MODE_COLUMN] as String,
            bandData.data[EquipmentUtilizationItem.UTIL_VALUE_TYPE_ID_COLUMN] as String,
            bandData.data[EquipmentUtilizationItem.UTIL_VALUE_TYPE_NAME_COLUMN] as String,
            bandData.data[EquipmentUtilizationItem.UTIL_VALUE_TYPE_VARIABLE_NAME_COLUMN] as String,
            bandData.data[EquipmentUtilizationItem.UTIL_VALUE_TYPE_ORDER_COLUMN] as Int,
            bandData.data[EquipmentUtilizationItem.UTIL_VALUE_COLUMN] as BigDecimal
        )
    }

    private fun setupHeader(headerRow: Row) {
        columns.forEach { reportColumn ->
            addCellToRow(headerRow, reportColumn.name, getStyle(reportColumn.headerStyleName)!!)
        }
    }

    private fun addEquipmentItemInfo(row: Row, equipment: EquipmentItem) {
        addCellToRow(row, equipment.equipmentSystem.referenceCode, getStyle(CELL_STYLE_TEXT)!!)
        addCellToRow(row, equipment.equipmentSystem.customer, getStyle(CELL_STYLE_TEXT)!!)
        addCellToRow(row, equipment.equipmentSystem.rentalOrSale, getStyle(CELL_STYLE_TEXT)!!)
        addCellToRow(row, equipment.equipmentSystem.systemNumber, getStyle(CELL_STYLE_TEXT)!!)
        addCellToRow(row, equipment.equipmentType, getStyle(CELL_STYLE_TEXT)!!)
        addCellToRow(row, equipment.partNumber, getStyle(CELL_STYLE_TEXT)!!)
        addCellToRow(row, equipment.productDescription, getStyle(CELL_STYLE_TEXT)!!)
        addNumberCellToRow(row, equipment.qty)
        addCellToRow(row, equipment.uom, getStyle(CELL_STYLE_BOLD_BORDER)!!)

        columns.forEach { reportColumn ->
            when (reportColumn.type) {
                EquipmentUtilizationItem.REVENUE_TYPE_COLUMN_TYPE -> {
                    val revenueModeValue = equipment.equipmentUtilizations.firstOrNull()?.revenueModeId ?: ""
                    addCellToRow(row, revenueModeValue, getStyle(reportColumn.valueStyleName)!!)
                }

                EquipmentDemandItem.DEMAND_COLUMN_TYPE -> {
                    equipment.equipmentDemands.find { reportColumn == it.getColumn() }?.let {
                        addNumberCellToRow(row, it.value, reportColumn.valueStyleName)
                    }
                }
                else -> {
                    var value : BigDecimal?

                    //search in allocations
                    value = equipment.equipmentAllocations.find {
                        reportColumn == it.getColumn()
                    }?.value

                    //search in utilization
                    if (value == null) {
                        value = equipment.equipmentUtilizations.find {
                            reportColumn == it.getColumn()
                        }?.value
                    }
                    addPercentCellToRow(row, value ?: "")
                }
            }
        }
    }

    private fun addNumberCellToRow(row: Row, value: Any) {
        addNumberCellToRow(row, value, CELL_STYLE_TEXT)
    }

    private fun addNumberCellToRow(row: Row, value: Any, style: String) {
        val c = addCellToRow(row, value, getStyle(style)!!)
        c.t = STCellType.N
    }

    private fun addPercentCellToRow(row: Row, value: Any) {
        val c = addCellToRow(row, value, getStyle(CELL_STYLE_PERCENT)!!)
        c.t = STCellType.N
    }

    private fun addColorPercentCellToRow(row: Row, value: Any) {
        val c = addCellToRow(row, value, getStyle(CELL_STYLE_PERCENT_COLORED)!!)
        c.t = STCellType.N
    }

    private fun addCellToRow(row: Row, value: Any, style: Long) : Cell {
        val c = addCellToRow(row)
        c.t = STCellType.STR
        c.v = value.toString()
        c.s = style
        return c
    }

    private fun addCellToRow(row: Row) : Cell {
        val c = Cell()
        c.parent = row
        row.c.add(c)
        return c
    }

}