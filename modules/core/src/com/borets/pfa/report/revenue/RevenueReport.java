package com.borets.pfa.report.revenue;

import com.borets.pfa.report.custom.CustomExcelReportTemplate;
import com.haulmont.yarg.formatters.CustomReport;
import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.Report;

import java.util.Map;

/**
 * Revenue Report wrapper for Cuba Yarg engine.
 */
@SuppressWarnings("unused")
public class RevenueReport implements CustomReport {
    @Override
    public byte[] createReport(Report report, BandData rootBand, Map<String, Object> params) {

        RevenueReportTemplateImpl reportHolder = new CustomExcelReportTemplate.Builder()
                .withTitle("Revenue Report")
                .withReport(report)
                .withData(rootBand, "ReportData")
                .withParameters(params)
                .build(RevenueReportTemplateImpl.class);

        return reportHolder.getReport();
    }
}
