package com.borets.pfa.report.equip

import com.borets.pfa.report.custom.CustomExcelReportTemplate
import com.borets.pfa.report.equip.dto.EquipmentItem
import com.haulmont.yarg.formatters.CustomReport
import com.haulmont.yarg.structure.BandData
import com.haulmont.yarg.structure.Report

class EquipmentUtilizationReport : CustomReport {
    override fun createReport(report: Report, rootBand: BandData, params: MutableMap<String, Any>): ByteArray {
        val reportHolder: CustomExcelReportTemplate<EquipmentItem> = CustomExcelReportTemplate.Builder()
            .withTitle("Equipment Utilization Report")
            .withReport(report)
            .withData(rootBand, "ReportData")
            .withParameters(params)
            .withStyleDetection()
            .build(EquipmentUtilizationReportTemplateImpl::class.java)

        return reportHolder.report
    }
}