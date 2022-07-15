package com.borets.pfa.report.custom;

import java.math.BigDecimal;

public class ReportCell<T> {

    private final Coordinates<T> coordinates;

    private final CellType cellType;

    private final String valueString;

    private final BigDecimal valueInt;

    private ReportCell(Coordinates<T> coordinates, CellType cellType, String valueString, BigDecimal valueInt) {
        this.coordinates = coordinates;
        this.cellType = cellType;
        this.valueString = valueString;
        this.valueInt = valueInt;
    }

    public static <T> ReportCell newDigit(Object value, T row, HorizontalPosition column) {
        return new ReportCell(new Coordinates<T>(row, column), CellType.DIGIT, null, new BigDecimal(value.toString()));
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public CellType getCellType() {
        return cellType;
    }

    public String getValueString() {
        return valueString;
    }

    public BigDecimal getValueNumber() {
        return valueInt;
    }

}
