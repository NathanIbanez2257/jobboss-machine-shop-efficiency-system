package com.example.machinistapp;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JobNumberData {
    public String[][] runReport(List<String> jobNumbers) throws Exception {

        String format = "%-25s %-25s %-25s %-25s %-25s %-25s %-25s %-25s %-25s %n";
        System.out.println("Job Number Report Initiated");
        String apiKey = api_login();

        Set<String> uniqueSet = new HashSet<>(jobNumbers);
        jobNumbers = new ArrayList<>(uniqueSet);

        List<String[]> mainList = new ArrayList<>();
        String[][] finalList = initialList(mainList, jobNumbers, apiKey);

        finalList= removeColumn(finalList, 4);
        int[] newOrder = {6,5,0,4,1,2,3,7,8};
        finalList = reorderColumns(finalList, newOrder);
        finalList = removeFirstRow(finalList);

        System.out.println("\n\n");
        for (String[] row : finalList) {
            System.out.printf(format, (Object[]) row);
        }
        System.out.print("Finished Job Number Data Script\n");
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
    private static String[][] initialList(List<String[]> mainList, List<String> jobNumberList, String apiKey) throws Exception {
        for (String jobNumber : jobNumberList) {
            JSONObject jsonObject = new JSONObject(extractData(jobNumber, apiKey));
            Thread.sleep(100);
            JSONArray dataArray = jsonObject.getJSONArray("Data");
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObject = dataArray.getJSONObject(i);
                String operatorCode = dataObject.optString("operationCode", null);
                if ("MILL".equalsIgnoreCase(operatorCode) || "LATHE".equalsIgnoreCase(operatorCode)) {
                    mainList.addAll(convertJSONToTableData(mainList, dataObject));
                }
            }
        }
        String[][] resultArray = new String[mainList.size()][];
        mainList.toArray(resultArray);

        return resultArray;

    }
    private static String extractData(String jobNumber, String apiKey) throws Exception {
        String urlString = String.format(
                "https://.mye2shop.com/api/v1/order-routings?jobNumber=%s&fields=actualPiecesGood,actualPiecesScrap,estimatedQuantity,jobNumber,operationCode,partNumber,status,totalActualHours,totalEstimatedHours,workCenter",
                jobNumber);
        return toString(urlString, apiKey);
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
    private static List<String[]> convertJSONToTableData(List<String[]> mainList, JSONObject dataObject) {
        List<String[]> tableData = new ArrayList<>();
        if (mainList.isEmpty()) {
            String[] headers = {"Pieces Good", "Total Estimated Hours", "Total Actual Hours", "Estimated Quantity", "Operation Code",
                    "Pieces Scrapped", "Part Number", "Job Number", "Work Center", "Status"};
            mainList.add(headers);
        }


        String[] row = new String[10];

        row[0] = String.valueOf(dataObject.getInt("actualPiecesGood"));
        row[1] = String.valueOf(Math.round(dataObject.optDouble("totalEstimatedHours", 0.0) * 10) / 10.0);
        row[2] = String.valueOf(Math.round(dataObject.optDouble("totalActualHours", 0.0) * 10) / 10.0);
        row[3] = String.valueOf(dataObject.getInt("estimatedQuantity"));
        row[4] = dataObject.getString("operationCode");
        row[5] = String.valueOf(dataObject.getInt("actualPiecesScrap"));
        row[6] = dataObject.getString("partNumber");
        row[7] = dataObject.getString("jobNumber");
        row[8] = dataObject.getString("workCenter");

        if (dataObject.getInt("actualPiecesGood") > dataObject.getInt("estimatedQuantity")) {
            row[9] = "Finished";
        } else {
            row[9] = "Not Finished";
        }

        tableData.add(row);

        return tableData;
    }
    private static String api_login() throws Exception {
        String urlString = "https://.mye2shop.com/api/v1/Login";
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        String requestBody = "{\"apiKey\": \"\", \"userName\": \"NATHAN\", \"password\": \"\"}";
        byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Length", String.valueOf(requestBodyBytes.length));

        // Enable output for the request body
        connection.setDoOutput(true);

        // Write the request body to the connection's output stream
        connection.getOutputStream().write(requestBodyBytes);

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Print the response body
        JSONObject jsonResponse = new JSONObject(response.toString());

        return jsonResponse.getString("result");
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
