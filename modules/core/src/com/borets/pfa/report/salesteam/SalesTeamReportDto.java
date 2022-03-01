package com.borets.pfa.report.salesteam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SalesTeamReportDto {

    private Set<Date> periods = new TreeSet<>(Date::compareTo);

    private Set<String> analyticSets = new TreeSet<>(String::compareToIgnoreCase);

    private List<AccountDto> accounts = new ArrayList<>();

    private AccountDto subtotals = new AccountDto();

    public List<AccountDto> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountDto> accounts) {
        this.accounts = accounts;
    }

    public Set<Date> getPeriods() {
        return periods;
    }

    public void setPeriods(Set<Date> periods) {
        this.periods = periods;
    }

    public Set<String> getAnalyticSets() {
        return analyticSets;
    }

    public void setAnalyticSets(Set<String> analyticSets) {
        this.analyticSets = analyticSets;
    }

    public AccountDto getSubtotals() {
        return subtotals;
    }

    public void setSubtotals(AccountDto subtotals) {
        this.subtotals = subtotals;
    }

}
