package com.example.machinistapp;
import java.util.List;

public class ReportResult {
    private final String[][] finalList;
    private final List<String> jobNumbers;
    public ReportResult(String[][] finalList, List<String> jobNumbers) {
        this.finalList = finalList;
        this.jobNumbers = jobNumbers;
    }
    public String[][] getFinalList() {
        return finalList;
    }
    public List<String> getJobNumbers() {
        return jobNumbers;
    }
}
