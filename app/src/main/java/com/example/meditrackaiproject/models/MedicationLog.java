package com.example.meditrackaiproject.models;

public class MedicationLog {
    private String medName;
    private long timestamp;
    private String status; // Taken, Missed

    public MedicationLog() {}

    public MedicationLog(String medName, long timestamp, String status) {
        this.medName = medName;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getMedName() { return medName; }
    public void setMedName(String medName) { this.medName = medName; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}