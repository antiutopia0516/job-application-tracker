package com.jobtracker;

import java.time.LocalDate;

public class JobApplication {
    private String company;
    private String position;
    private LocalDate dateApplied;
    private String status;

    public JobApplication(String company, String position, LocalDate dateApplied, String status) {
        this.company = company;
        this.position = position;
        this.dateApplied = dateApplied;
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public String getPosition() {
        return position;
    }

    public LocalDate getDateApplied() {
        return dateApplied;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Company: " + company + ", Position: " + position + ", Applied on: " + dateApplied + ", Status: " + status;
    }

    public String toCSV() {
        return company + "," + position + "," + dateApplied + "," + status;
    }
    
    public static JobApplication fromCSV(String line) {
        String[] parts = line.split(",");
        if (parts.length != 4) throw new IllegalArgumentException("Invalid CSV line: " + line);
        return new JobApplication(
            parts[0].trim(),
            parts[1].trim(),
            LocalDate.parse(parts[2].trim()),
            parts[3].trim()
        );
    }
}

