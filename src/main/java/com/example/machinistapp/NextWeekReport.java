package com.example.machinistapp;
import javafx.beans.property.*;

public class NextWeekReport {
    private final StringProperty machine;
    private final StringProperty monday;
    private final StringProperty tuesday;
    private final StringProperty wednesday;
    private final StringProperty thursday;
    private final StringProperty friday;

    public NextWeekReport (String machine, String monday, String tuesday, String wednesday, String thursday, String friday) {

        this.machine = new SimpleStringProperty(machine);
        this.monday = new SimpleStringProperty(monday);
        this.tuesday = new SimpleStringProperty(tuesday);
        this.wednesday = new SimpleStringProperty(wednesday);
        this.thursday = new SimpleStringProperty(thursday);
        this.friday = new SimpleStringProperty(friday);
    }
    public String getMachine() {
        return machine.get();
    }
    public String getMonday() {
        return monday.get();
    }
    public String getTuesday() {
        return tuesday.get();
    }
    public String getWednesday() {
        return wednesday.get();
    }
    public String getThursday() {
        return thursday.get();
    }
    public String getFriday() {
        return friday.get();
    }

    public StringProperty machineProperty() {
        return machine;
    }
    public StringProperty mondayProperty() {
        return monday;
    }
    public StringProperty tuesdayProperty() {
        return tuesday;
    }
    public StringProperty wednesdayProperty() {
        return wednesday;
    }
    public StringProperty thursdayProperty() {
        return thursday;
    }
    public StringProperty fridayProperty() {
        return friday;
    }
}
