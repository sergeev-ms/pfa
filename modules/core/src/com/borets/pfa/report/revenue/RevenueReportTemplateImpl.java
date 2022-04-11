package com.borets.pfa.report.revenue;

import com.borets.pfa.entity.activity.RecordType;
import com.borets.pfa.report.custom.Account;
import com.borets.pfa.report.custom.ReportCell;
import com.borets.pfa.report.custom.CustomExcelReportTemplate;
import com.borets.pfa.report.custom.HorizontalPosition;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.yarg.formatters.impl.xlsx.CellReference;
import com.haulmont.yarg.formatters.impl.xlsx.Document;
import com.haulmont.yarg.structure.BandData;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.sml.CTMergeCell;
import org.xlsx4j.sml.CTMergeCells;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.STCellType;
import org.xlsx4j.sml.SheetData;
import org.xlsx4j.sml.Worksheet;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Revenue Report data and logic container.
 */
public class RevenueReportTemplateImpl extends CustomExcelReportTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevenueReportTemplateImpl.class);

    private static final int ACCOUNT_INFO_COLUMNS = 5;

    private static final String PARAMS_THRESHOLD_DATE = "dateThreshold";
    private static final String PARAMS_MODE = "mode";

    private static final String DATA_BAND_NAME = "ReportData";

    private static final String ACCOUNT_TYPE_FIELD = "ACCOUNT_TYPE";
    private static final String ACCOUNT_ORDER_FIELD = "ACCOUNT_ORDER";
    private static final String ACCOUNT_PARENT_FIELD = "PARENT_ACCOUNT_NAME";
    private static final String ACCOUNT_CUSTOMER_FIELD = "ACCOUNT_NAME";
    private static final String ACCOUNT_BUSINESS_MODEL_FIELD = "BUSINESS_MODEL";
    private static final String ACCOUNT_APPLICATION_TYPE_FIELD = "APPLICATION_TYPE";

    private static final String REVENUE_TYPE_NAME_FIELD = "REVENUE_TYPE_NAME";
    private static final String REVENUE_TYPE_ORDER_FIELD = "REVENUE_TYPE_ORDER";
    private static final String REVENUE_PERIOD_FIELD = "P";

    private static final String REVENUE_VALUE = "REVENUE";

    private static final int ROW_DECORATIVE_IDX = 1;
    private static final int ROW_HEADER_IDX = 2;
    private static final int ROW_SUBHEADER_IDX = 3;
    private static final int ROW_SUMMARY_IDX = 4;

    private static final int COL_HEADER_ODD_STYLE_IDX = 0;
    private static final int COL_HEADER_EVEN_STYLE_IDX = 3;
    private static final int COL_REFERENCE_HEADER_IDX = 3;
    private static final int COL_REFERENCE_SUBHEADER_IDX = 0;
    private static final int CELL_MERGE_DECORATIVE_BEGIN_IDX = 4;

    private static final int SIGN_REPORT_ROW = 0;
    private static final int SIGN_REPORT_COL = 3;

    private final Map<Account, List<HorizontalPosition>> coordinates = new HashMap<>();
    private final List<ReportCell> reportCells = new ArrayList<>();

    // grouping by revenue type
    private final OrderedBidiMap<String, Integer> revenueTypeOrders = new TreeBidiMap<>();
    private final List<Date> dates = new ArrayList<>();

    @Override
    protected void preProcessDataElement(String bandName, BandData dataElement) {
        Map<String, Object> data = dataElement.getData();
        String revenueType = (String) data.get(REVENUE_TYPE_NAME_FIELD);
        // map revenue types to their orders
        revenueTypeOrders.computeIfAbsent(revenueType, String -> (Integer) data.get(REVENUE_TYPE_ORDER_FIELD));
        // get distinct dates of report
        Date d = (Date) data.get(REVENUE_PERIOD_FIELD);
        if (!dates.contains(d)) {
            dates.add(d);
        }
    }

    @Override
    protected void afterPreProcess() {
        // Dates must be sorted in natural order
        Collections.sort(dates);
    }

    @Override
    protected void processDataElement(String bandName, BandData dataElement) {
        if (DATA_BAND_NAME.equals(bandName)) {
            Account x = getAccount(dataElement);
            coordinates.computeIfAbsent(x, Account -> new ArrayList<>());
            List<HorizontalPosition> horizontalPositions = coordinates.get(x);
            HorizontalPosition y = getHorizontalPosition(dataElement);
            if (!horizontalPositions.contains(y)) {
                horizontalPositions.add(y);
            }
            // Getting existing objects instead of newly created
            for (Account a :
                    coordinates.keySet()) {
                if (a.equals(x)) {
                    x = a;
                    break;
                }
            }
            for (HorizontalPosition a :
                    coordinates.get(x)) {
                if (a.equals(y)) {
                    y = a;
                    break;
                }
            }
            // Create new cell
            ReportCell reportCell = ReportCell.newDigit(dataElement.getData().getOrDefault(REVENUE_VALUE, 0), x, y);
            reportCells.add(reportCell);
        }
    }

    @Override
    protected void generateReport() throws Docx4JException {

        Document.SheetWrapper sheetWrapper = document.getWorksheets().get(0);
        WorksheetPart worksheetPart = sheetWrapper.getWorksheet();
        Worksheet contents = worksheetPart.getContents();
        SheetData sheetData = contents.getSheetData();
        List<Row> rows = sheetData.getRow();

        Row referenceRow = rows.get(rows.size() - 1);

        // Sorted accounts
        List<Account> orderedAccounts = coordinates.keySet().stream()
                .sorted(Comparator.comparing(Account::getOrder)
                        .thenComparing(Account::getParent, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Account::getCustomer))
                .collect(Collectors.toList());

        // Headers
        Row decorativeRow = rows.get(ROW_DECORATIVE_IDX);
        Row headerRow = rows.get(ROW_HEADER_IDX);
        long oddHeaderStyle = headerRow.getC().get(COL_HEADER_ODD_STYLE_IDX).getS();
        long evenHeaderStyle = headerRow.getC().get(COL_HEADER_EVEN_STYLE_IDX).getS();

        Row subheaderRow = rows.get(ROW_SUBHEADER_IDX);
        CTMergeCells mergeCells = contents.getMergeCells();
        int columnGroupIndex = 0;
        BidiMap<Integer, String> ordersToRevenues = revenueTypeOrders.inverseBidiMap();
        for (Date date : dates) {
            long style = ++columnGroupIndex % 2 == 0 ? evenHeaderStyle : oddHeaderStyle;
            boolean firstColumnForDate = true;
            CellReference mergeBegin = null;
            CellReference mergeEnd = null;
            for (Integer order :
                    ordersToRevenues.keySet()) {
                String revenueTypeName = ordersToRevenues.get(order);

                Cell subheaderCell = createFormattedCell(subheaderRow.getC().get(COL_REFERENCE_SUBHEADER_IDX));
                subheaderCell.setT(STCellType.STR);
                subheaderCell.setV(revenueTypeName);
                subheaderRow.getC().add(subheaderCell);

                Cell headerCell = createFormattedCell(headerRow.getC().get(COL_REFERENCE_HEADER_IDX));
                headerCell.setParent(headerRow);
                headerCell.setT(STCellType.STR);
                headerCell.setS(style);
                headerRow.getC().add(headerCell);

                if (firstColumnForDate) {
                    headerCell.setV(new SimpleDateFormat("yyyy - MM", Locale.US).format(date));
                    mergeBegin = new CellReference(sheetWrapper.getName(), Math.toIntExact(headerRow.getR()), headerRow.getC().size());
                }
                firstColumnForDate = false;
                mergeEnd = new CellReference(sheetWrapper.getName(), Math.toIntExact(headerRow.getR()), headerRow.getC().size());
            }
            CTMergeCell mc = new CTMergeCell();
            mc.setRef(mergeBegin.toReference() + ":" + mergeEnd.toReference());
            mergeCells.getMergeCell().add(mc);

            mc = new CTMergeCell();
            CellReference decorativeBegin = new CellReference(sheetWrapper.getName(), Math.toIntExact(decorativeRow.getR()), CELL_MERGE_DECORATIVE_BEGIN_IDX);
            CellReference decorativeEnd = new CellReference(sheetWrapper.getName(), Math.toIntExact(decorativeRow.getR()), subheaderRow.getC().size());
            mc.setRef(decorativeBegin.toReference() + ":" + decorativeEnd.toReference());
            mergeCells.getMergeCell().add(mc);

        }

        for (Account account :
                orderedAccounts) {
            Row row = createFormattedRow(referenceRow);
            row.setR((long) rows.size());
            rows.add(row);

            addAccountInfo(row, account);

            // iterating over all data columns
            for (Date date : dates) {
                for (Integer order :
                        ordersToRevenues.keySet()) {
                    String revenueTypeName = ordersToRevenues.get(order);
                    // account, revenueTypeName, date

                    List<Cell> c = row.getC();

                    Cell dataCell = new Cell();
                    dataCell.setParent(row);
                    dataCell.setT(STCellType.N);
                    c.add(dataCell);
                    dataCell.setV("0");

                    List<HorizontalPosition> existingCoords = coordinates.get(account);
                    for (HorizontalPosition horcoord : existingCoords) {

                        if (date.equals(horcoord.getDate()) && revenueTypeName.equals(horcoord.getName())) {
                            // must be existed in cells
                            ReportCell reportCell = findCell(account, revenueTypeName, date);
                            if (reportCell == null) {
                                throw new NullPointerException("Cell not found! account = "
                                        + account + ", revenue type = " + revenueTypeName + ", date = " + date);
                            }
                            dataCell.setV(reportCell.getValueNumber().toString());
                            break;
                        }
                    }
                }
            }
        }

        // Summary
        Row summaryRow = rows.get(ROW_SUMMARY_IDX);

        for (Date date : dates) {
            for (Integer order :
                    ordersToRevenues.keySet()) {
                String revenueTypeName = ordersToRevenues.get(order);
                BigDecimal total = BigDecimal.ZERO;

                for (Account account : orderedAccounts) {
                    ReportCell cell = findCell(account, revenueTypeName, date);
                    if (cell != null) {
                        total = total.add(cell.getValueNumber());
                    }
                }
                Cell totalCell = createFormattedCell(summaryRow.getC().get(summaryRow.getC().size() - 1));
                totalCell.setV(total.toString());
                summaryRow.getC().add(totalCell);
            }
        }

        signReport(rows.get(SIGN_REPORT_ROW).getC().get(SIGN_REPORT_COL), (Date) params.get(PARAMS_THRESHOLD_DATE), (RecordType) params.get(PARAMS_MODE));
    }

    private static void addAccountInfo(Row row, Account account) {
        List<Cell> c = row.getC();

        // account info
        for (int i = 0; i < ACCOUNT_INFO_COLUMNS; ++i) {
            Cell accountCell = new Cell();
            accountCell.setParent(row);
            accountCell.setT(STCellType.STR);
            switch (i) {
                case 0:
                    accountCell.setV(account.getType());
                    break;
                case 1:
                    accountCell.setV(account.getParent());
                    break;
                case 2:
                    accountCell.setV(account.getCustomer());
                    break;
                case 3:
                    accountCell.setV(account.getBusinessModel());
                    break;
                case 4:
                    accountCell.setV(account.getApplicationType());
                    break;
                default:
                    accountCell.setV("");
                    break;
            }
            c.add(accountCell);
        }
    }

    private @Nullable
    ReportCell findCell(Account account, String revenueTypeName, Date date) {
        return reportCells.stream().filter(dc -> account.equals(dc.getCoordinates().getRow())
                && revenueTypeName.equals(dc.getCoordinates().getColumn().getName())
                && date.equals(dc.getCoordinates().getColumn().getDate())
        ).findFirst().orElse(null);
    }

    private Row createFormattedRow(Row referenceRow) {
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

    private Cell createFormattedCell(Cell referenceCell) {
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

    private static Account getAccount(BandData dataElement) {
        Map<String, Object> data = dataElement.getData();
        return new Account(
                (String) data.getOrDefault(ACCOUNT_TYPE_FIELD, "---"),
                (String) data.get(ACCOUNT_PARENT_FIELD),
                (String) data.getOrDefault(ACCOUNT_CUSTOMER_FIELD, "---"),
                (String) data.getOrDefault(ACCOUNT_BUSINESS_MODEL_FIELD, "n/a"),
                (String) data.getOrDefault(ACCOUNT_APPLICATION_TYPE_FIELD, "n/a"),
                (Integer) data.getOrDefault(ACCOUNT_ORDER_FIELD, 0)
        );
    }

    private static HorizontalPosition getHorizontalPosition(BandData dataElement) {
        Map<String, Object> data = dataElement.getData();
        return new HorizontalPosition(
                (String) data.get(REVENUE_TYPE_NAME_FIELD),
                (Integer) data.get(REVENUE_TYPE_ORDER_FIELD),
                (Date) data.get(REVENUE_PERIOD_FIELD)
        );
    }

    private void signReport(Cell cell, Date dateThreshold, RecordType mode) {
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

}
