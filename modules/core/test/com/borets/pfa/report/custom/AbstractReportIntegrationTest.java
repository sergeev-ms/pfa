package com.borets.pfa.report.custom;

import com.borets.pfa.PfaTestContainer;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.global.ViewBuilder;
import com.haulmont.cuba.testsupport.TestContainer;
import com.haulmont.reports.ReportingApi;
import com.haulmont.reports.entity.Report;
import com.haulmont.yarg.formatters.CustomReport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public abstract class AbstractReportIntegrationTest {

    @RegisterExtension
    public static TestContainer cont = PfaTestContainer.Common.Companion.getINSTANCE();

    protected static DataManager dataManager;

    protected static ReportingApi reportingApi;

    @BeforeAll
    static void beforeAll() {
        dataManager = Objects.requireNonNull(AppBeans.get(DataManager.class));
        reportingApi = Objects.requireNonNull(AppBeans.get(ReportingApi.class));
    }

    protected <T extends CustomReport> Report loadCustomReport(Class<T> customReportClass) {
        List<Report> reports = dataManager.load(Report.class)
                .view(ViewBuilder.of(Report.class).addView(View.BASE).add("defaultTemplate", View.BASE).build())
                .list();

        return reports.stream()
                .filter(r -> r.getDefaultTemplate().isCustom() && customReportClass.getName().equals(r.getDefaultTemplate().getCustomDefinition()))
                .findAny().orElseThrow();
    }

    protected void open(File file) throws IOException {
        if (OS.LINUX.isCurrentOs()) {
            Runtime.getRuntime().exec(" libreoffice " + file.getAbsolutePath());
        } else if (OS.WINDOWS.isCurrentOs()) {
            Runtime.getRuntime().exec("start excel \"" + file.getAbsolutePath() + "\"");
        }
    }
}
