package com.borets.pfa.report.pricelist;

import com.borets.pfa.entity.activity.RecordType;
import com.borets.pfa.report.custom.Account;
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
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PriceListReportTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceListReportTest.class);

    @Disabled
    @Test
    void createReport() throws IOException {

        Map<String, Object> params = new HashMap<>();
        params.put("mode", RecordType.FORECAST);
        params.put("startPeriod", new Date(121, Calendar.JANUARY, 11));
        params.put("endPeriod", new Date(122, Calendar.DECEMBER, 11));
        params.put("dateThreshold", new Date());

        BandData rootBandData = new BandData("rootBandData");
        rootBandData.addChild(createTestData((Date) params.get("startPeriod"), 110, "Downhole (Sales)##Install-None-Newly drilled"));
        rootBandData.addChild(createTestData((Date) params.get("startPeriod"), 111, "Downhole (Sales)##Install-CompetitorWell-Run>1"));
        rootBandData.addChild(createTestData((Date) params.get("startPeriod"), 112, "Downhole (Sales)##Some analytic title"));
        rootBandData.addChild(createTestData((Date) params.get("startPeriod"), 209, "Another revenue##Another analytic"));
        rootBandData.addChild(createTestData((Date) params.get("endPeriod"), 110, "Downhole (Sales)##Install-None-Newly drilled"));
        rootBandData.addChild(createTestData((Date) params.get("endPeriod"), 111, "Downhole (Sales)##Install-CompetitorWell-Run>1"));
        rootBandData.addChild(createTestData((Date) params.get("endPeriod"), 112, "Downhole (Sales)##Some analytic title"));
        rootBandData.addChild(createTestData((Date) params.get("endPeriod"), 209, "Another revenue##Another analytic"));

        CustomExcelReportTemplate template = new CustomExcelReportTemplate.Builder()
                .withTemplate(this.getClass().getResourceAsStream("/com/borets/pfa/report/pricelist/Borets_Template_PriceListReport_1.xlsx"))
                .withData(rootBandData, "ReportData")
                .withTitle("test")
                .withParameters(params)
                .withStyleDetection()
                .build(PriceListReportTemplateImpl.class);

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

    private BandData createTestData(Date date, int order, String title) {
        BandData bandData = new BandData("ReportData");
        Map<String, Object> data = new HashMap<>();
        data.put("P", date);
        data.put(Account.ACCOUNT_CUSTOMER_FIELD, "Avad");
        data.put(Account.ACCOUNT_PARENT_FIELD, "Avad");
        data.put(Account.ACCOUNT_TYPE_FIELD, "Key");
        data.put(Account.ACCOUNT_BUSINESS_MODEL_FIELD, "Sale");
        data.put(Account.ACCOUNT_APPLICATION_TYPE_FIELD, "Conventional");
        data.put(Account.ACCOUNT_ORDER_FIELD, 20);
        data.put("VALUE_", BigDecimal.valueOf(new Random().nextInt(100000)));
        data.put("ORDER", order);
        data.put("TITLE", title);
        bandData.setData(data);
        return bandData;
    }

}
