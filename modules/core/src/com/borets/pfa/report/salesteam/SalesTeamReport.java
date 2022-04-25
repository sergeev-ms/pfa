package com.borets.pfa.report.salesteam;

import com.borets.pfa.report.custom.CustomExcelReportTemplate;
import com.haulmont.yarg.formatters.CustomReport;
import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.Report;

import java.util.Map;

@SuppressWarnings("unused")
public class SalesTeamReport implements CustomReport {
    @Override
    public byte[] createReport(Report report, BandData rootBand, Map<String, Object> params) {

        CustomExcelReportTemplate reportHolder = new CustomExcelReportTemplate.Builder()
                .withTitle("Sales Team Report")
                .withReport(report)
                .withData(rootBand, "ReportData")
                .withParameters(params)
                .build(SalesTeamReportTemplateImpl.class);

        return reportHolder.getReport();
    }
}
