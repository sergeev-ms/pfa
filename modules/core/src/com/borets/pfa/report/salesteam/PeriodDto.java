package com.borets.pfa.report.salesteam;

import java.util.ArrayList;
import java.util.List;

public class PeriodDto {

    private int order;
    private String periodName;
    private List<CellDto> cells = new ArrayList<CellDto>();

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public List<CellDto> getCells() {
        return cells;
    }

    public void setCells(List<CellDto> cells) {
        this.cells = cells;
    }
}
