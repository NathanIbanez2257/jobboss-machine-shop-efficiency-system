package com.example.machinistapp;
import javafx.scene.control.TableRow;

public class CustomTableRow extends TableRow<ReportRow> {
    @Override
    protected void updateItem(ReportRow item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setStyle("");
        } else {
            if (item.getPiecesGood() > item.getEstimatedPieces()) {
                setStyle("-fx-background-color: #FFCCCC;");
            } else {
                setStyle("");
            }
        }
    }
}
