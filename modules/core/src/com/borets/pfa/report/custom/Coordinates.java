package com.borets.pfa.report.custom;

import java.util.Objects;

public class Coordinates {

    private final Account row;

    private final HorizontalPosition column;

    public Coordinates(Account row, HorizontalPosition column) {
        this.row = Objects.requireNonNull(row);
        this.column = Objects.requireNonNull(column);
    }

    public Account getRow() {
        return row;
    }

    public HorizontalPosition getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return row.equals(that.row) && column.equals(that.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
