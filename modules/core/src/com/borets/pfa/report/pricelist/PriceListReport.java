package com.borets.pfa.report.pricelist;

import com.borets.pfa.report.custom.CustomExcelReportTemplate;
import com.haulmont.yarg.formatters.CustomReport;
import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.Report;

import java.util.Map;

@SuppressWarnings("unused")
public class PriceListReport implements CustomReport {
    @Override
    public byte[] createReport(Report report, BandData rootBand, Map<String, Object> params) {
        CustomExcelReportTemplate reportHolder = new CustomExcelReportTemplate.Builder()
                .withTitle("Price List Report")
                .withReport(report)
                .withData(rootBand, "ReportData")
                .withParameters(params)
                .withStyleDetection()
                .build(PriceListReportTemplateImpl.class);

        return reportHolder.getReport();
    }
}
