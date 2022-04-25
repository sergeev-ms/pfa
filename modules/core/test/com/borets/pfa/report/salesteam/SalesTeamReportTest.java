package com.borets.pfa.report.salesteam;

import com.borets.pfa.entity.activity.RecordType;
import com.borets.pfa.report.custom.CustomExcelReportTemplate;
import com.haulmont.yarg.structure.BandData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SalesTeamReportTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesTeamReportTest.class);

    @Test
    @Disabled
    void createReport() throws IOException {

        Date startPeriod = new Date(121, Calendar.JANUARY, 11);
        Date endPeriod = new Date(122, Calendar.DECEMBER, 11);
        Map<String, Object> params = new HashMap<>();
        params.put("mode", RecordType.FORECAST);
        params.put("startPeriod", startPeriod);
        params.put("endPeriod", endPeriod);
        params.put("dateThreshold", new Date());

        CustomExcelReportTemplate template = new CustomExcelReportTemplate.Builder()
                .withTemplate(this.getClass().getResourceAsStream("/com/borets/pfa/report/salesteam/Borets_Template_SalesTeamReport_7.xlsx"))
                .withData(DataUtils.generateData(startPeriod))
                .withTitle("test")
                .withParameters(params)
                .build(SalesTeamReportTemplateImpl.class);

        byte[] report = template.getReport();

        assertNotNull(report);

        File tempFile = File.createTempFile("pfa-report-test", ".xlsx");
        try (OutputStream os = new FileOutputStream(tempFile)) {
            os.write(report);
        }
        LOGGER.info(" libreoffice {}", tempFile.getAbsolutePath());
        if (OS.LINUX.isCurrentOs()) {
            Runtime.getRuntime().exec(" libreoffice " + tempFile.getAbsolutePath());
        } else if (OS.WINDOWS.isCurrentOs()) {
            Runtime.getRuntime().exec("start excel \"" + tempFile.getAbsolutePath() + "\"");
        }
    }

    private static class DataUtils {

        // TODO
        private static BandData generateData(Date startPeriod) {
            BandData bandData = new BandData("ReportData");
            Map<String, Object> data = new HashMap<>();
            data.put("P", startPeriod);
            data.put("ACCOUNT_NAME", "Avad");
            data.put("ACCOUNT_TYPE", "Key");
            data.put("APPLICATION_TYPE", "Conventional");
            data.put("VALUE_", BigDecimal.TEN);
            data.put("BUSINESS_MODEL", "Sale");
            data.put("ANALYTIC_TITLE", "ActiveWells");
            data.put("ANALYTIC_ORDER", 60);
            data.put("PARENT_ACCOUNT_NAME", null);
            data.put("ACCOUNT_ORDER", 20);
            data.put("IS_DELETED", 0);
            bandData.setData(data);
            return bandData;
        }

    }
}