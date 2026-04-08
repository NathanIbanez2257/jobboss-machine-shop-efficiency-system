package com.example.machinistapp;

import java.util.*;

public final class FakeReportData {
    private FakeReportData() {}

    public static String getCurrentStartDate() { return "08/19/2024"; }
    public static String getCurrentEndDate() { return "08/23/2024"; }
    public static String getPreviousStartDate() { return "08/12/2024"; }
    public static String getPreviousEndDate() { return "08/16/2024"; }
    public static String getNextWeekStartDate() { return "08/26/2024"; }
    public static String getNextWeekEndDate() { return "08/30/2024"; }

    public static String[][] currentReportRows() {
        return new String[][]{
                {"John D", "08/19/2024", "AA-M0000-AA", "9.1", "38", "0", "38", "45", "84.4", "", "0.2"},
                {"John D", "08/20/2024", "AA-M0000-AA", "2.2", "37", "0", "37", "11", "336.3", "", "0.2"},
                {"John D", "08/21/2024", "AA-M0000-AA", "9.1", "36", "0", "36", "45", "80.0", "", "0.2"},
                {"John D", "08/22/2024", "AA-M0000-AA", "9.0", "32", "0", "32", "45", "71.1", "", "0.2"},
                {"John D", "08/23/2024", "AA-M0000-AA", "8.7", "34", "0", "34", "43", "79.0", "", "0.2"},
                {"John Doe", "08/19/2024", "BBB-M1111-BB", "8.6", "17", "0", "17", "21", "80.9", "", "0.4"},
                {"John Doe", "08/20/2024", "BBB-M1111-BB", "8.0", "17", "0", "17", "20", "85.0", "", "0.4"},
                {"John Doe", "08/21/2024", "BBB-M1111-BB", "8.2", "17", "0", "17", "20", "85.0", "", "0.4"},
                {"John Doe", "08/22/2024", "BBB-M1111-BB", "8.6", "18", "0", "18", "21", "85.7", "", "0.4"},
                {"John Doe", "08/23/2024", "BBB-M1111-BB", "104.3", "0", "0", "0", "260", "0.0", "", "0.4"},
                {"Jane Doe", "08/19/2024", "CCC-M2222-CC", "9.3", "28", "0", "28", "37", "75.7", "", "0.25"},
                {"Jane Doe", "08/20/2024", "DDDD-M3333-DD", "3.7", "23", "0", "23", "11", "209.0", "", "0.333"},
                {"Jane Doe", "08/20/2024", "CCC-M2222-CC", "6.3", "28", "0", "28", "25", "112.0", "", "0.25"},
                {"Jane Doe", "08/20/2024", "DDDD-M3333-DD", "6.3", "29", "0", "29", "18", "161.1", "", "0.333"},
                {"Jane Doe", "08/21/2024", "DDDD-M3333-DD", "9.2", "27", "2", "29", "27", "100.0", "", "0.333"},
                {"Jane Doe", "08/21/2024", "CCC-M2222-CC", "5.1", "18", "0", "18", "20", "90.0", "", "0.25"},
                {"Jane Doe", "08/22/2024", "EEEE-M4444-EE", "1.6", "3", "1", "4", "4", "75.0", "Operator error", "0.4"},
                {"Jane Doe", "08/22/2024", "DDDD-M3333-DD", "8.9", "24", "4", "28", "26", "92.3", "", "0.333"},
                {"Jane Doe", "08/23/2024", "EEEE-M4444-EE", "8.8", "18", "0", "18", "22", "81.8", "", "0.4"},
                {"Jane Doe", "08/23/2024", "EEEE-M4444-EE", "9.0", "22", "0", "22", "22", "100.0", "", "0.4"},
                {"Jane Doe", "08/23/2024", "DDDD-M3333-DD", "9.0", "29", "0", "29", "27", "107.4", "", "0.333"}
        };
    }

    public static String[][] previousReportRows() {
        return new String[][]{
                {"Jane D", "08/12/2024", "FFFF-M5555-FF", "1.3", "0", "0", "0", "5", "0.0", "", "0.217"},
                {"Jane D", "08/12/2024", "FFFF-M5555-FF", "8.0", "30", "0", "30", "36", "83.3", "", "0.217"},
                {"Jane D", "08/13/2024", "GG-G6666-6-GG-6", "13.5", "0", "0", "0", "19", "0.0", "", "0.683"},
                {"Jane D", "08/14/2024", "GG-G6666-6-GG-6", "1.2", "5", "0", "5", "1", "500.0", "", "0.683"},
                {"Nathan I", "08/12/2024", "HH-G7777-HH", "2.7", "25", "0", "25", "29", "86.2", "", "0.092"},
                {"Nathan I", "08/13/2024", "JJ-J8888-JJ", "6.6", "100", "0", "100", "56", "178.5", "", "0.117"},
                {"Nathan I", "08/13/2024", "HH-G7777-HH", "8.6", "70", "0", "70", "93", "75.2", "", "0.092"},
                {"Nathan I", "08/14/2024", "JJ-J8888-JJ", "8.9", "0", "0", "0", "76", "0.0", "", "0.117"},
                {"Nathan I", "08/14/2024", "HH-G7777-HH", "8.8", "100", "0", "100", "95", "105.2", "", "0.092"},
                {"Nathan I", "08/15/2024", "JJ-J8888-JJ", "0.0", "65", "0", "65", "0", "0.0", "", "0.117"},
                {"Nathan I", "08/16/2024", "HH-G7777-HH", "7.8", "70", "0", "70", "84", "83.3", "", "0.092"},
                {"John D", "08/12/2024", "KK-K9999-KK", "8.8", "24", "0", "24", "35", "68.5", "", "0.25"},
                {"John D", "08/12/2024", "LL-L9999-LL", "8.8", "16", "0", "16", "35", "45.7", "", "0.25"},
                {"John D", "08/13/2024", "LL-L9999-LL", "4.7", "0", "0", "0", "18", "0.0", "", "0.25"},
                {"John D", "08/13/2024", "KK-K9999-KK", "4.7", "15", "0", "15", "18", "83.3", "", "0.25"},
                {"John D", "08/14/2024", "LL-L9999-LL", "3.3", "20", "0", "20", "13", "153.8", "", "0.25"},
                {"John D", "08/15/2024", "KK-K9999-KK", "8.0", "25", "0", "25", "32", "78.1", "", "0.25"},
                {"Jane Doe", "08/15/2024", "NN-L1111-NN", "8.0", "240", "0", "240", "200", "120.0", "", "0.2"},
                {"John Doe", "08/16/2024", "BBB-M1111-BB", "8.0", "24", "0", "24", "20", "120.0", "", "0.4"}
        };
    }

    public static String[][] currentJobRows() {
        return new String[][]{
                {"10109-04", "OO-O1111-OO", "21", "0", "8.3", "6.4", "250", "#1", "In Queue"},
                {"10132-09", "CCC-M2222-CC", "32", "0", "7.5", "8.0", "300", "#2", "In Process"},
                {"10117-06", "QQ-O1111-QQ", "16", "0", "5.5", "4.8", "175", "#4", "In Queue"},
                {"10132-01", "KK-K9999-KK", "32", "0", "11.7", "8.0", "2100", "#4", "Scheduled"},
                {"10132-02", "AA-M0000-AA", "40", "0", "10.4", "8.0", "1850", "#4", "Scheduled"},
                {"10132-03", "LL-L9999-LL", "32", "0", "8.1", "8.0", "650", "#4", "Scheduled"},
                {"10132-05", "XX-X1111-XX", "28", "0", "9.3", "8.0", "900", "#4", "Scheduled"},
                {"10132-04", "BA-X1111-LL", "17", "0", "6.8", "6.4", "450", "#5", "In Queue"},
                {"10124-01", "NA-X1111-QQ", "26", "0", "8.1", "6.5", "500", "#6", "In Queue"},
                {"10132-13", "UT-X1111-RS", "24", "0", "7.0", "6.9", "950", "#7", "In Queue"},
                {"10109-07", "IT-X3333-CC", "30", "0", "6.2", "6.0", "200", "BROTHER", "In Queue"},
                {"10135-01", "NN-L1111-NN", "240", "0", "15.0", "8.0", "5000", "TSUGAMI 1", "In Process"}
        };
    }

    public static String[][] previousJobRows() {
        return new String[][]{
                {"10088-01", "FFFF-M5555-FF", "30", "0", "7.8", "8.0", "300", "#1", "Completed"},
                {"10091-02", "JJ-J8888-JJ", "100", "0", "6.5", "6.6", "560", "#2", "Completed"},
                {"10094-03", "KK-K9999-KK", "25", "0", "7.9", "8.0", "320", "#3", "Completed"},
                {"10097-04", "LL-L9999-LL", "20", "0", "3.3", "3.3", "130", "#4", "Completed"},
                {"10099-05", "NN-L1111-NN", "240", "0", "14.9", "8.0", "5000", "TSUGAMI 1", "In Process"},
                {"10101-06", "BBB-M1111-BB", "24", "0", "6.8", "8.0", "200", "#7", "Completed"}
        };
    }

    public static TreeMap<String, Set<String>> nextWeekSchedule() {
        TreeMap<String, Set<String>> map = new TreeMap<>();
        map.put("#1", orderedSet(
                "10109-04:\nOO-O1111-OO\nPieces Completed: 21.0",
                "AVAILABLE",
                "AVAILABLE",
                "AVAILABLE",
                "AVAILABLE"
        ));
        map.put("#2", orderedSet(
                "10132-09:\nCCC-M2222-CC\nPieces Completed: 32.0",
                "AVAILABLE",
                "AVAILABLE",
                "AVAILABLE",
                "AVAILABLE"
        ));
        map.put("#4", orderedSet(
                "10117-06:\nQQ-O1111-QQ\nPieces: 16.0",
                "10132-01:\nKK-K9999-KK\nPieces: 32.0",
                "10132-02:\nAA-M0000-AA\nPieces: 40.0",
                "10132-03:\nLL-L9999-LL\nPieces: 32.0",
                "10132-05:\nXX-X1111-XX\nPieces: 28.0",
                "10132-11:\nNB-X3354-AA\nPieces: 30.0",
                "10132-12:\nKJ-Q1234-CC\nPieces: 16.0"
        ));
        map.put("#5", orderedSet(
                "10132-04:\nBA-X1111-LL\nPieces: 17.0",
                "AVAILABLE",
                "AVAILABLE",
                "AVAILABLE",
                "AVAILABLE"
        ));
        map.put("#6", orderedSet(
                "10124-01:\nNA-X1111-QQ\nPieces: 26.0",
                "AVAILABLE",
                "AVAILABLE",
                "AVAILABLE",
                "AVAILABLE"
        ));
        map.put("#7", orderedSet(
                "10132-13:\nUT-X1111-RS\nPieces: 24.0",
                "AVAILABLE",
                "AVAILABLE",
                "AVAILABLE",
                "AVAILABLE"
        ));
        map.put("BROTHER", orderedSet(
                "10109-07:\nIT-X3333-CC\nPieces: 30.0",
                "AVAILABLE",
                "AVAILABLE",
                "AVAILABLE",
                "AVAILABLE"
        ));
        map.put("TSUGAMI 1", orderedSet(
                "10135-01:\nNN-L1111-NN\nPieces: 240.0",
                "AVAILABLE",
                "AVAILABLE",
                "AVAILABLE",
                "AVAILABLE"
        ));
        return map;
    }

    private static Set<String> orderedSet(String... values) {
        return new LinkedHashSet<>(Arrays.asList(values));
    }
}
