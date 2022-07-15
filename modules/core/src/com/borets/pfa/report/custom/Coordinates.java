package com.borets.pfa.report.custom;

import java.util.Objects;

public class Coordinates<T> {

    private final T row;

    private final HorizontalPosition column;

    public Coordinates(T row, HorizontalPosition column) {
        this.row = Objects.requireNonNull(row);
        this.column = Objects.requireNonNull(column);
    }

    public T getRow() {
        return row;
    }

    public HorizontalPosition getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates<T> that = (Coordinates<T>) o;
        return row.equals(that.row) && column.equals(that.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

}
