package com.borets.pfa.report.custom;

import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.STCellType;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AccountBasedReportTemplate extends CustomExcelReportTemplate<Account> {

    protected List<Account> getOrderedAccounts() {
        return coordinates.keySet().stream()
                .sorted(Comparator.comparing(Account::getOrder)
                        .thenComparing(Account::getParent, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Account::getCustomer))
                .collect(Collectors.toList());
    }

    protected static void addAccountInfo(Row row, int accountInfoColumns, Account account, Long lastInRowStyle, Long cellStandardStyle) {
        List<Cell> c = row.getC();

        // account info
        for (int i = 0; i < accountInfoColumns; ++i) {
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
                    accountCell.setV(account.getAccountManager());
                    break;
                case 4:
                    accountCell.setV(account.getBusinessModel());
                    break;
                case 5:
                    accountCell.setV(account.getApplicationType());
                    break;
                case 6:
                    accountCell.setV(account.getActiveStr());
                    break;
                default:
                    accountCell.setV("");
                    break;
            }
            if (i == accountInfoColumns - 1 && lastInRowStyle != null) {
                accountCell.setS(lastInRowStyle);
            } else if (cellStandardStyle != null) {
                accountCell.setS(cellStandardStyle);
            }
            c.add(accountCell);
        }
    }

}
