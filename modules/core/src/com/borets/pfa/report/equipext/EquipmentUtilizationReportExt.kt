package com.borets.pfa.report.equipext

import com.borets.pfa.report.custom.CustomExcelReportTemplate
import com.borets.pfa.report.equipext.dto.EquipmentItem
import com.haulmont.yarg.formatters.CustomReport
import com.haulmont.yarg.structure.BandData
import com.haulmont.yarg.structure.Report

class EquipmentUtilizationReportExt : CustomReport {
    override fun createReport(report: Report, rootBand: BandData, params: MutableMap<String, Any>): ByteArray {
        val reportHolder: CustomExcelReportTemplate<EquipmentItem> = CustomExcelReportTemplate.Builder()
            .withTitle("Equipment Utilization Report")
            .withReport(report)
            .withData(rootBand, "ReportData")
            .withParameters(params)
            .withStyleDetection()
            .build(EquipmentUtilizationReportExtTemplateImpl::class.java)

        return reportHolder.report
    }
}