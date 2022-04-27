package com.borets.pfa.report.pricelist;

import com.borets.pfa.entity.activity.RecordType;
import com.borets.pfa.report.custom.CustomExcelReportTemplate;
import com.borets.pfa.report.revenue.RevenueReportTemplateImpl;
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

        BandData bandData = new BandData("ReportData");
        Map<String, Object> data = new HashMap<>();
        data.put("P", new Date(121, Calendar.JULY, 1));
        data.put("ACCOUNT_NAME", "Avad");
        data.put("PARENT_ACCOUNT_NAME", "Avad");
        data.put("ACCOUNT_TYPE", "Key");
        data.put("BUSINESS_MODEL", "Sale");
        data.put("APPLICATION_TYPE", "Conventional");
        data.put("ACCOUNT_ORDER", 20);
        data.put("VALUE_", BigDecimal.TEN);
        data.put("ORDER", 110);
        data.put("TITLE", "Downhole (Sales)##Install-None-Newly drilled");
        bandData.setData(data);
        rootBandData.addChild(bandData);

        bandData = new BandData("ReportData");
        data = new HashMap<>();
        data.put("P", new Date(121, Calendar.JULY, 1));
        data.put("ACCOUNT_NAME", "Avad");
        data.put("PARENT_ACCOUNT_NAME", "Avad");
        data.put("ACCOUNT_TYPE", "Key");
        data.put("BUSINESS_MODEL", "Sale");
        data.put("APPLICATION_TYPE", "Conventional");
        data.put("ACCOUNT_ORDER", 20);
        data.put("VALUE_", BigDecimal.ONE);
        data.put("ORDER", 111);
        data.put("TITLE", "Downhole (Sales)##Install-CompetitorWell-Run>1");
        bandData.setData(data);
        rootBandData.addChild(bandData);

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

}
