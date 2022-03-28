package com.borets.pfa.report.salesteam;

import com.haulmont.yarg.structure.BandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SalesTeamReportDataAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesTeamReportDataAdapter.class);

    private static final String COLUMN_ANALYTIC_TITLE = "ANALYTIC_TITLE";
    private static final String COLUMN_ANALYTIC_ORDER = "ANALYTIC_ORDER";
    private static final String COLUMN_PERIOD = "P";
    private static final String COLUMN_VALUE = "VALUE_";

    public SalesTeamReportDto create(List<BandData> data) {
        SalesTeamReportDto resultDto = new SalesTeamReportDto();
        if (isEmpty(data)) {
            return resultDto;
        }
        Set<Date> periods = resultDto.getPeriods();
        Set<String> analyticSets = resultDto.getAnalyticSets();
        List<AccountDto> accounts = resultDto.getAccounts();

        for (BandData row :
                data) {
            Map<String, Object> kv = row.getData();
            if (kv.containsKey(COLUMN_PERIOD)) {
                periods.add((Date) kv.get(COLUMN_PERIOD));
            }
            if (kv.containsKey(COLUMN_ANALYTIC_TITLE)) {
                analyticSets.add((String) kv.get(COLUMN_ANALYTIC_TITLE));
            }

            AccountDto accountDto = AccountDto.createFromMap(kv);
            if (!accounts.contains(accountDto)) {
                accounts.add(accountDto);
            }
        }

        for (AccountDto accountDto :
                accounts) {
            int periodOrder = 0;
            for (Date period :
                    periods) {
                PeriodDto p = new PeriodDto();
                p.setPeriodName(new SimpleDateFormat("MMM yyyy", Locale.US).format(period));
                p.setOrder(periodOrder++);
                accountDto.getPeriods().add(p);

                int cellOrder = 0;
                for (String analyticSet :
                        analyticSets) {
                    CellDto cellDto = new CellDto();
                    cellDto.setName(analyticSet);
                    cellDto.setOrder(cellOrder++);
                    p.getCells().add(cellDto);

                    for (BandData row :
                            data) {
                        Map<String, Object> kv = row.getData();

                        if (accountDto.equals(AccountDto.createFromMap(kv))
                                && Objects.equals(period, kv.get(COLUMN_PERIOD))
                                && Objects.equals(analyticSet, kv.get(COLUMN_ANALYTIC_TITLE))
                        ) {
                            Object value = kv.get(COLUMN_VALUE);
                            if (value instanceof Integer) {
                                cellDto.setValue(new BigDecimal((int) value));
                            }
                            int analyticOrder = (int) kv.getOrDefault(COLUMN_ANALYTIC_ORDER, -1);
                            cellDto.setAnalyticOrder(analyticOrder);
                            break;
                        }
                    }
                }
            }
        }
        sortAccounts(accounts);

        for (AccountDto acc :
                accounts) {
            acc.getPeriods().sort(Comparator.comparing(PeriodDto::getOrder));
            for (PeriodDto period :
                    acc.getPeriods()) {

                List<CellDto> cells = period.getCells();
                // Add some order to unordered (order is null) elements
                setAnalyticOrderToUnorderedCells(cells);
                cells.sort(Comparator.comparing(CellDto::getOrder));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("ACCOUNT {} PERIOD {}", acc.getCustomer(), period.getPeriodName());
                    cells.forEach(cellDto -> {
                        LOGGER.debug("{} - {}", cellDto.getOrder(), cellDto.getAnalyticOrder());
                    });
                    LOGGER.debug("----------------------------------------------");
                }
            }
        }
        fillSubtotals(resultDto.getSubtotals(), accounts);
        return resultDto;
    }

    private static void sortAccounts(List<AccountDto> accounts) {
        accounts.sort(Comparator.comparing(AccountDto::getAccountOrder)
                .thenComparing(AccountDto::getParent, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(AccountDto::getCustomer)
                .thenComparing(AccountDto::getAccountManager));
    }

    private void setAnalyticOrderToUnorderedCells(List<CellDto> cells) {
        if (cells.stream().anyMatch(c -> c.getAnalyticOrder() <= 0)) {
            int nextOrderValue = cells.stream().mapToInt(CellDto::getAnalyticOrder).max().orElseThrow() + 10000;
            for (CellDto c :
                    cells) {
                if (c.getAnalyticOrder() <= 0) {
                    nextOrderValue++;
                    LOGGER.debug("UNORDERED CELL: {}, VALUE: {}, DEFINED NEW ANALYTIC ORDER: {}",
                            c.getName(), c.getValue(), nextOrderValue);
                    c.setAnalyticOrder(nextOrderValue);
                }
            }
        }
    }

    private void fillSubtotals(AccountDto subtotals, List<AccountDto> accounts) {
        // Iterating over all already prepared accounts
        for (AccountDto acc :
                accounts) {
            // Iterating over all account's periods
            for (PeriodDto period :
                    acc.getPeriods()) {

                // Getting existing subtotal period (or creating new one)
                PeriodDto subtotalPeriod = null;
                for (PeriodDto p :
                        subtotals.getPeriods()) {
                    if (Objects.equals(period.getPeriodName(), p.getPeriodName())) {
                        subtotalPeriod = p;
                        break;
                    }
                }
                if (subtotalPeriod == null) {
                    subtotalPeriod = new PeriodDto();
                    subtotalPeriod.setPeriodName(period.getPeriodName());
                    subtotalPeriod.setOrder(period.getOrder());
                    subtotals.getPeriods().add(subtotalPeriod);
                }

                // Iteration over period's data cells
                for (CellDto cell :
                        period.getCells()) {

                    // Getting existing subtotal data cell (or creating new one)
                    CellDto subtotalCell = null;
                    for (CellDto c :
                            subtotalPeriod.getCells()) {
                        if (Objects.equals(cell.getName(), c.getName())) {
                            subtotalCell = c;
                            break;
                        }
                    }
                    if (subtotalCell == null) {
                        subtotalCell = new CellDto();
                        subtotalCell.setName(cell.getName());
                        subtotalCell.setOrder(cell.getOrder());
                        subtotalCell.setAnalyticOrder(cell.getAnalyticOrder());
                        subtotalPeriod.getCells().add(subtotalCell);
                    }

                    // Adding value to subtotal (if exist)
                    if (cell.getValue() != null) {
                        BigDecimal value = cell.getValue();
                        if (subtotalCell.getValue() == null) {
                            subtotalCell.setValue(value);
                        } else {
                            subtotalCell.setValue(value.add(subtotalCell.getValue()));
                        }
                    }
                }
            }
        }
    }

    private boolean isEmpty(List<BandData> data) {
        if (data.isEmpty()) {
            return true;
        }
        if (data.size() == 1 && data.get(0).getData().size() == 1) {
            return true;
        }
        return false;
    }

}
