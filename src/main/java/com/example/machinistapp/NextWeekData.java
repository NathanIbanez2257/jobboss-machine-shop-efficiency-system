package com.example.machinistapp;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NextWeekData {
    public TreeMap<String, Set<String>> runReport() throws Exception {
        String format = "%-25s %-25s %-25s %n";
        System.out.println("NEXT WEEK REPORT INITIATED");
        String apiKey = api_login();
        String[][] dataList;
        List<String[]> row = new ArrayList<>();
        dataList = initialList(row, apiKey);
        List<String> jobNumbers = reportList(dataList, 0);
        List<String> partNumbers = reportList(dataList, 1);
        List<String> workCenters = reportList(dataList, 2);
        TreeMap<String, Set<String>> map = workCenterMap(jobNumbers, workCenters, partNumbers);
        System.out.println("NEXT WEEK REPORT FINISHED");
        return map;
    }
    private static String extractData(String apiKey) throws Exception {
        String urlString = "https://.mye2shop.com/api/v1/order-routings?fields=status,jobNumber,partNumber,workCenter,operationCode&status=current";
        return toString(urlString, apiKey);
    }
    private static String getPiecesCompleted(String jobNumber, int getPiecesExpected, String apiKey) throws Exception {
        String urlString = String.format(
                "https://.mye2shop.com/api/v1/order-routings?jobNumber=%s&fields=actualPiecesGood,partNumber,jobNumber,estimatedQuantity",
                jobNumber);
        String output = toString(urlString, apiKey);
        JSONObject jsonObject = new JSONObject(output);
        JSONArray dataArray = jsonObject.getJSONArray("Data");
        JSONObject data = dataArray.getJSONObject(0);
        int piecesCompleted = (int) data.get("actualPiecesGood");
        int estimatedQuantity = (int) data.get("estimatedQuantity");
        int piecesFinished = piecesCompleted + getPiecesExpected;
        System.out.println("Pieces Completed: " + piecesCompleted + " Pieces Expected: " + getPiecesExpected);
        System.out.println(data.get("jobNumber") + " " + data.get("partNumber"));
        String test = String.format("%s / %s ",piecesFinished, estimatedQuantity);
        System.out.println(test);
        return test;
    }
    private static String[][] initialList(List<String[]> mainList, String apiKey) throws Exception {
        String data = extractData(apiKey);
        JSONObject jsonObject = new JSONObject(data);
        JSONArray dataArray = jsonObject.getJSONArray("Data");
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject dataObject = dataArray.getJSONObject(i);
            String operatorCode = dataObject.optString("operationCode", null);
            if ("MILL".equalsIgnoreCase(operatorCode) || "LATHE".equalsIgnoreCase(operatorCode)) {
                mainList.addAll(convertJSONToTableData(mainList, dataObject));
            }
        }
        String[][] resultArray = new String[mainList.size()][];
        mainList.toArray(resultArray);
        return resultArray;
    }
    private List<String> reportList(String[][] report, int index) {
        List<String> list = new ArrayList<>();
        for (int i = 1; i < report.length; i++) {
            list.add(report[i][index]); // Assuming the job number is in the 4th column (0-based indexing)
        }
        return list;
    }
    private static int getExpectedPieces(String partNumber, String apiKey) throws Exception {
        String urlString = String.format(
                "https://.mye2shop.com/api/v1/routings?fields=cycleTime,operatorCode&partNumber=%s",
                partNumber);
        String output = toString(urlString, apiKey);
        JSONObject jsonObject = new JSONObject(output);
        JSONArray dataArray = jsonObject.getJSONArray("Data");
        double estimated = 0;
        int convert = 0;
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject dataObject = dataArray.getJSONObject(i);
            String operatorCode = dataObject.optString("operatorCode", null);
            // TODO: MAKE DEFAULT VALUE NOT 0
            Double cycleTime = ((dataObject.has("cycleTime") ? dataObject.optDouble("cycleTime", 999) : 999) / 60);
            if ("MILL".equalsIgnoreCase(operatorCode) || "LATHE".equalsIgnoreCase(operatorCode)) {
                estimated = Math.floor(8 / cycleTime);
                convert = (int) estimated;
                break;
            }
        }
        return convert;
    }
    private TreeMap<String, Set<String>> workCenterMap(List<String> jobNumbers,List<String> workCenters, List<String> partNumbers) throws Exception{
        TreeMap<String, Set<String>> workCenterMap = new TreeMap<>();
        Comparator<String> customComparator = (p1, p2) -> {
            String lastTwoP1 = p1.substring(p1.length() - 2);
            String lastTwoP2 = p2.substring(p2.length() - 2);
            int compareLastTwo = lastTwoP1.compareTo(lastTwoP2);
            if (compareLastTwo != 0) {
                return compareLastTwo;
            } else {
                return p1.compareTo(p2);
            }
        };
        String[] workCenterNames = {"#1", "#2", "#3", "#4", "#5", "#6", "#7", "BROTHER", "TSUGAMI 1", "TSUGAMI 2"};
        for (String wc : workCenterNames) {
            workCenterMap.putIfAbsent(wc, new TreeSet<>(customComparator));
        }
        for (int i = 0; i < partNumbers.size(); i++) {
            String workCenter = workCenters.get(i);
            String partNumber = partNumbers.get(i);
            String jobNumber = jobNumbers.get(i);
            int expectedPieces = getExpectedPieces(partNumber, api_login());
            String piecesFinished = getPiecesCompleted(jobNumber, expectedPieces, api_login());
            if (partNumber.contains("F5")) {
                if (partNumber.contains("008")) {
                    System.out.println(partNumber + " F5 008 Part");
                    if (workCenterMap.get("#3").size() < 5) {
                        workCenter = "#3";
                    } else if (workCenterMap.get("#4").size() < 5) {
                        workCenter = "#4";
                    } else if (workCenterMap.get("#6").size() < 5) {
                        workCenter = "#6";
                    }
                }
                else if (partNumber.contains("012") || partNumber.contains("010")) {
                    System.out.println(partNumber + " F5 012/010 Part");
                    if (workCenterMap.get("#2").size() < 5) {
                        workCenter = "#2";
                    } else if (workCenterMap.get("#5").size() < 5) {
                        workCenter = "#5";
                    } else {
                        System.out.println("FULL");
//                        workCenter = "#6";
                    }
                }
            }
            else if(partNumber.contains("FA75")) {
                if (partNumber.contains("MBV")) {
                    System.out.println(partNumber + " FA75 Part WITH MBV");
                    if (workCenterMap.get("#1").size() < 5) {
                        workCenter = "#1";
                    }
                    else if (workCenterMap.get("#3").size() < 5) {
                        workCenter = "#3";
                    }
                    else if (workCenterMap.get("#4").size() < 5) {
                        workCenter = "#4";
                    }
                    else if (workCenterMap.get("#6").size() < 5) {
                        workCenter = "#6";
                    }
                }
                else if (partNumber.contains("PNB")) {
                    System.out.println(partNumber + " PNB Part");
                    if (workCenterMap.get("TSUGAMI 1").size() < 5) {
                        workCenter = "TSUGAMI 1";
                    } else if (workCenterMap.get("TSUGAMI 2").size() < 5) {
                        workCenter = "TSUGAMI 2";
                    }
                }
                else {
                    System.out.println(partNumber + " FA75 Part");
                    if (workCenterMap.get("#3").size() < 5) {
                        workCenter = "#3";
                    } else if (workCenterMap.get("#4").size() < 5) {
                        workCenter = "#4";
                    } else if (workCenterMap.get("#6").size() < 5) {
                        workCenter = "#6";
                    }
                }

            }
            else if(partNumber.contains("PNB")) {
                System.out.println(partNumber + " PNB Part");
                if (workCenterMap.get("#3").size() < 5) {
                    workCenter = "#3";
                }
                else if (workCenterMap.get("#4").size() < 5) {
                    workCenter = "#4";
                }
                else if (workCenterMap.get("#6").size() < 5) {
                    workCenter = "#6";
                }
            }
            else {
                System.out.println(partNumber + " ELSE PART NUMBER " + workCenter);
            }
            // Add the part number to the corresponding work center's list
            workCenterMap.get(workCenter).add(String.format("%s \nPieces Completed: %s \nPieces Finished: %s", partNumber, expectedPieces, piecesFinished));
        }

        return workCenterMap;
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
            String[] headers = {"Job Number", "Part Number", "Work Center"};
            mainList.add(headers);
        }
        String[] row = new String[3];
        row[0] = dataObject.getString("jobNumber");
        row[1] = dataObject.getString("partNumber");
        row[2] = dataObject.getString("workCenter");
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
}
