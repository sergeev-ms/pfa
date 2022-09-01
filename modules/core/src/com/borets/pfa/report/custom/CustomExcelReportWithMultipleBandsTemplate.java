package com.borets.pfa.report.custom;

import com.borets.pfa.entity.activity.RecordType;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.yarg.exception.ReportFormattingException;
import com.haulmont.yarg.formatters.impl.xlsx.CellReference;
import com.haulmont.yarg.formatters.impl.xlsx.Document;
import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.Report;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io3.Save;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.SpreadsheetML.SharedStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.sml.*;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiConsumer;

public abstract class CustomExcelReportWithMultipleBandsTemplate<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomExcelReportWithMultipleBandsTemplate.class);

    /**
     * Input data params.
     */
    public static final String DATA_BAND_NAME = "ReportData";

    protected static final String PARAMS_THRESHOLD_DATE = "dateThreshold";
    protected static final String PARAMS_MODE = "mode";
    protected static final String PARAMS_START_PERIOD = "startPeriod";
    protected static final String PARAMS_END_PERIOD = "endPeriod";

    protected final List<Date> dates = new ArrayList<>();
    protected final SortedSet<Column> columns = new TreeSet<>();

    protected String title;

    protected Document document; // TODO make private and accessor

    private boolean styleDetection;
    private final Map<String, Long> styles = new HashMap<>();

    protected Map<String, Object> params;

    protected void preProcessColumns(String bandName, BandData dataElement) {
        // Could be overridden in subclasses
    }

    /**
     * Usually is used for sorting
     * and combining data
     */
    protected void afterProcess() {
    }

    protected abstract void processDataElement(String bandName, BandData dataElement);

    protected abstract void generateReport(List<Document.SheetWrapper> sheetWrappers) throws Docx4JException;

    public byte[] getReport() {
        try {
            if (styleDetection) {
                try {
                    SharedStrings sharedStrings = (SharedStrings) document.getPackage().getParts()
                            .get(new PartName("/xl/sharedStrings.xml"));
                    List<CTRst> sharedStringsValues = sharedStrings.getJaxbElement().getSi();

                    for (Document.SheetWrapper sheetWrapper :
                            document.getWorksheets()) {
                        for (Row row :
                                sheetWrapper.getWorksheet().getContents().getSheetData().getRow()) {
                            for (Cell cell :
                                    row.getC()) {
                                if (StringUtils.isNotBlank(cell.getV()) && cell.getT() == STCellType.S) {
                                    int valuePos = Integer.parseInt(cell.getV());
                                    if (sharedStringsValues.size() > valuePos) {
                                        String value = sharedStringsValues.get(valuePos).getT().getValue();
                                        if (value.startsWith("{") && value.endsWith("}")) {
                                            String key = value.replace("{", "").replace("}", "");
                                            long style = cell.getS();
                                            styles.put(key, style);
                                            cell.setV(null);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.error("STYLE DETECTION IS ON: error thrown while autodetecting styles from cells", ex);
                }
            }
            generateReport(document.getWorksheets());
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

    protected void setStyleDetection(boolean styleDetection) {
        this.styleDetection = styleDetection;
    }

    protected static void setupAutoFilter(String sheetName, Worksheet contents, Row headerRow) {
        CellReference cr1 = new CellReference(sheetName, Math.toIntExact(headerRow.getR()), 1);
        CellReference cr2 = new CellReference(sheetName, Math.toIntExact(headerRow.getR()), headerRow.getC().size());
        CTAutoFilter autoFilter = contents.getAutoFilter();
        if (autoFilter == null) {
            autoFilter = new CTAutoFilter();
            contents.setAutoFilter(autoFilter);
        }
        autoFilter.setRef(cr1.toReference() + ":" + cr2.toReference());
    }

    protected static CellReference createCellRef(String sheetName, Row row, int col) {
        return new CellReference(sheetName, Math.toIntExact(row.getR()), col);
    }

    protected static CellReference createCellRef(String sheetName, Row row) {
        return new CellReference(sheetName, Math.toIntExact(row.getR()), row.getC().size());
    }

    protected static String getStrRef(CellReference begin, CellReference end) {
        return begin.toReference() + ":" + end.toReference();
    }

    protected static Row createFormattedRow(Row referenceRow) {
        Row row = Context.getsmlObjectFactory().createRow();
        row.setHt(referenceRow.getHt());
        row.setExtLst(referenceRow.getExtLst());
        row.setOutlineLevel(referenceRow.getOutlineLevel());
        row.setCustomFormat(referenceRow.isCustomFormat());
        row.setHidden(referenceRow.isHidden());
        row.setPh(referenceRow.isPh());
        row.setCollapsed(referenceRow.isCollapsed());
        row.setCustomHeight(referenceRow.isCustomHeight());
        row.setThickBot(referenceRow.isThickBot());
        row.setThickTop(referenceRow.isThickTop());
        row.setS(referenceRow.getS());
        return row;
    }

    protected static Cell createFormattedCellAppendingToRow(Row row) {
        Cell newCell = createFormattedCell(row.getC().get(row.getC().size() - 1));
        newCell.setParent(row);
        row.getC().add(newCell);
        return newCell;
    }

    protected static Cell createFormattedCell(Cell referenceCell) {
        Cell cell = Context.getsmlObjectFactory().createCell();
        cell.setCm(referenceCell.getCm());
        cell.setVm(referenceCell.getVm());
        cell.setF(referenceCell.getF());
        cell.setIs(referenceCell.getIs());
        cell.setT(referenceCell.getT());
        cell.setPh(referenceCell.isPh());
        cell.setS(referenceCell.getS());
        cell.setV(referenceCell.getV());
        cell.setParent(referenceCell.getParent());
        return cell;
    }

    protected static void createSummaryFormulaCell(String sheetName, List<Row> rows, int firstDataIndex, Row summaryRow, String formulaPrefix) {
        Row firstDataRow = rows.get(firstDataIndex);
        Cell totalCell = createFormattedCellAppendingToRow(summaryRow);
        if (StringUtils.isNotBlank(formulaPrefix)) {
            CellReference x1 = createCellRef(sheetName, firstDataRow, summaryRow.getC().size());
            CellReference x2 = new CellReference(sheetName, Math.toIntExact(rows.size() - 1), summaryRow.getC().size());
            CTCellFormula formula = new CTCellFormula();
            formula.setValue(formulaPrefix + getStrRef(x1, x2) + ")");
            totalCell.setF(formula);
        }
    }

    protected static void signReport(Cell cell, Date dateThreshold, RecordType mode) {
        String userName = "unidentified";
        try {
            userName = AppBeans.get(UserSessionSource.class).getUserSession().getUser().getName();
        } catch (Exception ex) {
            LOGGER.error("Could not get user's name from userSession", ex);
        }
        String modeStr = "n/a";
        try {
            modeStr = AppBeans.get(Messages.class).getMessage(mode);
        } catch (IllegalStateException ex) {
            LOGGER.error("Could not get string for mode", ex);
        }
        String sb = "generated by " +
                userName + " at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm", Locale.US)) +
                " (mode: \"" + modeStr + '"' +
                ", threshold: " + new SimpleDateFormat("dd.MM.yy", Locale.US).format(dateThreshold) + ")";

        cell.setT(STCellType.STR);
        cell.setV(sb);

        LOGGER.info("SalesTeamReport " + sb);
    }

    protected @Nullable Long getStyle(String styleName) {
        return styles.get(styleName);
    }

    public static class Builder {

        private String title = "";

        private String template = "DEFAULT";

        private Report report;

        private InputStream templateInputStream;

        private Map<String, Object> parameters = Collections.EMPTY_MAP;

        private Map<String, List<BandData>> data = new HashMap<>();

        private boolean styleDetection;
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

        public Builder withStyleDetection() {
            this.styleDetection = true;
            return this;
        }
        public <T extends CustomExcelReportWithMultipleBandsTemplate> T build(Class<T> clazz) {
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
                instance.setStyleDetection(styleDetection);

                instance.preProcess();
                iterateOverBandData(List.of(instance::preProcessColumns, instance::processDataElement));
                instance.afterProcess();

                return instance;
            } catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                throw new ReportFormattingException("Exception thrown while generating report \"" + title + "\"", ex);
            } catch (NoSuchMethodException ex) {
                throw new ReportFormattingException("Class \"" + clazz.getSimpleName() +"\" must have appropriate " +
                        "constructor (String, InputStream)", ex);
            }
        }

        private void iterateOverBandData(List<BiConsumer<String, BandData>> forEachDataElement) {
            for (Map.Entry<String, List<BandData>> dataEntry : data.entrySet()) {
                String bandName = dataEntry.getKey();
                for (BandData bandData : dataEntry.getValue()) {
                    forEachDataElement.forEach(stringBandDataBiConsumer ->
                            stringBandDataBiConsumer.accept(bandName, bandData));

                    bandData.getChildrenBands().forEach((subBandName, subBandDataList) ->
                            subBandDataList.forEach(subBandData ->
                                            forEachDataElement.get(0).accept(subBandName, subBandData))); // TODO: 23.08.2022 fix hardcode //fix hardcode

                }
            }
        }
    }

    protected void preProcess() {
        Date startPeriod = ((Date) params.get(PARAMS_START_PERIOD));
        Date endPeriod = ((Date) params.get(PARAMS_END_PERIOD));

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(startPeriod);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        while (calendar.getTime().before(endPeriod)) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.MONTH, 1);
        }
    };
}
