package com.borets.pfa.report.pricelist;

import com.borets.pfa.entity.activity.RecordType;
import com.borets.pfa.report.custom.AbstractReportIntegrationTest;
import com.borets.pfa.report.revenue.RevenueReport;
import com.haulmont.reports.entity.Report;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PriceListReportIntegrationTest extends AbstractReportIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceListReportIntegrationTest.class);

    @Disabled
    @Test
    void createReport() throws IOException {
        Report report = loadCustomReport(PriceListReport.class);

        Map<String, Object> params = new HashMap<>();
        params.put("mode", RecordType.FORECAST);
        params.put("startPeriod", new Date(121, Calendar.JANUARY, 11));
        params.put("endPeriod", new Date(122, Calendar.DECEMBER, 11));
        params.put("dateThreshold", new Date());

        ReportOutputDocument reportOutputDocument = reportingApi.createReport(report, params);

        File tempFile = File.createTempFile("pfa-pricelist-report-test", ".xlsx");
        try (OutputStream os = new FileOutputStream(tempFile)) {
            os.write(reportOutputDocument.getContent());
        }
        LOGGER.info(" libreoffice {}", tempFile.getAbsolutePath());
        open(tempFile);
    }
}