package com.borets.pfa.report.revenue;

import com.borets.pfa.PfaTestContainer;
import com.borets.pfa.entity.activity.RecordType;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.global.ViewBuilder;
import com.haulmont.cuba.testsupport.TestContainer;
import com.haulmont.reports.ReportingApi;
import com.haulmont.reports.entity.Report;
import com.haulmont.yarg.formatters.CustomReport;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RevenueReportIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevenueReportIntegrationTest.class);

    @RegisterExtension
    public static TestContainer cont = PfaTestContainer.Common.Companion.getINSTANCE();

    static DataManager dataManager;

    static ReportingApi reportingApi;

    @BeforeAll
    static void beforeAll() {
        dataManager = Objects.requireNonNull(AppBeans.get(DataManager.class));
        reportingApi = Objects.requireNonNull(AppBeans.get(ReportingApi.class));
    }

    //@org.junit.jupiter.api.Disabled
    @Test
    void createReport() throws IOException {
        Report report = loadCustomReport(RevenueReport.class);

        Map<String, Object> params = new HashMap<>();
        params.put("mode", RecordType.FORECAST);
        params.put("startPeriod", new Date(121, Calendar.JANUARY, 11));
        params.put("endPeriod", new Date(122, Calendar.DECEMBER, 11));
        params.put("dateThreshold", new Date());

        ReportOutputDocument reportOutputDocument = reportingApi.createReport(report, params);

        File tempFile = File.createTempFile("pfa-report-test", ".xlsx");
        new FileOutputStream(tempFile).write(reportOutputDocument.getContent());
        LOGGER.info(" libreoffice {}", tempFile.getAbsolutePath());
        if (OS.LINUX.isCurrentOs()) {
            Runtime.getRuntime().exec(" libreoffice " + tempFile.getAbsolutePath());
        }
    }

    private <T extends CustomReport> Report loadCustomReport(Class<T> customReportClass) {
        List<Report> reports = dataManager.load(Report.class)
                .view(ViewBuilder.of(Report.class).addView(View.BASE).add("defaultTemplate", View.BASE).build())
                .list();

        return reports.stream()
                .filter(r -> r.getDefaultTemplate().isCustom() && customReportClass.getName().equals(r.getDefaultTemplate().getCustomDefinition()))
                .findAny().orElseThrow();
    }

}