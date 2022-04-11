package com.borets.pfa.report.revenue;

import com.borets.pfa.entity.activity.RecordType;
import com.borets.pfa.report.custom.CustomExcelReportTemplate;
import com.haulmont.yarg.structure.BandData;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RevenueReportTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevenueReportTest.class);

    @Test
    void testCreateReport() throws IOException {

        Map<String, Object> params = new HashMap<>();
        params.put("mode", RecordType.FORECAST);
        params.put("startPeriod", new Date(121, Calendar.JANUARY, 11));
        params.put("endPeriod", new Date(122, Calendar.DECEMBER, 11));
        params.put("dateThreshold", new Date());

        BandData bandData = new BandData("ReportData");
        Map<String, Object> data = new HashMap<>();
        data.put("P", new Date(121, Calendar.JULY, 1));
        data.put("ACCOUNT_NAME", "Avad");
        data.put("ACCOUNT_TYPE", "Key");
        data.put("APPLICATION_TYPE", "Conventional");
        data.put("REVENUE", BigDecimal.TEN);
        data.put("REVENUE_TYPE_ORDER", Integer.valueOf(110));
        data.put("BUSINESS_MODEL", "Sale");
        data.put("REVENUE_TYPE_NAME", "Pull");
        data.put("PARENT_ACCOUNT_NAME", null);
        data.put("ACCOUNT_ORDER", Integer.valueOf(20));
        bandData.setData(data);

        RevenueReportTemplateImpl template = new CustomExcelReportTemplate.Builder()
                .withTemplate(this.getClass().getResourceAsStream("/com/borets/pfa/report/revenue/test_revenue_template.xlsx"))
                .withData(bandData)
                .withTitle("test")
                .withParameters(params)
                .build(RevenueReportTemplateImpl.class);

        byte[] report = template.getReport();

        assertNotNull(report);
//        File tempFile = File.createTempFile("pfa-report-test", ".xlsx");
//        new FileOutputStream(tempFile).write(report);
//        LOGGER.info(" libreoffice {}", tempFile.getAbsolutePath());
    }
}
