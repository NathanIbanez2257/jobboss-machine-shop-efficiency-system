package com.machinist.machinist;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReportRow {
    private final StringProperty comments;
    private final StringProperty employeeName;
    private final StringProperty partNumber;
    private final StringProperty ticketDate;

    private final SimpleDoubleProperty hoursWorkedDouble;
    private final SimpleDoubleProperty piecesGoodDouble;
    private final SimpleDoubleProperty piecesScrappedDouble;
    private final SimpleDoubleProperty piecesTotalDouble;
    private final SimpleDoubleProperty estimatedPiecesDouble;
    private final SimpleDoubleProperty efficiencyDouble;
    private final SimpleDoubleProperty estCycleTimeDouble;





    public ReportRow(String[] rowData) {

        this.employeeName = new SimpleStringProperty(rowData[0]);
        this.ticketDate = new SimpleStringProperty(rowData[1]);
        this.partNumber = new SimpleStringProperty(rowData[2]);
        this.hoursWorkedDouble = new SimpleDoubleProperty(Double.parseDouble(rowData[3]));
        this.piecesGoodDouble = new SimpleDoubleProperty(Double.parseDouble(rowData[4]));
        this.piecesScrappedDouble = new SimpleDoubleProperty(Double.parseDouble(rowData[5]));
        this.piecesTotalDouble = new SimpleDoubleProperty(Double.parseDouble(rowData[6]));
        this.estimatedPiecesDouble = new SimpleDoubleProperty(Double.parseDouble(rowData[7]));
        this.efficiencyDouble = new SimpleDoubleProperty(Double.parseDouble(rowData[8]));
        this.comments = new SimpleStringProperty(rowData[9]);
        this.estCycleTimeDouble = new SimpleDoubleProperty(Double.parseDouble(rowData[10]));

        this.piecesTotalDouble.bind(piecesGoodDouble.add(piecesScrappedDouble));
        this.efficiencyDouble.bind(piecesGoodDouble.divide(estimatedPiecesDouble.doubleValue())); // Assuming efficiency calculation

        this.estimatedPiecesDouble.bind(
                Bindings.createDoubleBinding(() ->
                                Math.floor(hoursWorkedDouble.get() / estCycleTimeDouble.get()),
                        hoursWorkedDouble, estCycleTimeDouble));



        this.efficiencyDouble.bind(Bindings.createDoubleBinding(() -> {
            double good = getPiecesGood();
            double estimated = getEstimatedPieces();

            if (Double.isFinite(estimated) && estimated != 0) {
                return Math.floor( (good / estimated) * 1000) / 10;
            } else {
                return 0.0; // Replace Infinity, NaN, and division by zero with 0
            }
        }, piecesGoodDouble, estimatedPiecesDouble));


    }


    public String getComments() {
        return comments.get();
    }

    public StringProperty commentsProperty() {
        return comments;
    }


    public double getEfficiency() {
        return efficiencyDouble.get();
    }

    public DoubleProperty efficiencyProperty() {
        return efficiencyDouble;
    }
    public void setEfficiency(double efficiency) {
        this.efficiencyDouble.set(efficiency);
    }



    public StringProperty employeeNameProperty() {
        return employeeName;
    }
    public String getEmployeeName() {
        return employeeName.get();
    }


    public double getEstCycleTime() {
        return estCycleTimeDouble.get();
    }
    public DoubleProperty estCycleTimeProperty() {
        return estCycleTimeDouble;
    }
    public void setEstCycleTime(double estCycleTime) {
        this.estCycleTimeDouble.set(estCycleTime);
    }


    public double getEstimatedPieces() {
        return estimatedPiecesDouble.get();
    }
    public DoubleProperty estimatedPiecesProperty() {
        return estimatedPiecesDouble;
    }
    public void setEstimatedPieces(double estimatedPieces) {
        this.estimatedPiecesDouble.set(estimatedPieces);
    }


    public double getHoursWorked() {
        return hoursWorkedDouble.get();
    }
    public DoubleProperty hoursWorkedProperty() {
        return hoursWorkedDouble;
    }
    public void setHoursWorked(double hoursWorked) {
        this.hoursWorkedDouble.set(hoursWorked);
    }


    public StringProperty partNumberProperty() {
        return partNumber;
    }


    public double getPiecesGood() {
        return piecesGoodDouble.get();
    }
    public DoubleProperty piecesGoodProperty() {
        return piecesGoodDouble;
    }
    public void setPiecesGood(double piecesGood) {
        this.piecesGoodDouble.set(piecesGood);
    }


    public double getPiecesScrapped() {
        return piecesScrappedDouble.get();
    }
    public DoubleProperty piecesScrappedProperty() {
        return piecesScrappedDouble;
    }
    public void setPiecesScrapped(double piecesScrapped) {
        this.piecesScrappedDouble.set(piecesScrapped);
    }


    public double getPiecesTotal() {
        return piecesTotalDouble.get();
    }
    public DoubleProperty piecesTotalProperty() {
        return piecesTotalDouble;
    }
    public void setPiecesTotal(double piecesTotal) {
        this.piecesTotalDouble.set(piecesTotal);
    }



    public StringProperty ticketDateProperty() {
        return ticketDate;
    }
}

