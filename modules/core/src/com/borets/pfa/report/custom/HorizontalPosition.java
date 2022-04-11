package com.borets.pfa.report.custom;

import java.util.Date;
import java.util.Objects;

public class HorizontalPosition {

    private final String name;

    private final int order;

    private final Date date;

    public HorizontalPosition(String name, int order, Date date) {
        this.name = name;
        this.order = order;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HorizontalPosition horizontalPosition = (HorizontalPosition) o;
        return order == horizontalPosition.order && name.equals(horizontalPosition.name) && Objects.equals(date, horizontalPosition.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, order, date);
    }
}
