package com.example.machinistapp;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class AggregateData {
    private final DoubleProperty totalPieces;
    private final DoubleProperty totalScrapped;
    private final DoubleProperty averageEfficiency;

    public AggregateData(double totalPieces, double totalScrapped, double averageEfficiency) {
        this.totalPieces = new SimpleDoubleProperty(totalPieces);
        this.totalScrapped = new SimpleDoubleProperty(totalScrapped);
        this.averageEfficiency = new SimpleDoubleProperty(averageEfficiency);
    }
    public DoubleProperty totalPiecesProperty() {
        return totalPieces;
    }
    public DoubleProperty totalScrappedProperty() {
        return totalScrapped;
    }
    public DoubleProperty averageEfficiencyProperty() {
        return averageEfficiency;
    }
}