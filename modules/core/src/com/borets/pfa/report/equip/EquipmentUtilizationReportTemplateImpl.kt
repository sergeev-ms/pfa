package com.borets.pfa.report.equip

import com.borets.pfa.entity.activity.RecordType
import com.borets.pfa.report.custom.CustomExcelReportTemplate
import com.borets.pfa.report.custom.HorizontalPosition
import com.borets.pfa.report.custom.ReportCell
import com.borets.pfa.report.equip.dto.EquipmentItem
import com.borets.pfa.report.equip.dto.EquipmentSystem
import com.haulmont.yarg.formatters.impl.xlsx.Document
import com.haulmont.yarg.structure.BandData
import org.slf4j.LoggerFactory.getLogger
import org.xlsx4j.jaxb.Context
import org.xlsx4j.sml.CTMergeCells
import org.xlsx4j.sml.CTSheetCalcPr
import org.xlsx4j.sml.Cell
import org.xlsx4j.sml.Row
import org.xlsx4j.sml.STCellType
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class EquipmentUtilizationReportTemplateImpl : CustomExcelReportTemplate<EquipmentItem>() {

    var activityTypes = mutableListOf<String>()

    companion object {
        @JvmStatic
        private val LOGGER = getLogger(javaClass.enclosingClass)
        const val ACTIVITY_FIELD = "A"
    }

    override fun preProcessDataElement(bandName: String, d: BandData) {
        // Add column to collection columnNamesToOrders (if absent)
        val activity = d.data[ACTIVITY_FIELD] as String?
        if (activity != null) {
            columnNamesToOrders.computeIfAbsent(activity) { d.data[ORDER_FIELD] as Int }
            if (activity !in activityTypes) {
                activityTypes.add(activity)
            }
        } else {
            val date = d.data[PERIOD_FIELD] as Date?
            if (date != null) {
                columnNamesToOrders.computeIfAbsent(date.toString()) { d.data[ORDER_FIELD] as Int } /* ??? 100 * (d.data[ORDER_FIELD] as Int)*/
                // Add date to collection dates (gmif absent)
                if (date !in dates) {
                    dates.add(date)
                }
            }
        }
    }

    override fun afterPreProcess() {
        super.afterPreProcess()
        val ordersToActivityTypes =
            columnNamesToOrders.inverseBidiMap().filter { activityTypes.contains(it.value) }.toSortedMap()
        val sortedActivityTypes = mutableListOf<String>()
        ordersToActivityTypes.forEach { (k, v) -> sortedActivityTypes.add(v) }
        activityTypes = sortedActivityTypes
    }

    override fun processDataElement(bandName: String, d: BandData) {
        if (DATA_BAND_NAME == bandName) {
            val equipment = getEquipment(d)
            coordinates.computeIfAbsent(equipment) { mutableListOf() }
            val horizontalPositions = coordinates[equipment]!!
            val horizontalPosition = getHorizontalPosition(equipment, d)
            if (!horizontalPositions.contains(horizontalPosition)) {
                horizontalPositions.add(horizontalPosition)
            }

            // Getting existing objects instead of newly created
            val existingEquipment = coordinates.keys.first { equipmentItem -> equipmentItem == equipment }
            val existingHorizontalPosition = coordinates[existingEquipment]!!.first { hp -> hp == horizontalPosition }

            reportCells.add(
                ReportCell
                    .newDigit(
                        d.data.getOrDefault(CELL_VALUE, BigDecimal.ZERO),
                        existingEquipment,
                        existingHorizontalPosition
                    ) as ReportCell<EquipmentItem>
            )
        }
    }

    private fun getHorizontalPosition(equipment: EquipmentItem, d: BandData): HorizontalPosition {
        val period = d.data[PERIOD_FIELD] as Date? ?: Date(Long.MIN_VALUE)
        val name = d.data[ACTIVITY_FIELD] as String? ?: SimpleDateFormat("YYYY#MM").format(period)
        val order = d.data[ORDER_FIELD] as Int
        return HorizontalPosition(name, order, period)
    }

    private fun getEquipment(d: BandData): EquipmentItem {
        val system = EquipmentSystem(
            d.data[EquipmentSystem.REFERENCE_CODE] as String,
            d.data[EquipmentSystem.CUSTOMER] as String,
            d.data[EquipmentSystem.CUSTOMER_ORDER] as Int,
            d.data[EquipmentSystem.RENTAL_OR_SALE] as String,
            d.data[EquipmentSystem.SYSTEM_NUMBER] as Long
        )
        val equipmentItem = EquipmentItem(
            system,
            d.data[EquipmentItem.PART_NUMBER] as String,
            d.data[EquipmentItem.PRODUCT_DESCRIPTION] as String,
            d.data[EquipmentItem.QTY] as BigDecimal,
            d.data[EquipmentItem.UOM] as String,
            d.data[EquipmentItem.FIRST_RUN] as BigDecimal,
            d.data[EquipmentItem.NEXT_RUNS] as BigDecimal,
            d.data[EquipmentItem.EQUIPMENT_TYPE_ORDER] as Int,
            d.data[EquipmentItem.REVENUE_MODE] as String
        )
        system.equipmentItems.add(equipmentItem)
        return equipmentItem
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
        val autofilterRow = rows[2]

        // Initializing structure for merging cells
        var mergeCells = contents.mergeCells
        if (mergeCells == null) {
            mergeCells = CTMergeCells()
            contents.mergeCells = mergeCells
        }

        activityTypes.forEach { a ->
            val yrCell = Context.getsmlObjectFactory().createCell()
            yrCell.s = getStyle("year")!!
            yrCell.parent = yearRow
            yearRow.c.add(yrCell)

            val cell = Context.getsmlObjectFactory().createCell()
            cell.s = getStyle("activityType")!!
            cell.t = STCellType.STR
            cell.v = a
            cell.parent = headerRow
            headerRow.c.add(cell)

            val afCell = Context.getsmlObjectFactory().createCell()
            afCell.s = getStyle("activityType")!!
            afCell.parent = autofilterRow
            autofilterRow.c.add(afCell)
        }

//        var prevCellYear = 0
//        dates.forEach { d ->
//            val cell = Context.getsmlObjectFactory().createCell()
//            cell.s = getStyle("month")!!
//            cell.t = STCellType.STR
//            cell.v = SimpleDateFormat("MM").format(d)
//            cell.parent = headerRow
//            headerRow.c.add(cell)
//
//            val afCell = Context.getsmlObjectFactory().createCell()
//            afCell.s = getStyle("month")!!
//            afCell.parent = autofilterRow
//            autofilterRow.c.add(afCell)
//
//            val yrCell = Context.getsmlObjectFactory().createCell()
//            yrCell.s = getStyle("year")!!
//            yrCell.parent = yearRow
//            yearRow.c.add(yrCell)
//
//            if (d.year > prevCellYear) {
//                yrCell.t = STCellType.STR
//                yrCell.v = SimpleDateFormat("YYYY").format(d)
//            }
//            prevCellYear = d.year
//        }

        getOrderedEquipment().forEach { equipment ->
            val row = Context.smlObjectFactory.createRow()
            val refRow = rows.last()
            rows.add(row)
            row.r = rows.size.toLong()
            row.s = refRow.s
            row.isCustomFormat = refRow.isCustomFormat
            row.isPh = refRow.isPh
            row.outlineLevel = refRow.outlineLevel
            // create common equipment item info
            addEquipmentItemInfo(row, equipment)
            // create activity types
            activityTypes.forEach { activityType ->
                val cell =
                    reportCells.firstOrNull { it.coordinates.row == equipment && it.coordinates.column.name == activityType }
                if (cell == null) {
                    addPercentCellToRow(row, "")
                } else {
                    addPercentCellToRow(row, cell.valueNumber)
                }
            }
            // create period values
//            dates.forEach { date ->
//                val cell = reportCells.first { it.coordinates.row == equipment && it.coordinates.column.date == date }
//                addCellToRow(row, cell.valueNumber, getStyle("percentValue")!!)
//            }
        }
        setupAutoFilter(sheetName, contents, autofilterRow)
        signReport(rows[0].c[2], Date(), RecordType.KPI)
    }

    private fun getOrderedEquipment(): List<EquipmentItem> {
        return coordinates.keys.sortedWith(
            compareBy(
                { it.equipmentSystem.customerOrder },
                { it.equipmentSystem.referenceCode },
                { it.equipmentSystem.customer },
                { it.equipmentSystem.systemNumber },
                { it.equipmentTypeOrder },
            )
        )
    }

    private fun addEquipmentItemInfo(row: Row, equipment: EquipmentItem) {
        addCellToRow(row, equipment.equipmentSystem.referenceCode, getStyle("textValue")!!)
        addCellToRow(row, equipment.equipmentSystem.customer, getStyle("textValue")!!)
        addCellToRow(row, equipment.equipmentSystem.rentalOrSale, getStyle("textValue")!!)
        addCellToRow(row, equipment.equipmentSystem.systemNumber, getStyle("textValue")!!)
        addCellToRow(row, equipment.partNumber, getStyle("textValue")!!)
        addCellToRow(row, equipment.productDescription, getStyle("textValue")!!)
        addNumberCellToRow(row, equipment.qty)
        addCellToRow(row, equipment.uom, getStyle("textWithBoldBorderValue")!!)
        addColorPercentCellToRow(row, equipment.value1stRun)
        addColorPercentCellToRow(row, equipment.valueNextRuns)
        addCellToRow(row, equipment.revenueMode, getStyle("textValue")!!)
    }

    private fun addNumberCellToRow(row: Row, value: Any) {
        val c = addCellToRow(row, value, getStyle("textValue")!!)
        c.t = STCellType.N
    }

    private fun addPercentCellToRow(row: Row, value: Any) {
        val c = addCellToRow(row, value, getStyle("percentValue")!!)
        c.t = STCellType.N
    }

    private fun addColorPercentCellToRow(row: Row, value: Any) {
        val c = addCellToRow(row, value, getStyle("colorPercentValue")!!)
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