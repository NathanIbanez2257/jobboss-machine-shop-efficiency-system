package com.example.machinistapp;
import javafx.beans.property.*;

public class JobNumberReport {
    private final StringProperty jobNumber;
    private final StringProperty partNumber;
    private final StringProperty workCenter;
    private final StringProperty status;
    private final SimpleIntegerProperty piecesGood;
    private final SimpleIntegerProperty piecesScrapped;
    private final SimpleDoubleProperty totalEstimatedHours;
    private final SimpleDoubleProperty totalActualHours;
    private final SimpleIntegerProperty estimatedQuantity;

    public JobNumberReport (String[] rowData) {
        this.jobNumber = new SimpleStringProperty(rowData[0]);
        this.partNumber = new SimpleStringProperty(rowData[1]);
        this.piecesGood = new SimpleIntegerProperty((Integer.parseInt(rowData[2])));
        this.piecesScrapped = new SimpleIntegerProperty(Integer.parseInt(rowData[3]));
        this.totalEstimatedHours = new SimpleDoubleProperty(roundToNearestTenth(Double.parseDouble(rowData[4])));
        this.totalActualHours = new SimpleDoubleProperty(roundToNearestTenth(Double.parseDouble(rowData[5])));
        this.estimatedQuantity = new SimpleIntegerProperty(Integer.parseInt(rowData[6]));
        this.workCenter = new SimpleStringProperty(rowData[7]);
        this.status = new SimpleStringProperty(rowData[8]);
    }
    public String getJobNumber() {
        return jobNumber.get();
    }
    public StringProperty jobNumberProperty() {
        return jobNumber;
    }
    public String getPartNumber() {
        return partNumber.get();
    }
    public StringProperty partNumberProperty() {
        return partNumber;
    }
    public double getPiecesGood() {
        return piecesGood.get();
    }
    public IntegerProperty piecesGoodProperty() {
        return piecesGood;
    }
    public double getPiecesScrapped() {
        return piecesScrapped.get();
    }
    public SimpleIntegerProperty piecesScrappedProperty() {
        return piecesScrapped;
    }
    public double getTotalEstimatedHours() {
        return roundToNearestTenth(totalEstimatedHours.get());
    }
    public DoubleProperty totalEstimatedHoursProperty() {
        return totalEstimatedHours;
    }
    public double getTotalActualHours() {
        return roundToNearestTenth(totalActualHours.get());
    }
    public DoubleProperty totalActualHoursProperty() {
        return totalActualHours;
    }
    public double getEstimatedQuantity() {
        return estimatedQuantity.get();
    }
    public SimpleIntegerProperty estimatedQuantityProperty() {
        return estimatedQuantity;
    }
    public String getWorkCenter() {
        return workCenter.get();
    }
    public StringProperty workCenterProperty() {
        return workCenter;
    }
    public String getStatus() {
        return status.get();
    }
    public StringProperty statusProperty() {
        return status;
    }
    private double roundToNearestTenth(double value) {
        return Math.round(value * 10) / 10.0;
    }
}