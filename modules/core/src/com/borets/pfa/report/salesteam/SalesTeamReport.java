package com.borets.pfa.report.salesteam;

import com.haulmont.yarg.exception.ReportFormattingException;
import com.haulmont.yarg.formatters.CustomReport;
import com.haulmont.yarg.formatters.impl.xlsx.CellReference;
import com.haulmont.yarg.formatters.impl.xlsx.Document;
import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.Report;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io3.Save;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.sml.CTMergeCell;
import org.xlsx4j.sml.CTMergeCells;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Col;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.STCellType;
import org.xlsx4j.sml.SheetData;
import org.xlsx4j.sml.Worksheet;

import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class SalesTeamReport implements CustomReport {

    private static final int ACCOUNT_COLUMN_COUNT = 7;

    private Document document;
    private CTMergeCells mergeCells;
    private Worksheet worksheet;
    private String sheetName;
    private List<Row> rows;
    private Row dataRow;
    private Row upperDecorativeRow;
    private Row periodsRow;
    private Row columnsRow;
    private Row summaryRow;

    @Override
    public byte[] createReport(Report report, BandData rootBand, Map<String, Object> params) {

        Date startDate = (Date) params.get("startDate");
        Date endDate = (Date) params.get("endDate");
        SalesTeamReportDto salesTeamReportDto = new SalesTeamReportDataAdapter()
                .create(rootBand.getChildrenByName("ReportData"));
        List<AccountDto> data = salesTeamReportDto.getAccounts();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            init(report);

            if (data.isEmpty()) {
                throw new ReportFormattingException("No data loaded!");
            }

            preparePeriodColumns(data.get(0).getPeriods());

            int rowIndexOffset = 0;
            for (AccountDto dto :
                    data) {
                prepareDataRow(rowIndexOffset);
                Row row = getDataRow(rowIndexOffset++);
                fillRowKey(row, dto);
                fillRowValues(row, dto);
            }
            setRowCellValues(summaryRow, ACCOUNT_COLUMN_COUNT, salesTeamReportDto.getSubtotals());

            Save save = new Save(document.getPackage());
            save.save(outputStream);
            outputStream.flush();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new ReportFormattingException("Exception thrown on init", ex);
        }
    }

    private void init(Report report) throws Docx4JException {
        document = Document.create(SpreadsheetMLPackage
                .load(report.getReportTemplates().get("DEFAULT").getDocumentContent()));
        Document.SheetWrapper sheetWrapper = document.getWorksheets().get(0);
        WorksheetPart worksheetPart = sheetWrapper.getWorksheet();
        worksheet = worksheetPart.getContents();
        mergeCells = worksheet.getMergeCells();
        SheetData sheetData = worksheet.getSheetData();
        sheetName = sheetWrapper.getName();
        rows = sheetData.getRow();
        dataRow = rows.get(rows.size() - 1);
        upperDecorativeRow = rows.get(1);
        periodsRow = rows.get(2);
        columnsRow = rows.get(3);
        summaryRow = rows.get(4);
    }

    private void prepareDataRow(int dataRowOffset) {
        Row newRow = createRow(dataRow);
        newRow.setR(dataRow.getR() + dataRowOffset);
        newRow.setParent(worksheet.getSheetData());
        worksheet.getSheetData().getRow().add(newRow);

        for (Cell c :
                dataRow.getC()) {
            Cell newCell = createCell(c);
            newCell.setParent(newRow);
            newRow.getC().add(newCell);
        }
    }

    private Row getDataRow(int dataRowOffset) {
        return rows.get(Math.toIntExact(dataRow.getR() + dataRowOffset));
    }

    private void preparePeriodColumns(List<PeriodDto> periods) {
        if (periods.isEmpty()) {
            throw new ReportFormattingException("No time interval loaded");
        }
        periods.sort(Comparator.comparing(PeriodDto::getOrder));

        int cellIndex = upperDecorativeRow.getC().size() - 1;
        Col col = worksheet.getCols().get(0).getCol().get(0);
        col.setBestFit(true);

        for (PeriodDto period :
                periods) {
            int mergeSize = period.getCells().size();
            period.getCells().sort(Comparator.comparing(CellDto::getAnalyticOrder));

            Cell decorativeCell = createCell(upperDecorativeRow.getC().get(cellIndex));
            Cell periodCell = createCell(periodsRow.getC().get(cellIndex));
            Cell columnCell = createCell(columnsRow.getC().get(cellIndex));
            Cell summaryCell = createCell(summaryRow.getC().get(cellIndex));
            periodCell.setT(STCellType.STR);
            periodCell.setV(period.getPeriodName());
            periodCell.setParent(periodsRow);
            periodsRow.getC().add(periodCell);

            columnCell.setT(STCellType.STR);
            columnCell.setV(period.getCells().get(0).getName());
            columnCell.setParent(columnsRow);
            columnsRow.getC().add(columnCell);

            summaryCell.setParent(summaryRow);
            summaryRow.getC().add(summaryCell);

            decorativeCell.setParent(upperDecorativeRow);
            upperDecorativeRow.getC().add(decorativeCell);

            CellReference firstCr1 = new CellReference(sheetName, Math.toIntExact(upperDecorativeRow.getR()), upperDecorativeRow.getC().size());
            CellReference firstCr2 = new CellReference(sheetName, Math.toIntExact(periodsRow.getR()), periodsRow.getC().size());

            for (int i = 1; i < mergeSize; i++) {
                Cell emptyCell = Context.getsmlObjectFactory().createCell();
                emptyCell.setParent(periodsRow);
                periodsRow.getC().add(emptyCell);
                emptyCell = Context.getsmlObjectFactory().createCell();
                emptyCell.setParent(upperDecorativeRow);
                upperDecorativeRow.getC().add(emptyCell);
                columnCell = createCell(columnsRow.getC().get(cellIndex));
                columnCell.setT(STCellType.STR);
                columnCell.setV(period.getCells().get(i).getName());
                columnsRow.getC().add(columnCell);
                emptyCell = createCell(summaryRow.getC().get(cellIndex));
                emptyCell.setParent(summaryRow);
                summaryRow.getC().add(emptyCell);
            }
            CellReference lastCr1 = new CellReference(sheetName, Math.toIntExact(upperDecorativeRow.getR()), upperDecorativeRow.getC().size());
            CellReference lastCr2 = new CellReference(sheetName, Math.toIntExact(periodsRow.getR()), periodsRow.getC().size());

            CTMergeCell mc1 = new CTMergeCell();
            mc1.setRef(firstCr1.toReference() + ":" + lastCr1.toReference());
            mergeCells.getMergeCell().add(mc1);
            CTMergeCell mc2 = new CTMergeCell();
            mc2.setRef(firstCr2.toReference() + ":" + lastCr2.toReference());
            mergeCells.getMergeCell().add(mc2);
        }
    }

    private void fillRowKey(Row row, AccountDto dto) {
        List<Cell> cells = row.getC();
        for (int i = 0; i < ACCOUNT_COLUMN_COUNT; i++) {
            cells.get(i).setT(STCellType.STR);
        }
        cells.get(0).setV(dto.getAccountType());
        cells.get(1).setV(dto.getParent());
        cells.get(2).setV(dto.getCustomer());
        cells.get(3).setV(dto.getAccountManager());
        cells.get(4).setV(dto.getBusinessModel());
        cells.get(5).setV(dto.getApplicationType());
        cells.get(6).setV(dto.getActiveStr());
    }

    private void fillRowValues(Row row, AccountDto dto) {
        List<Cell> cells = row.getC();
        List<PeriodDto> periodDtos = dto.getPeriods();
        for (PeriodDto period :
                periodDtos) {
            for (CellDto cellDto :
                    period.getCells()) {
                Cell cell = Context.getsmlObjectFactory().createCell();
                cell.setT(STCellType.N);
                if (cellDto.getValue() != null) {
                    cell.setV(cellDto.getValue().toString());
                }
                cell.setParent(row);
                cells.add(cell);
            }
        }
    }

    private void setRowCellValues(Row row, int firstCellColumnIndex, AccountDto dto) {
        List<Cell> cells = row.getC();
        List<PeriodDto> periodDtos = dto.getPeriods();
        for (PeriodDto period :
                periodDtos) {
            for (CellDto cellDto :
                    period.getCells()) {
                Cell cell = cells.get(firstCellColumnIndex++);
                if (cellDto.getValue() != null) {
                    cell.setT(STCellType.N);
                    cell.setV(cellDto.getValue().toString());
                }
            }
        }
    }

    private static Row createRow(Row r) {
        Row row = Context.getsmlObjectFactory().createRow();
        row.setHt(r.getHt());
        row.setExtLst(r.getExtLst());
        row.setOutlineLevel(r.getOutlineLevel());
        row.setCustomFormat(r.isCustomFormat());
        row.setHidden(r.isHidden());
        row.setPh(r.isPh());
        row.setCollapsed(r.isCollapsed());
        row.setCustomHeight(r.isCustomHeight());
        row.setThickBot(r.isThickBot());
        row.setThickTop(r.isThickTop());
        row.setS(r.getS());
        return row;
    }

    private Cell createCell(Cell c) {
        Cell cell = Context.getsmlObjectFactory().createCell();
        cell.setCm(c.getCm());
        cell.setVm(c.getVm());
        cell.setExtLst(c.getExtLst());
        cell.setF(c.getF());
        cell.setIs(c.getIs());
        cell.setT(c.getT());
        //cell.setR(c.getR());
        cell.setPh(c.isPh());
        cell.setS(c.getS());
        cell.setV(c.getV());
        return cell;
    }
}
