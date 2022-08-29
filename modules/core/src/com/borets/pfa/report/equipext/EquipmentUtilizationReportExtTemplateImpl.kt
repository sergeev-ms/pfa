package com.borets.pfa.report.equipext

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

    var equipmentList = mutableListOf<EquipmentItem>()
    var equipmentUtilizationList = mutableListOf<EquipmentUtilizationItem>()
    var countrySettingAllocationRemapList = mutableListOf<CountrySettingsAllocationRemapItem>()
    var activityStatsList = mutableListOf<ActivityStat>()

    companion object {
        const val ALLOCATION_BAND_NAME = "SystemAllocationData"
        const val UTILIZATION_BAND_NAME = "EquipmentUtilization"
        const val COUNTRY_SETTINGS_ALLOCATION_REMAP_BAND_NAME = "CountrySettingsAllocationRemap"
        const val ACTIVITY_STATS_BAND_NAME = "ActivityStats"

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
        }

    }

    override fun processDataElement(bandName: String, bandData: BandData) {
        when (bandName) {
            DATA_BAND_NAME -> {
                getEquipment(bandData).let {
                    it.equipmentAllocations = processSubBand(bandData)
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
                getAnalyticStats(bandData).let { activityStatsList.add(it) }
            }
        }
    }

    override fun afterProcess() {
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
                EquipmentAllocation(remap.utilizationValueTypeItem, evaluateScript(remap.remapScript, equipment))
            }.toList()
                .let {
                    equipment.equipmentAllocations = it
                }
        }

        //additional columns
        columns.add(Column(
            EquipmentUtilizationItem.REVENUE_TYPE_COLUMN_TYPE,
            "Revenue Mode",
            EquipmentUtilizationItem.REVENUE_TYPE_COLUMN_TYPE)
        )
    }

    private fun getAnalyticStats(bandData: BandData): ActivityStat {
        return ActivityStat(
            bandData.data[ActivityStat.ACCOUNT_ID_COLUMN].toString(),
            bandData.data[ActivityStat.ANALYTIC_COLUMN].toString(),
            bandData.data[ActivityStat.WELL_TAG_COLUMN].toString(),
            bandData.data[ActivityStat.YEAR_MONTH_COLUMN] as Date,
            bandData.data[ActivityStat.VALUE_COLUMN] as Int
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

    private fun evaluateScript(remapScript: String, equipment: EquipmentItem): BigDecimal {
        val scripting = AppBeans.get(Scripting.NAME, Scripting::class.java)

        val accountActivityStats = activityStatsList.filter {
            it.accountId == equipment.equipmentSystem.accountId
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


    private fun getCountrySettingsAllocationRemap(bandData: BandData): CountrySettingsAllocationRemapItem {
        return CountrySettingsAllocationRemapItem(
            bandData.data[CountrySettingsAllocationRemapItem.COUNTRY_ID_COLUMN].toString(),
            bandData.data[CountrySettingsAllocationRemapItem.UTILIZATION_VALUE_TYPE_ID_COLUMN].toString(),
            bandData.data[CountrySettingsAllocationRemapItem.UTILIZATION_VALUE_TYPE_NAME_COLUMN].toString(),
            bandData.data[CountrySettingsAllocationRemapItem.UTILIZATION_VALUE_TYPE_ORDER_COLUMN] as Int,
            bandData.data[CountrySettingsAllocationRemapItem.REMAP_SCRIPT_COLUMN] as String,
        )
    }

    private fun processSubBand(subBandData: BandData): List<EquipmentAllocation> {
        val subBandDataList = subBandData.findBandsRecursively(ALLOCATION_BAND_NAME)
        return subBandDataList?.map { bandData -> getAllocation(bandData) }
            ?.toList()
            ?: emptyList()
    }


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
//            d.data[EquipmentItem.NEXT_RUNS_COMP] as BigDecimal,
//            d.data[EquipmentItem.PULL_FIRST_RUN] as BigDecimal,
//            d.data[EquipmentItem.PULL_NEXT_RUNS] as BigDecimal,
            d.data[EquipmentItem.EQUIPMENT_TYPE_ORDER] as Int
//            d.data[EquipmentItem.REVENUE_MODE] as String
        )
        system.equipmentItems.add(equipmentItem)
        return equipmentItem
    }
    private fun getAllocation(bandData: BandData): EquipmentAllocation {
        return EquipmentAllocation(
            bandData.data[EquipmentAllocation.APPLICATION_DATA_ID_COLUMN].toString(),
            bandData.data[EquipmentAllocation.SYSTEM_ID_COLUMN].toString(),
            bandData.data[EquipmentAllocation.UTILIZATION_VALUE_TYPE_ID_COLUMN].toString(),
            bandData.data[EquipmentAllocation.UTILIZATION_VALUE_TYPE_NAME_COLUMN].toString(),
            bandData.data[EquipmentAllocation.UTILIZATION_VALUE_TYPE_ORDER_COLUMN] as Int,
            bandData.data[EquipmentAllocation.VALUE_COLUMN] as BigDecimal,
        )
    }

    private fun getUtilization(bandData: BandData): EquipmentUtilizationItem {
        return EquipmentUtilizationItem(
            bandData.data[EquipmentUtilizationItem.UTIL_ACCOUNT_ID_COLUMN] as String,
            bandData.data[EquipmentUtilizationItem.UTIL_EQUIPMENT_TYPE_ID_COLUMN] as String,
            bandData.data[EquipmentUtilizationItem.UTIL_REVENUE_MODE_COLUMN] as String,
            bandData.data[EquipmentUtilizationItem.UTIL_VALUE_TYPE_ID_COLUMN] as String,
            bandData.data[EquipmentUtilizationItem.UTIL_VALUE_TYPE_NAME_COLUMN] as String,
            bandData.data[EquipmentUtilizationItem.UTIL_VALUE_TYPE_ORDER_COLUMN] as Int,
            bandData.data[EquipmentUtilizationItem.UTIL_VALUE_COLUMN] as BigDecimal
        )
    }

    private fun setupHeader(headerRow: Row) {
        columns.forEach { reportColumn ->
            addCellToRow(headerRow, reportColumn.name, getStyle(reportColumn.headerStyleName)!!)
        }
    }

//    private fun getOrderedEquipment(): List<EquipmentItem> {
//        return coordinates.keys.sortedWith(
//            compareBy(
//                { it.equipmentSystem.customerOrder },
//                { it.equipmentSystem.referenceCode },
//                { it.equipmentSystem.customer },
//                { it.equipmentSystem.systemNumber },
//                { it.equipmentTypeOrder },
//            )
//        )
//    }

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
//        addColorPercentCellToRow(row, equipment.value1stRun)
//        addColorPercentCellToRow(row, equipment.valueNextRuns)
//        addColorPercentCellToRow(row, equipment.valueNextRunsCompetitor)
//        addColorPercentCellToRow(row, equipment.valuePullFirstRun)
//        addColorPercentCellToRow(row, equipment.valuePullNextRuns)
//        addCellToRow(row, equipment.revenueMode, getStyle("textValue")!!)

        columns.forEach { reportColumn ->
            if (EquipmentUtilizationItem.REVENUE_TYPE_COLUMN_TYPE == reportColumn.type) {
                val revenueModeValue = equipment.equipmentUtilizations.firstOrNull()?.revenueModeId ?: ""
                addCellToRow(row, revenueModeValue, getStyle(reportColumn.valueStyleName)!!)
            }
            else {
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

    private fun addNumberCellToRow(row: Row, value: Any) {
        val c = addCellToRow(row, value, getStyle(CELL_STYLE_TEXT)!!)
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