package com.example.machinistapp;
import javafx.scene.control.TableCell;

public class PercentageTableCell<T> extends TableCell<T, Double> {
    @Override
    protected void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            setText(String.format("%.2f%%", item));
        }
    }
}

