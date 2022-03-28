package com.borets.pfa.report.salesteam;

import java.math.BigDecimal;

public class CellDto {
    private String name;
    private BigDecimal value;
    private int analyticOrder;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public int getAnalyticOrder() {
        return analyticOrder;
    }

    public void setAnalyticOrder(int analyticOrder) {
        this.analyticOrder = analyticOrder;
    }

}
