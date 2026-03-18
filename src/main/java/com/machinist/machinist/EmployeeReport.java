package com.machinist.machinist;

import javafx.beans.property.SimpleDoubleProperty;

public class EmployeeReport {

    private final String employeeName;
    private final SimpleDoubleProperty totalPiecesGood;
    private final SimpleDoubleProperty totalScrapped;
    private final SimpleDoubleProperty averageEfficiency;
    private int efficiencyCount;

    public EmployeeReport(String employeeName, double piecesGood, double piecesScrapped, double efficiency) {
        this.employeeName = employeeName;
        this.totalPiecesGood = new SimpleDoubleProperty(piecesGood);
        this.totalScrapped = new SimpleDoubleProperty(piecesScrapped);
        this.averageEfficiency = new SimpleDoubleProperty(efficiency);
        this.efficiencyCount = 1;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public double getTotalPiecesGood() {
        return totalPiecesGood.get();
    }

    public SimpleDoubleProperty totalPiecesGoodProperty() {
        return totalPiecesGood;
    }

    public void addPiecesGood(double piecesGood) {
        this.totalPiecesGood.set(this.totalPiecesGood.get() + piecesGood);
    }

    public double getTotalScrapped() {
        return totalScrapped.get();
    }

    public SimpleDoubleProperty totalScrappedProperty() {
        return totalScrapped;
    }

    public void addScrapped(double piecesScrapped) {
        this.totalScrapped.set(this.totalScrapped.get() + piecesScrapped);
    }

    public double getAverageEfficiency() {
        return averageEfficiency.get();
    }

    public SimpleDoubleProperty averageEfficiencyProperty() {
        return averageEfficiency;
    }

    public void addEfficiency(double efficiency) {
        double totalEfficiency = this.averageEfficiency.get() * this.efficiencyCount;
        totalEfficiency += efficiency;
        this.efficiencyCount++;
        this.averageEfficiency.set(totalEfficiency / this.efficiencyCount);
    }
}

