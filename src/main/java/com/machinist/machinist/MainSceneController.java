package com.machinist.machinist;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.converter.DoubleStringConverter;
import javafx.fxml.FXML;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainSceneController {

    @FXML
    private TextField tfTitle, start_date, end_date;

    private final machinist report = new machinist();

    String[][] reportstring;
    String[][] secondreportstring;

    String[] secondReportDates;
    String firstStartDate, firstEndDate, secondStartDate, secondEndDate;




    @FXML
    String EndDateInput() {
        String end_date_str = end_date.getText();
        return end_date_str;
    }

    @FXML
    String StartDateInput() {

        String start_date_str = start_date.getText();
        return start_date_str;

    }

    @FXML
    void btnGenerate(ActionEvent event) throws Exception {

        System.out.println(StartDateInput() + "  " + EndDateInput());

        showPopUp("This May Take 1-5 Minutes");


        firstStartDate = StartDateInput();
        firstEndDate = EndDateInput();

        secondReportDates = calculateSecondReportDates(StartDateInput(), EndDateInput());
        secondStartDate = secondReportDates[0];
        secondEndDate = secondReportDates[1];

        Service<String[][]> service = createReportService(StartDateInput(), EndDateInput());


        System.out.println(secondReportDates[0] + " " + secondReportDates[1]);

        service.setOnSucceeded(e -> {

            reportstring = service.getValue();
            // Close the "Please wait" popup and show success message
            showPopUp("Report for " + StartDateInput() + " " + EndDateInput() + " "
                    + "successfully generated!\nSecond report will generate shortly..");
            setReportData(table, filter, reportstring);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            //////////// second report string //////////////////////


            System.out.println(secondStartDate + " " + secondEndDate);
            Service<String[][]> secondService = createReportService(secondStartDate, secondEndDate);

            secondService.setOnSucceeded(event2 -> {
                secondreportstring = secondService.getValue();
                // showPopUp("Second Report generated successfully.");
                setReportData(table2, filter2, secondreportstring);
                // Handle second report data as needed
            });
            secondService.setOnFailed(event2 -> {
                showPopUp("Failed to generate the second report.");
            });
            secondService.start();

        });

        service.setOnFailed(e -> {
            // Handle failure
            showPopUp("Failed to generate report for last week.");
        });
        service.start();

        Label1.setText("Start Date: " + firstStartDate + "\tEnd Date: " + firstEndDate);
        Label2.setText("Start Date: " + secondStartDate + "\tEnd Date: " + secondEndDate);

    }

    private String[] calculateSecondReportDates(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        // Calculate the number of days between the start and end dates
        long daysBetween = ChronoUnit.DAYS.between(start, end);

        // Determine the day of the week for the original start date
        DayOfWeek startDayOfWeek = start.getDayOfWeek();

        // Calculate the second start date
        // Adjust to the start of the week (Monday)
        LocalDate secondStartDate = start.minusDays(daysBetween + 1); // +1 to avoid overlap
        secondStartDate = secondStartDate.with(TemporalAdjusters.previousOrSame(startDayOfWeek));

        // Calculate the second end date based on the number of days between the second start date and original start date
        LocalDate secondEndDate = secondStartDate.plusDays(daysBetween);

        // Adjust if secondEndDate overlaps with original start date
        if (secondEndDate.isAfter(end)) {
            secondEndDate = end;
            secondStartDate = secondEndDate.minusDays(daysBetween);
            secondStartDate = secondStartDate.with(TemporalAdjusters.previousOrSame(startDayOfWeek));
        }

        return new String[]{secondStartDate.format(formatter), secondEndDate.format(formatter)};
    }

    @FXML
    void btnLastWeek(ActionEvent event) throws Exception {

        LocalDate today = LocalDate.now();
        LocalDate lastMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastFullMonday = lastMonday.minusWeeks(1);
        LocalDate lastFullFriday = lastFullMonday.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedMonday = lastFullMonday.format(formatter);
        String formattedFriday = lastFullFriday.format(formatter);

        System.out.println("Last full Monday: " + formattedMonday);
        System.out.println("Last full Friday: " + formattedFriday);


        firstStartDate = formattedMonday;
        firstEndDate = formattedFriday;

        secondReportDates = calculateSecondReportDates(formattedMonday, formattedFriday);
        System.out.println(secondReportDates[0] + " " + secondReportDates[1]);

        secondReportDates = calculateSecondReportDates(formattedMonday, formattedFriday);
        secondStartDate = secondReportDates[0];
        secondEndDate = secondReportDates[1];

        showPopUp("This May Take 1-5 Minutes");

        Service<String[][]> service = createReportService(formattedMonday, formattedFriday);

        service.setOnSucceeded(e -> {
            reportstring = service.getValue();
            // Close the "Please wait" popup and show success message
            showPopUp("Report for last week "
                    + "successfully generated!\nSecond report will generate shortly..");
            setReportData(table, filter, reportstring);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }


            System.out.println(secondReportDates[0] + " " + secondReportDates[1]);

            Service<String[][]> secondService = createReportService(secondStartDate, secondEndDate);
            secondService.setOnSucceeded(event2 -> {
                secondreportstring = secondService.getValue();
                // showPopUp("Second Report generated successfully.");
                setReportData(table2, filter2, secondreportstring);

                // Handle second report data as needed
            });
            secondService.setOnFailed(event2 -> {
                showPopUp("Failed to generate the second report.");
            });
            secondService.start();

        });

        service.setOnFailed(e -> {
            // Handle failure
            showPopUp("Failed to generate report for last week.");
        });
        service.start();

        Label1.setText("Start Date: " + firstStartDate + "    End Date: " + firstEndDate);
        Label2.setText("Start Date: " + secondStartDate + "\tEnd Date: " + secondEndDate);

    }

    private Service<String[][]> createReportService(String startDate, String endDate) {
        return new Service<>() {
            @Override
            protected Task<String[][]> createTask() {
                return new Task<>() {
                    @Override
                    protected String[][] call() throws Exception {
                        return report.runReport(startDate, endDate);
                    }
                };
            }
        };
    }

    private void showPopUp(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initStyle(StageStyle.DECORATED);

        alert.setTitle("Report Generation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FXML
    private Label Label1, Label2;


    @FXML
    private Button removeRowButton2;
    @FXML
    private TableView<ReportRow> table2;
    @FXML
    private TableView<AggregateData> bottomTable2;
    @FXML
    private TableColumn<ReportRow, String> comments2;
    @FXML
    private TableColumn<ReportRow, Double> efficiency2;
    @FXML
    private TableColumn<ReportRow, String> employeeName2;
    @FXML
    private TableColumn<ReportRow, Double> estCycleTime2;
    @FXML
    private TableColumn<ReportRow, Double> estimatedPieces2;
    @FXML
    private TableColumn<ReportRow, Double> hoursWorked2;
    @FXML
    private TableColumn<ReportRow, String> partNumber2;
    @FXML
    private TableColumn<ReportRow, Double> piecesGood2;
    @FXML
    private TableColumn<ReportRow, Double> piecesScrapped2;
    @FXML
    private TableColumn<ReportRow, Double> piecesTotal2;
    @FXML
    private TableColumn<ReportRow, String> ticketDate2;
    @FXML
    private ChoiceBox<String> filter2;
    @FXML
    private TableColumn<AggregateData, Double> totalPiecesColumn2;
    @FXML
    private TableColumn<AggregateData, Double> totalScrappedColumn2;
    @FXML
    private TableColumn<AggregateData, Double> averageEfficiencyColumn2;


    @FXML
    private Button removeRowButton;
    @FXML
    private TableView<ReportRow> table;
    @FXML
    private TableView<AggregateData> bottomTable;
    @FXML
    private TableColumn<ReportRow, String> comments;
    @FXML
    private TableColumn<ReportRow, Double> efficiency;
    @FXML
    private TableColumn<ReportRow, String> employeeName;
    @FXML
    private TableColumn<ReportRow, Double> estCycleTime;
    @FXML
    private TableColumn<ReportRow, Double> estimatedPieces;
    @FXML
    private TableColumn<ReportRow, Double> hoursWorked;
    @FXML
    private TableColumn<ReportRow, String> partNumber;
    @FXML
    private TableColumn<ReportRow, Double> piecesGood;
    @FXML
    private TableColumn<ReportRow, Double> piecesScrapped;
    @FXML
    private TableColumn<ReportRow, Double> piecesTotal;
    @FXML
    private TableColumn<ReportRow, String> ticketDate;
    @FXML
    private ChoiceBox<String> filter;
    @FXML
    private TableColumn<AggregateData, Double> totalPiecesColumn;
    @FXML
    private TableColumn<AggregateData, Double> totalScrappedColumn;
    @FXML
    private TableColumn<AggregateData, Double> averageEfficiencyColumn;

    private ObservableList<ReportRow> reportRows = FXCollections.observableArrayList();
    private ObservableList<AggregateData> aggregateDataList = FXCollections.observableArrayList();

    private ObservableList<ReportRow> reportRows2 = FXCollections.observableArrayList();
    private ObservableList<AggregateData> aggregateDataList2 = FXCollections.observableArrayList();



    @FXML
    private TableView<EmployeeReport> compareTopTable;
    @FXML
    private TableColumn<EmployeeReport, Double> averageEfficiency3;
    @FXML
    private TableColumn<EmployeeReport, String> employeeName3;
    @FXML
    private TableColumn<EmployeeReport, Double> piecesGood3;
    @FXML
    private TableColumn<EmployeeReport, Double> totalScrapped3;


    @FXML
    private TableView<EmployeeReport> compareBottomTable;
    @FXML
    private TableColumn<EmployeeReport, Double> averageEfficiency4;
    @FXML
    private TableColumn<EmployeeReport, String> employeeName4;
    @FXML
    private TableColumn<EmployeeReport, Double> piecesGood4;
    @FXML
    private TableColumn<EmployeeReport, Double> totalScrapped4;


    @FXML
    void initialize() {
        tab2();
        tab3();
        compareTab();

    }


    private void compareTab() {
        employeeName3.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmployeeName()));
        piecesGood3.setCellValueFactory(cellData -> cellData.getValue().totalPiecesGoodProperty().asObject());
        totalScrapped3.setCellValueFactory(cellData -> cellData.getValue().totalScrappedProperty().asObject());
        averageEfficiency3.setCellValueFactory(cellData -> cellData.getValue().averageEfficiencyProperty().asObject());
        averageEfficiency3.setCellFactory(column -> new PercentageTableCell<>());

        employeeName4.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmployeeName()));
        piecesGood4.setCellValueFactory(cellData -> cellData.getValue().totalPiecesGoodProperty().asObject());
        totalScrapped4.setCellValueFactory(cellData -> cellData.getValue().totalScrappedProperty().asObject());
        averageEfficiency4.setCellValueFactory(cellData -> cellData.getValue().averageEfficiencyProperty().asObject());
        averageEfficiency4.setCellFactory(column -> new PercentageTableCell<>());

    }

    private void addSummaryRow(TableView<EmployeeReport> table) {
        double totalEfficiency = 0;
        double totalPiecesGood = 0;
        double totalScrapped = 0;
        int count = 0;

        for (EmployeeReport report : table.getItems()) {
            totalEfficiency += report.getAverageEfficiency();
            totalPiecesGood += report.getTotalPiecesGood();
            totalScrapped += report.getTotalScrapped();
            count++;
        }

        double averageEfficiency = count == 0 ? 0 : totalEfficiency / count;

        // Remove existing summary row if any
        if (!table.getItems().isEmpty() && "Summary".equals(table.getItems().get(table.getItems().size() - 1).getEmployeeName())) {
            table.getItems().remove(table.getItems().size() - 1);
        }
        // Create a summary row with the calculated data
        EmployeeReport summaryReport = new EmployeeReport("Summary", totalPiecesGood, totalScrapped, averageEfficiency);
        table.getItems().add(summaryReport);

    }


    private void updateCompareTopTable(TableView<EmployeeReport> table, ObservableList<ReportRow> reportRows) {
        Map<String, EmployeeReport> employeeDataMap = new HashMap<>();

        for (ReportRow row : reportRows) {
            String employeeName = row.getEmployeeName();
            double piecesGood = row.getPiecesGood();
            double piecesScrapped = row.getPiecesScrapped();
            double efficiency = row.getEfficiency();

            if (employeeDataMap.containsKey(employeeName)) {
                EmployeeReport report = employeeDataMap.get(employeeName);
                report.addPiecesGood(piecesGood);
                report.addScrapped(piecesScrapped);
                report.addEfficiency(efficiency);
            } else {
                employeeDataMap.put(employeeName, new EmployeeReport(employeeName, piecesGood, piecesScrapped, efficiency));
            }

            ObservableList<EmployeeReport> employeeReports = FXCollections.observableArrayList(employeeDataMap.values());
            table.setItems(employeeReports);
        }

    }

    private void tab2() {
        comments.setCellValueFactory(cellData -> cellData.getValue().commentsProperty());
        efficiency.setCellValueFactory(cellData -> cellData.getValue().efficiencyProperty().asObject());
        employeeName.setCellValueFactory(cellData -> cellData.getValue().employeeNameProperty());
        estCycleTime.setCellValueFactory(cellData -> cellData.getValue().estCycleTimeProperty().asObject());
        estimatedPieces.setCellValueFactory(cellData -> cellData.getValue().estimatedPiecesProperty().asObject());
        hoursWorked.setCellValueFactory(cellData -> cellData.getValue().hoursWorkedProperty().asObject());
        partNumber.setCellValueFactory(cellData -> cellData.getValue().partNumberProperty());
        piecesGood.setCellValueFactory(cellData -> cellData.getValue().piecesGoodProperty().asObject());
        piecesScrapped.setCellValueFactory(cellData -> cellData.getValue().piecesScrappedProperty().asObject());
        piecesTotal.setCellValueFactory(cellData -> cellData.getValue().piecesTotalProperty().asObject());
        ticketDate.setCellValueFactory(cellData -> cellData.getValue().ticketDateProperty());

        totalPiecesColumn.setCellValueFactory(cellData -> cellData.getValue().totalPiecesProperty().asObject());
        totalScrappedColumn.setCellValueFactory(cellData -> cellData.getValue().totalScrappedProperty().asObject());
        averageEfficiencyColumn
                .setCellValueFactory(cellData -> cellData.getValue().averageEfficiencyProperty().asObject());

        filter.getItems().add("View All");
        filter.setValue("View All");

        filter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filterTableByEmployee(table, newValue, reportstring);
        });

        efficiency.setCellFactory(column -> new PercentageTableCell<>());
        hoursWorked.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        // estimatedPieces.setCellFactory(TextFieldTableCell.forTableColumn(new
        // DoubleStringConverter()));
        piecesGood.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        piecesScrapped.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        comments.setCellFactory(TextFieldTableCell.forTableColumn());

        hoursWorked.setOnEditCommit(event -> {
            ReportRow row = event.getRowValue();
            row.setHoursWorked(Double.parseDouble(event.getNewValue().toString()));
            updateAggregateData(bottomTable, reportRows, aggregateDataList);
            updateCompareTopTable(compareTopTable, reportRows);
        });

        piecesGood.setOnEditCommit(event -> {
            ReportRow row = event.getRowValue();
            row.setPiecesGood(event.getNewValue());
            updateAggregateData(bottomTable, reportRows, aggregateDataList);
            updateCompareTopTable(compareTopTable, reportRows);
        });
        piecesScrapped.setOnEditCommit(event -> {
            ReportRow row = event.getRowValue();
            row.setPiecesScrapped(event.getNewValue());
            updateAggregateData(bottomTable, reportRows, aggregateDataList);
            updateCompareTopTable(compareTopTable, reportRows);
        });

        table.setItems(reportRows);

        reportRows.addListener((ListChangeListener<ReportRow>) c -> {
            if (!reportRows.isEmpty()) {
                updateAggregateData(bottomTable, reportRows, aggregateDataList);
                updateCompareTopTable(compareTopTable, reportRows);
                addSummaryRow(compareTopTable);
            } else {
                aggregateDataList.clear();
                compareTopTable.getItems().clear();
            }
        });

        removeRowButton.setOnAction(event -> removeSelectedRow(table));
    }

    private void tab3() {
        comments2.setCellValueFactory(cellData -> cellData.getValue().commentsProperty());
        efficiency2.setCellValueFactory(cellData -> cellData.getValue().efficiencyProperty().asObject());
        employeeName2.setCellValueFactory(cellData -> cellData.getValue().employeeNameProperty());
        estCycleTime2.setCellValueFactory(cellData -> cellData.getValue().estCycleTimeProperty().asObject());
        estimatedPieces2.setCellValueFactory(cellData -> cellData.getValue().estimatedPiecesProperty().asObject());
        hoursWorked2.setCellValueFactory(cellData -> cellData.getValue().hoursWorkedProperty().asObject());
        partNumber2.setCellValueFactory(cellData -> cellData.getValue().partNumberProperty());
        piecesGood2.setCellValueFactory(cellData -> cellData.getValue().piecesGoodProperty().asObject());
        piecesScrapped2.setCellValueFactory(cellData -> cellData.getValue().piecesScrappedProperty().asObject());
        piecesTotal2.setCellValueFactory(cellData -> cellData.getValue().piecesTotalProperty().asObject());
        ticketDate2.setCellValueFactory(cellData -> cellData.getValue().ticketDateProperty());

        totalPiecesColumn2.setCellValueFactory(cellData -> cellData.getValue().totalPiecesProperty().asObject());
        totalScrappedColumn2.setCellValueFactory(cellData -> cellData.getValue().totalScrappedProperty().asObject());
        averageEfficiencyColumn2.setCellValueFactory(cellData -> cellData.getValue().averageEfficiencyProperty().asObject());


        filter2.getItems().add("View All");
        filter2.setValue("View All");

        filter2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filterTableByEmployee(table2, newValue, secondreportstring);
        });

        efficiency2.setCellFactory(column -> new PercentageTableCell<>());
        hoursWorked2.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        // estimatedPieces.setCellFactory(TextFieldTableCell.forTableColumn(new
        // DoubleStringConverter()));
        piecesGood2.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        piecesScrapped2.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        comments2.setCellFactory(TextFieldTableCell.forTableColumn());

        hoursWorked2.setOnEditCommit(event -> {
            ReportRow row = event.getRowValue();
            row.setHoursWorked(Double.parseDouble(event.getNewValue().toString()));
            updateAggregateData(bottomTable2, reportRows2, aggregateDataList2);
            updateCompareTopTable(compareBottomTable, reportRows2);
        });

        piecesGood2.setOnEditCommit(event -> {
            ReportRow row = event.getRowValue();
            row.setPiecesGood(event.getNewValue());
            updateAggregateData(bottomTable2, reportRows2, aggregateDataList2);
            updateCompareTopTable(compareBottomTable, reportRows2);
        });
        piecesScrapped2.setOnEditCommit(event -> {
            ReportRow row = event.getRowValue();
            row.setPiecesScrapped(event.getNewValue());
            updateAggregateData(bottomTable2, reportRows2, aggregateDataList2);
            updateCompareTopTable(compareBottomTable, reportRows2);
        });

        table2.setItems(reportRows2);

        reportRows2.addListener((ListChangeListener<ReportRow>) c -> {
            if (!reportRows2.isEmpty()) {
                updateAggregateData(bottomTable2, reportRows2, aggregateDataList2);
                updateCompareTopTable(compareBottomTable, reportRows2);
                addSummaryRow(compareBottomTable);
            } else {
                aggregateDataList2.clear();
            }
        });

        removeRowButton2.setOnAction(event -> removeSelectedRow(table2));
    }

    private void updateAggregateData(TableView<AggregateData> bottomTables, ObservableList<ReportRow> reportRow, ObservableList<AggregateData> aggregateDataLists) {
        double totalGood = 0.0;
        double totalScrapped = 0.0;
        double totalEfficiency = 0.0;

        for (ReportRow row : reportRow) {
            totalGood += row.getPiecesGood();
            totalScrapped += row.getPiecesScrapped();
            totalEfficiency += row.getEfficiency();
        }

        double averageEfficiency = Math.floor(reportRow.isEmpty() ? 0.0 : totalEfficiency / reportRow.size());

        aggregateDataLists.clear();
        aggregateDataLists.add(new AggregateData(totalGood, totalScrapped, averageEfficiency));

        bottomTables.setItems(aggregateDataLists);
    }

    public void setReportData(TableView<ReportRow> tables, ChoiceBox<String> filters, String[][] reportstring) {
        tables.getItems().clear();

        Set<String> uniqueEmployees = new HashSet<>();
        for (String[] row : reportstring) {
            tables.getItems().add(new ReportRow(row));
            uniqueEmployees.add(row[0]);
        }

        filters.getItems().clear();
        filters.getItems().add("View All");
        filters.getItems().addAll(uniqueEmployees);
        filters.setValue("View All");
    }

    private void filterTableByEmployee(TableView<ReportRow> tables, String employee, String[][] report) {
        if ("View All".equals(employee)) {
            tables.getItems().clear();
            for (String[] row : report) {
                tables.getItems().add(new ReportRow(row));
            }
        } else {
            tables.getItems().clear();
            for (String[] row : report) {
                if (row[0].equals(employee)) {
                    tables.getItems().add(new ReportRow(row));
                }
            }
        }
    }

    private void removeSelectedRow(TableView<ReportRow> tables) {
        int selectedIndex = tables.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            // Remove the selected row from the TableView
            tables.getItems().remove(selectedIndex);

            // Remove the selected row from the reportstring array
            List<String[]> list = new ArrayList<>(List.of(reportstring));
            list.remove(selectedIndex);
            reportstring = list.toArray(new String[0][]);
        } else {
            // Show a popup message if no row is selected
            showPopUp("No row selected. Please select a row to remove.");
        }
    }
}
