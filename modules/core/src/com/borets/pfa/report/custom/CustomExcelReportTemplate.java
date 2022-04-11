package com.borets.pfa.report.custom;

import com.haulmont.yarg.exception.ReportFormattingException;
import com.haulmont.yarg.formatters.impl.xlsx.Document;
import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.Report;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io3.Save;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public abstract class CustomExcelReportTemplate {

    protected String title;

    protected Document document;

    protected Map<String, Object> params;

    protected void preProcessDataElement(String bandName, BandData dataElement) {
        // Could be overridden in subclasses
    }

    protected void afterPreProcess() {
        // Could be overridden in subclasses
    }

    protected abstract void processDataElement(String bandName, BandData dataElement);

    protected abstract void generateReport() throws Docx4JException;

    public byte[] getReport() {
        try {
            generateReport();
        } catch (Docx4JException ex) {
            throw new ReportFormattingException("Exception thrown while generating report " + title, ex);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Save save = new Save(document.getPackage());
            save.save(outputStream);
            outputStream.flush();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new ReportFormattingException("Exception thrown while saving report " + title + " to byte array", ex);
        }
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    protected void setDocument(Document document) {
        this.document = document;
    }

    protected void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public static class Builder {

        private String title = "";

        private String template = "DEFAULT";

        private Report report;

        private InputStream templateInputStream;

        private Map<String, Object> parameters = Collections.EMPTY_MAP;

        private Map<String, List<BandData>> data = new HashMap<>();

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withTemplateName(String template) {
            this.template = template;
            return this;
        }

        public Builder withReport(Report report) {
            this.report = report;
            return this;
        }

        public Builder withTemplate(InputStream templateInputStream) {
            this.templateInputStream = templateInputStream;
            return this;
        }

        public Builder withParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder withData(BandData rootBand, String bandName) {
            Objects.requireNonNull(rootBand);
            this.data.put(Objects.requireNonNull(bandName), Objects.requireNonNull(rootBand.getChildrenByName(bandName)));
            return this;
        }

        public Builder withData(String bandName, Collection<BandData> bandData) {
            this.data.put(Objects.requireNonNull(bandName), new ArrayList<>(Objects.requireNonNull(bandData)));
            return this;
        }

        public Builder withData(BandData bandData) {
            Objects.requireNonNull(bandData);
            this.data.put(Objects.requireNonNull(bandData.getName()), Collections.singletonList(bandData));
            return this;
        }

        public <T extends CustomExcelReportTemplate> T build(Class<T> clazz) {
            if (report != null && templateInputStream != null) {
                throw new ReportFormattingException("Ambiguous input parameters: use report or templateInputStream but not both");
            }
            if (report == null && templateInputStream == null) {
                throw new ReportFormattingException("Neither report nor templateInputStream were set");
            }
            try (InputStream is = report != null
                    ? report.getReportTemplates().get(template).getDocumentContent()
                    : templateInputStream) {
                Constructor<T> ctor = clazz.getDeclaredConstructor();
                T instance = ctor.newInstance();
                try {
                    instance.setDocument(Document.create(SpreadsheetMLPackage.load(is)));
                } catch (Docx4JException ex) {
                    throw new RuntimeException("Could not instantiate report holder for report \"" + title + "\"", ex);
                }
                instance.setParams(Objects.requireNonNull(parameters));
                instance.setTitle(Objects.requireNonNull(title));
                iterateOverBandData(instance::preProcessDataElement);
                instance.afterPreProcess();
                iterateOverBandData(instance::processDataElement);

                return instance;
            } catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                throw new ReportFormattingException("Exception thrown while generating report \"" + title + "\"", ex);
            } catch (NoSuchMethodException ex) {
                throw new ReportFormattingException("Class \"" + clazz.getSimpleName() +"\" must have appropriate " +
                        "constructor (String, InputStream)", ex);
            }
        }

        private void iterateOverBandData(BiConsumer<String, BandData> forEachDataElement) {
            for (Map.Entry<String, List<BandData>> dataEntry : data.entrySet()) {
                String bandName = dataEntry.getKey();
                for (BandData bandData :
                        dataEntry.getValue()) {
                    forEachDataElement.accept(bandName, bandData);
                }
            }
        }
    }
}
