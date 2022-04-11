package com.borets.pfa.report.custom;

import java.math.BigDecimal;

public class ReportCell {

    private final Coordinates coordinates;

    private final CellType cellType;

    private final String valueString;

    private final BigDecimal valueInt;

    private ReportCell(Coordinates coordinates, CellType cellType, String valueString, BigDecimal valueInt) {
        this.coordinates = coordinates;
        this.cellType = cellType;
        this.valueString = valueString;
        this.valueInt = valueInt;
    }

    public static ReportCell newDigit(Object value, Account account, HorizontalPosition column) {
        return new ReportCell(new Coordinates(account, column), CellType.DIGIT, null, new BigDecimal(value.toString()));
    }

    public static ReportCell newString(Object value, Account account, HorizontalPosition column) {
        return new ReportCell(new Coordinates(account, column), CellType.STRING, (String) value, null);
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
