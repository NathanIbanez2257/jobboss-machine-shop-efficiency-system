package com.machinist.machinist;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class machinist {

    public String[][] runReport(String startDate, String endDate) throws Exception {

        System.out.println("Report Initiated");

        String apiKey = api_login();

        List<Integer> employeeNumList = new ArrayList<>(Arrays.asList(101, 102, 104, 105, 103, 111));
        // List<Integer> employeeNumList = new ArrayList<>(Arrays.asList(101));

        List<String> dates8 = getDatesBetween(startDate, endDate, 8);
        List<String> dates7 = getDatesBetween(startDate, endDate, 7);

        List<String[]> mainList = new ArrayList<>();

        String[][] firstList = initialList(mainList, employeeNumList, dates8, dates7, apiKey);

        List<String> jobNumbers = reportList(firstList, 3);


        List<String> partNumbers = getPartNumbers(jobNumbers, apiKey);



        List<String> operatorCodes = getOperatorCodes(partNumbers, apiKey);


        List<String> cycleTimes = getCycleTimes(partNumbers, apiKey);





        String[][] finalList = appendNewColumn(firstList, "Part Number", partNumbers);
        finalList = appendNewColumn(finalList, "Operator Code", operatorCodes);
        finalList = appendNewColumn(finalList, "Estimated Cycle Time", cycleTimes);

        List<String> estimatedPieces = estimatedPieces(finalList);
        finalList = appendNewColumn(finalList, "Estimated Pieces", estimatedPieces);

        List<String> efficiency = efficiency(finalList);
        finalList = appendNewColumn(finalList, "Efficiency", efficiency);

        List<String> piecesTotal = piecesTotal(finalList);
        finalList = appendNewColumn(finalList, "Pieces Total", piecesTotal);



        finalList = removeColumn(finalList, 0);
        finalList = removeColumn(finalList, 2);
        finalList = removeColumn(finalList, 2);
        finalList = removeColumn(finalList, 7);




        String format = "%-35s %-25s %-25s %-25s %-25s %-25s %-25s %-25s %-25s %-25s %-25s %n";

        int[] newOrder = {1,5,6,4,2,3,10,8,9,0,7};


        finalList = reorderColumns(finalList, newOrder);
        finalList = removeFirstRow(finalList);

        System.out.println("\n\n");
        for (String[] row : finalList) {
            System.out.printf(format, (Object[]) row);
        }

        System.out.printf("Finished Script\n");

        return finalList;


    }


    public String[][] removeFirstRow(String[][] data) {
        if (data == null || data.length <= 1) {
            return new String[0][];
        }

        String[][] result = new String[data.length - 1][];
        System.arraycopy(data, 1, result, 0, data.length - 1);

        return result;
    }






    private static String[][] initialList(List<String[]> mainList, List<Integer> employeeNumList,
                                          List<String> dates8, List<String> dates7, String apiKey) throws Exception {


        try {

            for (Integer employee : employeeNumList) {
                int num = 0;
                for (String date : dates8) {

                    String response = getJobNumber(employee.toString(), date, apiKey);

                    if (response.toString().equals("{\"Data\":[]}")) {

                        response = getJobNumber(employee.toString(), dates7.get(num), apiKey);

                        if (response.toString().equals("{\"Data\":[]}")) {
                        }

                        // successful 7 am
                        else {
                            mainList.addAll(convertJSONToTableData(mainList, response));
                        }

                    }

                    // successfull 8 am
                    else {
                        mainList.addAll(convertJSONToTableData(mainList, response));
                    }

                    num += 1;
                }

            }


            String[][] resultArray = new String[mainList.size()][];
            mainList.toArray(resultArray);

            return resultArray;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new String[0][];
    }

    private static List<String> getDatesBetween(String startDate, String endDate, int hour) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        List<String> dates = new ArrayList<>();

        LocalDate currentDate = start;
        while (!currentDate.isAfter(end)) {

            // dates.add(String.join("", currentDate.toString(), "T08:00:00Z"));
            String formattedDate = String.format("%sT%02d:00:00Z", currentDate.toString(), hour);
            dates.add(formattedDate);
            currentDate = currentDate.plusDays(1);
        }

        return dates;
    }

    private static String getJobNumber(String employeeCode, String ticketDate, String apiKey) throws Exception {
        String urlString = String.format(
                "https://.mye2shop.com/api/v1/time-ticket-details?employeeCode=%s&fields=employeeCode,comments," +
                        "employeeName,jobNumber,workCenter,piecesFinished,piecesScrapped,cycleTime,ticketDate&ticketDate=%s",
                employeeCode, ticketDate);

        String response = toString(urlString, apiKey);

        return response;
    }


    private List<String> reportList(String[][] report, int index) {

        List<String> list = new ArrayList<>();

        for (int i = 1; i < report.length; i++) {
            list.add(report[i][index]); // Assuming the job number is in the 4th column (0-based indexing)
        }

        return list;
    }


    private static String extractPartNumbers(String jobNumber, String apiKey) throws Exception {
        String urlString = String.format(
                "https://.mye2shop.com/api/v1/order-routings?fields=partNumber,jobNumber&jobNumber=%s",
                jobNumber);

        String response = toString(urlString, apiKey);

        return response;


    }

    private static List<String> getPartNumbers(List<String> jobNumbers, String apiKey) throws Exception {
        List<String> partNumbers = new ArrayList<>();

        for (String jobNumber : jobNumbers) {

            JSONObject jsonObject = new JSONObject(extractPartNumbers(jobNumber, apiKey));
            Thread.sleep(100);
            JSONArray dataArray = jsonObject.getJSONArray("Data");

            if (dataArray.length() > 0) {
                JSONObject firstItem = dataArray.getJSONObject(0);
                String partNumber = firstItem.getString("partNumber");
                partNumbers.add(partNumber);
            }
            else{
                System.out.println("fail");
            }
        }

        return partNumbers;
    }


    private static String extractLastData(String partNumber, String apiKey) throws Exception {
        String urlString = String.format(
                "https://.mye2shop.com/api/v1/routings?fields=operatorCode,cycleTime,partNumber&partNumber=%s",
                partNumber);

        String response = toString(urlString, apiKey);

        return response;


    }

    private static List<String> getOperatorCodes(List<String> partNumbers, String apiKey) throws Exception{

        List<String> operatorCodeList = new ArrayList<>();


        for (String partNumber : partNumbers) {

            JSONObject jsonObject = new JSONObject(extractLastData(partNumber, apiKey));
            Thread.sleep(100);
            JSONArray dataArray = jsonObject.getJSONArray("Data");

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObject = dataArray.getJSONObject(i);
                String operatorCode = dataObject.optString("operatorCode", null);

                if ("MILL".equalsIgnoreCase(operatorCode) || "LATHE".equalsIgnoreCase(operatorCode)) {
                    operatorCodeList.add(operatorCode);
                }
            }
        }
        return operatorCodeList;
    }

    private static List<String> getCycleTimes(List<String> partNumbers, String apiKey) throws Exception{


        List<String> cycleTimeList = new ArrayList<>();



        for (String partNumber : partNumbers) {

            JSONObject jsonObject = new JSONObject(extractLastData(partNumber, apiKey));

            Thread.sleep(200);
            JSONArray dataArray = jsonObject.getJSONArray("Data");


            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObject = dataArray.getJSONObject(i);
                String operatorCode = dataObject.optString("operatorCode", null);
                Double cycleTime = ((dataObject.has("cycleTime") ? dataObject.optDouble("cycleTime", 999) : 999) / 60);


                if ("MILL".equalsIgnoreCase(operatorCode) || "LATHE".equalsIgnoreCase(operatorCode)) {
                    BigDecimal bd = new BigDecimal(cycleTime.toString());
                    cycleTimeList.add(bd.setScale(3, RoundingMode.HALF_UP).toString());

                }

            }
        }



        return cycleTimeList;
    }








    private static String[][] appendNewColumn(String[][] originalList, String newColumnTitle, List<String> newColumnData) {
        int numRows = originalList.length;
        int numCols = originalList[0].length;

        // Create a new array with an additional column
        String[][] updatedList = new String[numRows][numCols + 1];

        // Copy the existing data to the new array and add the new column data
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                updatedList[i][j] = originalList[i][j];
            }
            if (i == 0) {
                // Add the new column title to the header row
                updatedList[i][numCols] = newColumnTitle;
            } else {
                // Add the new column data to the data rows
                updatedList[i][numCols] = newColumnData.get(i - 1);
            }
        }

        return updatedList;
    }




    private static String toString(String urlString, String apiKey) throws Exception {

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("HTTP error code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();


        return response.toString();


    }


    // Estimated pieces : cycleTime // EstCycleTime
    // Efficiency : round (piecesFinished / Estimated Pieces)
    // piecesTotal : piecesFinished + pieces scrapped

    private static List<String> piecesTotal(String[][] list) throws Exception {

        List<String> piecesFinished = new ArrayList<>();
        List<String> piecesScrapped = new ArrayList<>();

        List<String> piecesTotal = new ArrayList<>();

        for (int i=1; i < list.length; i++) {
            piecesFinished.add(list[i][5]);
        }

        for (int i=1; i < list.length; i++) {
            piecesScrapped.add(list[i][6]);
        }

        for (int i = 0; i < piecesFinished.size(); i++) {
            try {
                double piecesFinishedValue = Double.parseDouble(piecesFinished.get(i));
                double piecesScrappedValue = Double.parseDouble(piecesScrapped.get(i));

                double result = (piecesFinishedValue + piecesScrappedValue);

                // if (!Double.isFinite(result) || Double.isNaN(result)) {
                //     result = 0.0;
                // }

                piecesTotal.add(Double.toString(result));

            } catch (Exception e) {

                System.out.println(e);

                piecesTotal.add("0");
            }
        }

        // System.out.println("\n\n" + piecesTotal);   // .size()

        return piecesTotal;

    }

    private static List<String> efficiency(String[][] list) throws Exception {

        List<String> estimatedPieces = new ArrayList<>();
        List<String> piecesFinsihed = new ArrayList<>();

        List<String> efficiency = new ArrayList<>();

        for (int i=1; i < list.length; i++) {
            estimatedPieces.add(list[i][12]);
        }

        for (int i=1; i < list.length; i++) {
            piecesFinsihed.add(list[i][5]);
        }

        for (int i = 0; i < estimatedPieces.size(); i++) {
            try {
                double estimatedPiecesValue = Double.parseDouble(estimatedPieces.get(i));
                double piecesFinishedValue = Double.parseDouble(piecesFinsihed.get(i));

                double result = (piecesFinishedValue / estimatedPiecesValue) * 100;

                if (!Double.isFinite(result) || Double.isNaN(result)) {
                    result = 0.0;
                }

                BigDecimal bd = new BigDecimal(result);
                efficiency.add(String.valueOf(bd.setScale(1, RoundingMode.HALF_UP).toString()));

            } catch (Exception e) {

                System.out.println(e);

                efficiency.add("0");
            }
        }

        // System.out.println("\n\n" + efficiency.size());

        return efficiency;

    }


    private static List<String> estimatedPieces(String[][] list) throws Exception {

        List<String> estCycleTime = new ArrayList<>();
        List<String> cycleTime = new ArrayList<>();

        List<String> estimatedPieces = new ArrayList<>();

        for (int i=1; i < list.length; i++) {
            estCycleTime.add(list[i][11]);
        }

        for (int i=1; i < list.length; i++) {
            cycleTime.add(list[i][7]);
        }

        for (int i = 0; i < cycleTime.size(); i++) {
            try {
                double cycleTimeValue = Double.parseDouble(cycleTime.get(i));
                double estCycleTimeValue = Double.parseDouble(estCycleTime.get(i));

                double result = Math.floor(cycleTimeValue / estCycleTimeValue);

                if (!Double.isFinite(result) || Double.isNaN(result)) {
                    result = 0.0;
                }

                BigDecimal bd = new BigDecimal(result);
                estimatedPieces.add(String.valueOf(bd.setScale(1, RoundingMode.HALF_UP).toString()));

            } catch (Exception e) {

                System.out.println(e);

                estimatedPieces.add("0");
            }
        }

        return estimatedPieces;

    }








    private static List<String[]> convertJSONToTableData(List<String[]> mainList, String jsonData) {

        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray dataArray = jsonObject.getJSONArray("Data");

        List<String[]> tableData = new ArrayList<>();

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        if (mainList.isEmpty()) {
            String[] headers = { "Employee Code", "Comments", "Employee Name", "Job Number", "Work Center",
                    "Pieces Good", "Pieces Scrapped", "Hours Worked", "Ticket Date"};
            mainList.add(headers);
        }

        // Populate the array with data from the JSON
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject rowData = dataArray.getJSONObject(i);
            String[] row = new String[9];
            row[0] = String.valueOf(rowData.getInt("employeeCode"));
            row[1] = rowData.optString("comments", ""); // Handle null values
            row[2] = rowData.getString("employeeName");
            row[3] = rowData.getString("jobNumber");
            row[4] = String.valueOf(rowData.getInt("workCenter"));
            row[5] = String.valueOf(rowData.getDouble("piecesFinished"));
            row[6] = String.valueOf(rowData.getDouble("piecesScrapped"));
            row[7] = String.valueOf(Math.round(rowData.optDouble("cycleTime", 0.0) * 10) / 10.0); // Round to the nearest tenth
            // row[7] = String.valueOf(rowData.optDouble("cycleTime", 0.0)); // Handle null values with a default value



            String ticketDateStr = rowData.getString("ticketDate");
            LocalDateTime dateTime = LocalDateTime.parse(ticketDateStr, inputFormatter);
            row[8] = dateTime.format(outputFormatter);



            tableData.add(row);
        }

        return tableData;
    }




    
    private static String api_login() throws Exception {
        String urlString = "https://.mye2shop.com/api/v1/Login";
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

//        String requestBody = "{\"apiKey\": \"", \"userName\": \"NATHAN\", \"password\": \"\"}";
//        byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
//        connection.setRequestProperty("Content-Length", String.valueOf(requestBodyBytes.length));

        // Enable output for the request body
        connection.setDoOutput(true);

        // Write the request body to the connection's output stream
//        connection.getOutputStream().write(requestBodyBytes);

        // Get the response code from the server

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Print the response body

        JSONObject jsonResponse = new JSONObject(response.toString());
        String result = jsonResponse.getString("result");


        return result;
    }



    private static String[][] removeColumn(String[][] originalArray, int columnIndexToRemove) {
        int numRows = originalArray.length;
        int numCols = originalArray[0].length - 1; // Decrease the number of columns

        String[][] newArray = new String[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            int k = 0; // index for the new array columns
            for (int j = 0; j < originalArray[i].length; j++) {
                if (j != columnIndexToRemove) {
                    newArray[i][k++] = originalArray[i][j];
                }
            }
        }

        return newArray;
    }


    private static String[][] reorderColumns(String[][] original, int[] newOrder) {
        int numRows = original.length;
        int numCols = original[0].length;

        String[][] reordered = new String[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                reordered[i][j] = original[i][newOrder[j]];
            }
        }

        return reordered;
    }



}
