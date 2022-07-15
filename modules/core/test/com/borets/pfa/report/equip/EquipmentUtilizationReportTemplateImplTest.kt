package com.borets.pfa.report.equip

import com.borets.pfa.report.custom.CustomExcelReportTemplate
import com.borets.pfa.report.equip.dto.EquipmentItem
import com.borets.pfa.report.equip.dto.EquipmentSystem
import com.haulmont.yarg.structure.BandData
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.OS
import org.slf4j.LoggerFactory.getLogger
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.MathContext
import java.security.SecureRandom
import java.util.*

class EquipmentUtilizationReportTemplateImplTest {
    companion object {
        @JvmStatic
        private val LOGGER = getLogger(javaClass.enclosingClass)
    }

    @Test
    fun testCreateReport() {
        val years = mutableListOf(121, 122)
        val months = 0..11
        val dates = mutableListOf<Date>()

        years.forEach { y ->
            months.forEach { m ->
                dates.add(Date(y, m, 11))
            }
        }
        val params: MutableMap<String, Any> = HashMap()
        params["startPeriod"] = dates.first()
        params["endPeriod"] = dates.last()

        val rootBandData = BandData("rootBandData")

        val equipmentSystems = mutableListOf<EquipmentSystem>()
        val equipmentSystem1 = EquipmentSystem(
            "DiamondbackRUM",
            "Diamondback",
            1,
            "Rental",
            1
        )
        equipmentSystems.add(equipmentSystem1)

        equipmentSystem1.equipmentItems.add(
            EquipmentItem(
                equipmentSystem1,
                "10006707",
                "Bolt on discharge head, ESP B 400 2.875 EUE SS",
                BigDecimal.ONE,
                "ea",
                BigDecimal("0.10"),
                BigDecimal.ZERO,
                50010,
                "rental"
            )
        )
        equipmentSystem1.equipmentItems.add(
            EquipmentItem(
                equipmentSystem1,
                "10006707",
                "Second product description",
                BigDecimal.ONE,
                "ea",
                BigDecimal("0.10"),
                BigDecimal.ZERO,
                50020,
                "rental"
            )
        )

        val equipmentSystem2 = EquipmentSystem(
            "SystemNumberTwo",
            "CustomerOfSystem",
            2,
            "Sales",
            2
        )
        equipmentSystem2.equipmentItems.add(
            EquipmentItem(
                equipmentSystem2,
                "10006788",
                "Product description 1",
                BigDecimal.ONE,
                "ea",
                BigDecimal("0.41"),
                BigDecimal.ZERO,
                10010,
                "rental"
            )
        )
        equipmentSystem2.equipmentItems.add(
            EquipmentItem(
                equipmentSystem2,
                "10006900",
                "Product description 2",
                BigDecimal.ONE,
                "ea",
                BigDecimal("0.72"),
                BigDecimal.ZERO,
                10020,
                "rental"
            )
        )
        equipmentSystems.add(equipmentSystem2)


        equipmentSystems.forEach { s ->
            s.equipmentItems.forEach { e ->
                var order = 0
                val random = SecureRandom()
                // Activity values
                rootBandData.addChild(
                    createTestDataWithActivityValue(s, e, -123, "Design first", BigDecimal(random.nextInt(101)))
                )
                rootBandData.addChild(
                    createTestDataWithActivityValue(s, e, ++order, "Install newly drilled", BigDecimal(random.nextInt(101)))
                )
                rootBandData.addChild(
                    createTestDataWithActivityValue(s, e, ++order, "Install RUN >1", BigDecimal(random.nextInt(51)))
                )
                rootBandData.addChild(
                    createTestDataWithActivityValue(s, e, ++order, "Field service", BigDecimal(random.nextInt(51)))
                )
                rootBandData.addChild(
                    createTestDataWithActivityValue(s, e, ++order, "Pull", BigDecimal(random.nextInt(51)))
                )
                dates.forEach { d ->
                    rootBandData.addChild(
                        createTestDataWithPeriodValue(s, e, ++order, d, BigDecimal(random.nextInt(101)).divide(BigDecimal("100"), MathContext(2)))
                    )
                }
            }
        }

        val template: CustomExcelReportTemplate<EquipmentItem> = CustomExcelReportTemplate.Builder()
            .withTemplate(this.javaClass.getResourceAsStream("/com/borets/pfa/report/equip/Equipment_Utilization_Report_Template_1_2.xlsx"))
            .withData(rootBandData, CustomExcelReportTemplate.DATA_BAND_NAME)
            .withTitle("test")
            .withParameters(params)
            .withStyleDetection()
            .build(EquipmentUtilizationReportTemplateImpl::class.java)

        val report = template.report

        assertNotNull(report)
        val tempFile = File.createTempFile("pfa-report-test", ".xlsx")

        FileOutputStream(tempFile).use { os -> os.write(report) }
        LOGGER.info(" libreoffice {}", tempFile.absolutePath)
        if (OS.LINUX.isCurrentOs) {
            Runtime.getRuntime().exec(" libreoffice " + tempFile.absolutePath)
        } else if (OS.WINDOWS.isCurrentOs) {
            Runtime.getRuntime().exec("start excel \"" + tempFile.absolutePath + "\"")
        }
    }

    private fun createTestDataWithActivityValue(
        equipmentSystem: EquipmentSystem,
        equipmentItem: EquipmentItem,
        order: Int,
        activityName: String,
        value: Any
    ): BandData {
        val bandData = BandData(CustomExcelReportTemplate.DATA_BAND_NAME)
        val data: MutableMap<String, Any> = HashMap()
        fillEquipment(equipmentSystem, equipmentItem, data)
        data[CustomExcelReportTemplate.ORDER_FIELD] = order
        data[EquipmentUtilizationReportTemplateImpl.ACTIVITY_FIELD] = activityName
        data[CustomExcelReportTemplate.CELL_VALUE] = value
        bandData.data = data
        return bandData
    }

    private fun createTestDataWithPeriodValue(
        equipmentSystem: EquipmentSystem,
        equipmentItem: EquipmentItem,
        order: Int,
        date: Date,
        value: Any
    ): BandData {
        val bandData = BandData(CustomExcelReportTemplate.DATA_BAND_NAME)
        val data: MutableMap<String, Any> = HashMap()
        fillEquipment(equipmentSystem, equipmentItem, data)
        data[CustomExcelReportTemplate.PERIOD_FIELD] = date
        data[CustomExcelReportTemplate.ORDER_FIELD] = order
        data[CustomExcelReportTemplate.CELL_VALUE] = value
        bandData.data = data
        return bandData
    }

    private fun fillEquipment(equipmentSystem: EquipmentSystem, equipmentItem: EquipmentItem, data: MutableMap<String, Any>) {
        data[EquipmentSystem.REFERENCE_CODE] = equipmentSystem.referenceCode
        data[EquipmentSystem.CUSTOMER] = equipmentSystem.customer
        data[EquipmentItem.QTY] = equipmentItem.qty
        data[EquipmentItem.UOM] = equipmentItem.uom
        data[EquipmentSystem.RENTAL_OR_SALE] = equipmentSystem.rentalOrSale
        data[EquipmentItem.PART_NUMBER] = equipmentItem.partNumber
        data[EquipmentItem.PRODUCT_DESCRIPTION] = equipmentItem.productDescription
        data[EquipmentSystem.SYSTEM_NUMBER] = equipmentSystem.systemNumber
        data[EquipmentItem.FIRST_RUN] = equipmentItem.value1stRun
        data[EquipmentItem.NEXT_RUNS] = equipmentItem.valueNextRuns
    }

}