package com.example.machinistapp;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.converter.DoubleStringConverter;
import javafx.fxml.FXML;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class MainSceneController {

    @FXML
    private TextField start_date, end_date;
    private final machinist report = new machinist();
    private final JobNumberData jobNumberData = new JobNumberData();
    private final JobNumberData jobNumberData2 = new JobNumberData();
    private final NextWeekData nextWeekData = new NextWeekData();
    List<String> unusedPartNumbers;
    String[][] reportstring, secondreportstring, jobData, jobData2;
    String[] secondReportDates;
    String firstStartDate, firstEndDate, secondStartDate, secondEndDate, startDateFuture, endDateFuture;

    void test(String StartDate, String EndDate) throws Exception {
        TreeMap<String, Set<String>> map = nextWeekData.runReport();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate firstStart = LocalDate.parse(StartDate, formatter);
        LocalDate firstEnd = LocalDate.parse(EndDate, formatter);

        startDateFuture = firstStart.plusDays(7).format(formatter);
        endDateFuture = firstEnd.plusDays(7).format(formatter);
        Label3.setText("Next Week: " + startDateFuture + " - " + endDateFuture);
        setNextWeekData(nextWeekTable, map);
        for(int num = 0; num < 5; num++) {
            switch (num) {
                case 0:
                    monday.setText(String.format("Monday %s", startDateFuture));
                    break;
                case 1:
                    String tuesdayStr = setDateFuture(startDateFuture,1);
                    tuesday.setText(String.format("Tuesday %s", tuesdayStr));
                    break;
                case 2:
                    String wednesdayStr = setDateFuture(startDateFuture,2);
                    wednesday.setText(String.format("Wednesday %s", wednesdayStr));
                    break;
                case 3:
                    String thursdayStr = setDateFuture(startDateFuture,3);
                    thursday.setText(String.format("Thursday %s", thursdayStr));
                    break;
                case 4:
                    String fridayStr = setDateFuture(startDateFuture,4);
                    friday.setText(String.format("Friday %s", fridayStr));
                    break;
            }
        }
    }

    @FXML
    String EndDateInput() {
        return end_date.getText();
    }
    @FXML
    String StartDateInput() {
        return start_date.getText();
    }

    @FXML
    void btnGenerate() {
        System.out.println(StartDateInput() + "  " + EndDateInput());
        showPopUp("This May Take 1-5 Minutes");
        firstStartDate = StartDateInput();
        firstEndDate = EndDateInput();
        secondReportDates = calculateSecondReportDates(StartDateInput(), EndDateInput());
        secondStartDate = secondReportDates[0];
        secondEndDate = secondReportDates[1];

        Service<ReportResult> service = createReportService(StartDateInput(), EndDateInput());
        System.out.println(secondReportDates[0] + " " + secondReportDates[1]);
        service.setOnSucceeded(e -> {
            reportstring = service.getValue().getFinalList();
            // Close the "Please wait" popup and show success message
            showPopUp("Report for " + StartDateInput() + " " + EndDateInput() + " "
                    + "successfully generated!\nSecond report will generate shortly..");
            setReportData(table, filter, reportstring);
            try {
                jobData = jobNumberData.runReport(service.getValue().getJobNumbers());
                setJobData(jobDataTable, jobData);
                Thread.sleep(2000);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            //////////// second report string //////////////////////
            System.out.println(secondStartDate + " " + secondEndDate);
            Service<ReportResult> secondService = createReportService(secondStartDate, secondEndDate);
            secondService.setOnSucceeded(event2 -> {
                secondreportstring = secondService.getValue().getFinalList();
                // showPopUp("Second Report generated successfully.");
                setReportData(table2, filter2, secondreportstring);
                // Handle second report data as needed
                try {
                    jobData2 = jobNumberData2.runReport(secondService.getValue().getJobNumbers());
                    setJobData(jobDataTable2, jobData2);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate firstStart = LocalDate.parse(firstStartDate, formatter);
        LocalDate firstEnd = LocalDate.parse(firstEndDate, formatter);
        startDateFuture = firstStart.plusDays(7).format(formatter);
        endDateFuture = firstEnd.plusDays(7).format(formatter);
        Label1.setText("Start Date: " + firstStartDate + "\tEnd Date: " + firstEndDate);
        Label2.setText("Start Date: " + secondStartDate + "\tEnd Date: " + secondEndDate);
        Label3.setText(startDateFuture + " - " + endDateFuture);
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

        LocalDate firstStart = LocalDate.parse(firstStartDate, formatter);
        LocalDate firstEnd = LocalDate.parse(firstEndDate, formatter);
        String startDateFuture = firstStart.plusDays(7).format(formatter);
        String endDateFuture = firstEnd.plusDays(7).format(formatter);

        showPopUp("This May Take 1-5 Minutes");
        Service<ReportResult> service = createReportService(formattedMonday, formattedFriday);
        service.setOnSucceeded(e -> {
            reportstring = service.getValue().getFinalList();
            // Close the "Please wait" popup and show success message
            showPopUp("Report for last week "
                    + "successfully generated!\nSecond report will generate shortly..");
            setReportData(table, filter, reportstring);

            Task<Void> backgroundTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    test(startDateFuture, endDateFuture);
                    return null;
                }
            };
            backgroundTask.run();


            try {
                jobData = jobNumberData.runReport(service.getValue().getJobNumbers());
                setJobData(jobDataTable, jobData);
                Thread.sleep(2000);

            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            System.out.println(secondReportDates[0] + " " + secondReportDates[1]);
            Service<ReportResult> secondService = createReportService(secondStartDate, secondEndDate);
            secondService.setOnSucceeded(event2 -> {
                secondreportstring = secondService.getValue().getFinalList();
                // showPopUp("Second Report generated successfully.");
                setReportData(table2, filter2, secondreportstring);
                try {
                    jobData2 = jobNumberData2.runReport(secondService.getValue().getJobNumbers());
                    setJobData(jobDataTable2, jobData2);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            secondService.setOnFailed(event2 -> {
                showPopUp("Failed to generate the second report.");
            });
            secondService.start();
        });
        service.setOnFailed(e -> {
            Throwable exception = service.getException(); // Get the exception thrown by the service
            System.err.println("Failed to generate report for last week: " + exception.getMessage());
            exception.printStackTrace();
            showPopUp("Failed to generate report for last week.");
        });
        service.start();


        Label1.setText("Start Date: " + firstStartDate + "    End Date: " + firstEndDate);
        Label2.setText("Start Date: " + secondStartDate + "\tEnd Date: " + secondEndDate);
//        Label3.setText("Next Week: " + startDateFuture + " - " + endDateFuture);
    }

    private Service<ReportResult> createReportService(String startDate, String endDate) {
        return new Service<>() {
            @Override
            protected Task<ReportResult> createTask() {
                return new Task<>() {
                    @Override
                    protected ReportResult call() throws Exception {
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
    private Label Label1, Label2, Label3;
    ///// TABLE ONE MAIN DATA /////////
    @FXML
    private Button removeRowButton;
    @FXML
    private TableView<ReportRow> table;
    @FXML
    private TableView<AggregateData> bottomTable;
    @FXML
    private TableColumn<ReportRow, String> comments, employeeName, partNumber, ticketDate;
    @FXML
    private TableColumn<ReportRow, Double> efficiency, estCycleTime, estimatedPieces, hoursWorked, piecesGood, piecesScrapped, piecesTotal;
    @FXML
    private ChoiceBox<String> filter;
    @FXML
    private TableColumn<AggregateData, Double> totalPiecesColumn, totalScrappedColumn, averageEfficiencyColumn;

    ///// TABLE TWO MAIN DATA /////////
    @FXML
    private Button removeRowButton2;
    @FXML
    private TableView<ReportRow> table2;
    @FXML
    private TableView<AggregateData> bottomTable2;
    @FXML
    private TableColumn<ReportRow, String> comments2, employeeName2, partNumber2, ticketDate2;
    @FXML
    private TableColumn<ReportRow, Double> efficiency2, estCycleTime2, estimatedPieces2, hoursWorked2, piecesGood2, piecesScrapped2, piecesTotal2;
    @FXML
    private ChoiceBox<String> filter2;
    @FXML
    private TableColumn<AggregateData, Double> totalPiecesColumn2, totalScrappedColumn2, averageEfficiencyColumn2;
    private ObservableList<ReportRow> reportRows = FXCollections.observableArrayList();
    private ObservableList<AggregateData> aggregateDataList = FXCollections.observableArrayList();
    private ObservableList<ReportRow> reportRows2 = FXCollections.observableArrayList();
    private ObservableList<AggregateData> aggregateDataList2 = FXCollections.observableArrayList();

    ///// TABLE TOP COMPARE DATA /////////
    @FXML
    private TableView<EmployeeReport> compareTopTable;
    @FXML
    private TableColumn<EmployeeReport, String> employeeName3, totalHours;
    @FXML
    private TableColumn<EmployeeReport, Double> piecesGood3, totalScrapped3, averageEfficiency3;

    ///// TABLE BOTTOM COMPARE DATA /////////
    @FXML
    private TableView<EmployeeReport> compareBottomTable;
    @FXML
    private TableColumn<EmployeeReport, Double> averageEfficiency4, piecesGood4, totalScrapped4;
    @FXML
    private TableColumn<EmployeeReport, String> employeeName4, totalHours2;

    ///// TABLE ONE JOB NUMBER DATA /////////
    @FXML
    private TableView<JobNumberReport> jobDataTable;
    @FXML
    private TableColumn<JobNumberReport, String> jobNumber_D, partNumber_D, workCenter_D, status_D;
    @FXML
    private TableColumn<JobNumberReport, Integer> piecesGood_D, piecesScrapped_D, orderQuantity_D;
    @FXML
    private TableColumn<JobNumberReport, Double> totalEstimatedHours_D, totalActualHours_D;

    ///// TABLE TWO JOB NUMBER DATA /////////
    @FXML
    private TableView<JobNumberReport> jobDataTable2;
    @FXML
    private TableColumn<JobNumberReport, String> jobNumber_D2, partNumber_D2, workCenter_D2, status_D2;
    @FXML
    private TableColumn<JobNumberReport, Integer> piecesGood_D2, piecesScrapped_D2, orderQuantity_D2;
    @FXML
    private TableColumn<JobNumberReport, Double> totalEstimatedHours_D2, totalActualHours_D2;

    ///// TABLE NEXT WEEK DATA /////////
    @FXML
    private TableView<NextWeekReport> nextWeekTable;
    @FXML
    private TableColumn<NextWeekReport, String> machines, monday, tuesday, wednesday, thursday, friday;
    @FXML
    private ListView<String> extra;
    Deque<String> previousPartNumbers = new ArrayDeque<>();
    Deque<TableCell<NextWeekReport, String>> latestCells = new ArrayDeque<>();
    ObservableList<String> observablePartNumbers;

    ///// INITIALIZE TABLES /////////
    @FXML
    void initialize() {
        tab2();
        tab3();
        compareTab();
        jobData();
        tab4();
        drag();
        loadStaticDemoData();
        extra.setOnDragDetected(event -> {
            String selectedItem = extra.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Dragboard dragboard = extra.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(selectedItem);
                dragboard.setContent(content);
                event.consume();
            }
        });
    }
    private void loadStaticDemoData() {
        firstStartDate = FakeReportData.getCurrentStartDate();
        firstEndDate = FakeReportData.getCurrentEndDate();
        secondStartDate = FakeReportData.getPreviousStartDate();
        secondEndDate = FakeReportData.getPreviousEndDate();
        startDateFuture = FakeReportData.getNextWeekStartDate();
        endDateFuture = FakeReportData.getNextWeekEndDate();


        reportstring = FakeReportData.currentReportRows();
        secondreportstring = FakeReportData.previousReportRows();
        jobData = FakeReportData.currentJobRows();
        jobData2 = FakeReportData.previousJobRows();

        setReportData(table, filter, reportstring);
        setReportData(table2, filter2, secondreportstring);
        setJobData(jobDataTable, jobData);
        setJobData(jobDataTable2, jobData2);
        setNextWeekData(nextWeekTable, FakeReportData.nextWeekSchedule());

        updateCompareTopTable(compareTopTable, reportRows);
        addSummaryRow(compareTopTable);
        updateCompareTopTable(compareBottomTable, reportRows2);
        addSummaryRow(compareBottomTable);

        Label1.setText("Start Date: " + firstStartDate + "    End Date: " + firstEndDate);
        Label2.setText("Start Date: " + secondStartDate + "    End Date: " + secondEndDate);
        Label3.setText("Next Week: " + startDateFuture + " - " + endDateFuture);

        monday.setText("Monday " + startDateFuture);
        tuesday.setText("Tuesday " + setDateFuture(startDateFuture, 1));
        wednesday.setText("Wednesday " + setDateFuture(startDateFuture, 2));
        thursday.setText("Thursday " + setDateFuture(startDateFuture, 3));
        friday.setText("Friday " + setDateFuture(startDateFuture, 4));
    }

    private void jobData() {
        jobNumber_D.setCellValueFactory(cellData -> cellData.getValue().jobNumberProperty());
        partNumber_D.setCellValueFactory(cellData -> cellData.getValue().partNumberProperty());
        piecesGood_D.setCellValueFactory(cellData -> cellData.getValue().piecesGoodProperty().asObject());
        piecesScrapped_D.setCellValueFactory(cellData -> cellData.getValue().piecesScrappedProperty().asObject());
        totalEstimatedHours_D.setCellValueFactory(cellData -> cellData.getValue().totalEstimatedHoursProperty().asObject());
        totalActualHours_D.setCellValueFactory(cellData -> cellData.getValue().totalActualHoursProperty().asObject());
        orderQuantity_D.setCellValueFactory(cellData -> cellData.getValue().estimatedQuantityProperty().asObject());
        workCenter_D.setCellValueFactory(cellData -> cellData.getValue().workCenterProperty());
        status_D.setCellValueFactory(cellData -> cellData.getValue().statusProperty());


        jobNumber_D2.setCellValueFactory(cellData -> cellData.getValue().jobNumberProperty());
        partNumber_D2.setCellValueFactory(cellData -> cellData.getValue().partNumberProperty());
        piecesGood_D2.setCellValueFactory(cellData -> cellData.getValue().piecesGoodProperty().asObject());
        piecesScrapped_D2.setCellValueFactory(cellData -> cellData.getValue().piecesScrappedProperty().asObject());
        totalEstimatedHours_D2.setCellValueFactory(cellData -> cellData.getValue().totalEstimatedHoursProperty().asObject());
        totalActualHours_D2.setCellValueFactory(cellData -> cellData.getValue().totalActualHoursProperty().asObject());
        orderQuantity_D2.setCellValueFactory(cellData -> cellData.getValue().estimatedQuantityProperty().asObject());
        workCenter_D2.setCellValueFactory(cellData -> cellData.getValue().workCenterProperty());
        status_D2.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

    }
    private void compareTab() {
        employeeName3.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmployeeName()));
        piecesGood3.setCellValueFactory(cellData -> cellData.getValue().totalPiecesGoodProperty().asObject());
        totalScrapped3.setCellValueFactory(cellData -> cellData.getValue().totalScrappedProperty().asObject());
        averageEfficiency3.setCellValueFactory(cellData -> cellData.getValue().averageEfficiencyProperty().asObject());
        averageEfficiency3.setCellFactory(column -> new PercentageTableCell<>());

        totalHours.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().totalHoursProperty()));

        employeeName4.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmployeeName()));
        piecesGood4.setCellValueFactory(cellData -> cellData.getValue().totalPiecesGoodProperty().asObject());
        totalScrapped4.setCellValueFactory(cellData -> cellData.getValue().totalScrappedProperty().asObject());
        averageEfficiency4.setCellValueFactory(cellData -> cellData.getValue().averageEfficiencyProperty().asObject());
        averageEfficiency4.setCellFactory(column -> new PercentageTableCell<>());

        totalHours2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().totalHoursProperty()));

    }
    private void addSummaryRow(TableView<EmployeeReport> table) {
        double totalEfficiency = 0;
        double totalPiecesGood = 0;
        double totalScrapped = 0;
        double totalHours = 0;
        int count = 0;

        for (EmployeeReport report : table.getItems()) {
            totalEfficiency += report.getAverageEfficiency();
            totalPiecesGood += report.getTotalPiecesGood();
            totalScrapped += report.getTotalScrapped();
            totalHours += report.getTotalHours();

            count++;
        }

        double averageEfficiency = count == 0 ? 0 : totalEfficiency / count;

        // Remove existing summary row if any
        if (!table.getItems().isEmpty() && "Summary".equals(table.getItems().get(table.getItems().size() - 1).getEmployeeName())) {
            table.getItems().remove(table.getItems().size() - 1);
        }
        // Create a summary row with the calculated data
        EmployeeReport summaryReport = new EmployeeReport("Summary", totalPiecesGood, totalScrapped, averageEfficiency, totalHours);
        table.getItems().add(summaryReport);

    }
    private void updateCompareTopTable(TableView<EmployeeReport> table, ObservableList<ReportRow> reportRows) {
        Map<String, EmployeeReport> employeeDataMap = new HashMap<>();

        for (ReportRow row : reportRows) {
            String employeeName = row.getEmployeeName();
            double piecesGood = row.getPiecesGood();
            double piecesScrapped = row.getPiecesScrapped();
            double efficiency = row.getEfficiency();
            double hoursWorked = row.getHoursWorked();

            if (employeeDataMap.containsKey(employeeName)) {
                EmployeeReport report = employeeDataMap.get(employeeName);
                report.addPiecesGood(piecesGood);
                report.addScrapped(piecesScrapped);
                report.addEfficiency(efficiency);
                report.addHoursWorked(hoursWorked);
            } else {
                employeeDataMap.put(employeeName, new EmployeeReport(employeeName, piecesGood, piecesScrapped, efficiency, hoursWorked));
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
        piecesGood.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        piecesScrapped.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        comments.setCellFactory(TextFieldTableCell.forTableColumn());

        hoursWorked.setOnEditCommit(event -> {
            ReportRow row = event.getRowValue();
            row.setHoursWorked(Double.parseDouble(event.getNewValue().toString()));
            updateAggregateData(bottomTable, reportRows, aggregateDataList);
            updateCompareTopTable(compareTopTable, reportRows);
            table.refresh();
        });

        piecesGood.setOnEditCommit(event -> {
            ReportRow row = event.getRowValue();
            row.setPiecesGood(event.getNewValue());
            updateAggregateData(bottomTable, reportRows, aggregateDataList);
            updateCompareTopTable(compareTopTable, reportRows);
            table.refresh();
        });
        piecesScrapped.setOnEditCommit(event -> {
            ReportRow row = event.getRowValue();
            row.setPiecesScrapped(event.getNewValue());
            updateAggregateData(bottomTable, reportRows, aggregateDataList);
            updateCompareTopTable(compareTopTable, reportRows);
            table.refresh();
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

        removeRowButton.setOnAction(event -> removeSelectedRow(table, reportstring));

        table.setRowFactory(tv -> new CustomTableRow());

    }
    private void tab3() {

        table2.setRowFactory(tv -> new CustomTableRow());

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

        piecesGood2.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        piecesScrapped2.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        comments2.setCellFactory(TextFieldTableCell.forTableColumn());

        hoursWorked2.setOnEditCommit(event -> {
            ReportRow row = event.getRowValue();
            row.setHoursWorked(Double.parseDouble(event.getNewValue().toString()));
            updateAggregateData(bottomTable2, reportRows2, aggregateDataList2);
            updateCompareTopTable(compareBottomTable, reportRows2);
            table2.refresh();
        });

        piecesGood2.setOnEditCommit(event -> {
            ReportRow row = event.getRowValue();
            row.setPiecesGood(event.getNewValue());
            updateAggregateData(bottomTable2, reportRows2, aggregateDataList2);
            updateCompareTopTable(compareBottomTable, reportRows2);
            table2.refresh();
        });
        piecesScrapped2.setOnEditCommit(event -> {
            ReportRow row = event.getRowValue();
            row.setPiecesScrapped(event.getNewValue());
            updateAggregateData(bottomTable2, reportRows2, aggregateDataList2);
            updateCompareTopTable(compareBottomTable, reportRows2);
            table2.refresh();
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
        removeRowButton2.setOnAction(event -> removeSelectedRow(table2, secondreportstring));
    }
    private void tab4() {
        nextWeekTable.setRowFactory(tv -> {
            TableRow<NextWeekReport> row = new TableRow<>();
            row.setPrefHeight(80); // Set preferred height for each row (40 is just an example)
            row.setStyle("-fx-font-size: 16px;");
            return row;
        });
        machines.setCellValueFactory(cellData -> cellData.getValue().machineProperty());
        monday.setCellValueFactory(cellData -> cellData.getValue().mondayProperty());
        tuesday.setCellValueFactory(cellData -> cellData.getValue().tuesdayProperty());
        wednesday.setCellValueFactory(cellData -> cellData.getValue().wednesdayProperty());
        thursday.setCellValueFactory(cellData -> cellData.getValue().thursdayProperty());
        friday.setCellValueFactory(cellData -> cellData.getValue().fridayProperty());
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
    public void setReportData(TableView<ReportRow> tables, ChoiceBox<String> filters,  String[][]reportstring) {
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
    public void setJobData(TableView<JobNumberReport> tables, String[][] reportstring) {

        for (String[] row : reportstring) {
            tables.getItems().add(new JobNumberReport(row));
        }

    }
    public void setNextWeekData(TableView<NextWeekReport> tables, TreeMap<String, Set<String>> reportMap) {
        for (String machine: reportMap.keySet()) {
            String monday = "AVAILABLE";
            String tuesday = "AVAILABLE";
            String wednesday = "AVAILABLE";
            String thursday = "AVAILABLE";
            String friday = "AVAILABLE";
            for (int i = 0; i < reportMap.get(machine).size(); i++) {
                Set<String> partNumbersSet = reportMap.get(machine);
                List<String> partNumbers = new ArrayList<>(partNumbersSet);
                switch (i) {
                    case 0: monday = partNumbers.get(i);
                    break;
                    case 1: tuesday = partNumbers.get(i);
                    break;
                    case 2: wednesday = partNumbers.get(i);
                    break;
                    case 3: thursday = partNumbers.get(i);
                    break;
                    case 4: friday = partNumbers.get(i);
                    break;
                }
                if (i > 4) {
                    unusedPartNumbers = partNumbers.subList(5, partNumbers.size());
                    System.out.println("UNUSED");
                    System.out.println(unusedPartNumbers + "\n");
                    break;
                }
            }
            tables.getItems().add(new NextWeekReport(machine, monday, tuesday, wednesday, thursday, friday));
        }
        observablePartNumbers = FXCollections.observableArrayList(unusedPartNumbers);
        extra.setItems(observablePartNumbers);
        extra.setStyle("-fx-font-size: 14px;");
    }
    private void drag(){
        day_drag(monday);
        day_drag(tuesday);
        day_drag(wednesday);
        day_drag(thursday);
        day_drag(friday);
    }

    @FXML
    void undo_button() throws Exception {
        if (!latestCells.isEmpty()) {
            latestCells.getFirst().setText("AVAILABLE");
            observablePartNumbers.addAll(previousPartNumbers.getFirst());
            previousPartNumbers.pop();
            latestCells.pop();
        }
    }
    private void day_drag( TableColumn<NextWeekReport, String> day) {
        day.setCellFactory(column -> {
            TableCell<NextWeekReport, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item);
                }
            };
            cell.setOnDragOver(event -> {
                if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                    if (cell.getItem() != null && cell.getText().equals("AVAILABLE")) {
                        event.acceptTransferModes(TransferMode.MOVE);  // Accept only if it's "AVAILABLE"
                    }
                }
                event.consume();
            });
            cell.setOnDragDropped(event -> {
                Dragboard dragboard = event.getDragboard();
                boolean success = false;

                if (dragboard.hasString()) {
                    String droppedPartNumber = dragboard.getString();
                    NextWeekReport report_t = cell.getTableRow().getItem();

                    if (report_t != null && cell.getItem().equals("AVAILABLE")) {
                        // Set the part number for Monday
                        cell.setText(droppedPartNumber);
                        previousPartNumbers.push(droppedPartNumber);
                        latestCells.push(cell);
                        success = true;
                        // Remove the part number from the ListView (extra)
                        extra.getItems().remove(droppedPartNumber);
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });
            return cell;
        });
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
    private void removeSelectedRow(TableView<ReportRow> tables, String[][] reportstring) {
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
    private String setDateFuture(String date, int daysToAdd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate formatDate = LocalDate.parse(date, formatter);
        return formatDate.plusDays(daysToAdd).format(formatter);
    }
}