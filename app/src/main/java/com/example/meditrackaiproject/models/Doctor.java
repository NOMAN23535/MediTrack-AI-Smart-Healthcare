package com.example.meditrackaiproject.models;

public class Doctor extends User {
    private String specialization;
    private String qualifications;
    private String experience;
    private String status; // Pending, Approved, Rejected

    public Doctor() {
        super();
        setRole("Doctor");
        this.status = "Pending";
    }

    public Doctor(String uid, String fullName, String email, String phone, String specialization, String qualifications, String experience) {
        super(uid, fullName, email, phone, "Doctor");
        this.specialization = specialization;
        this.qualifications = qualifications;
        this.experience = experience;
        this.status = "Pending";
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getQualifications() { return qualifications; }
    public void setQualifications(String qualifications) { this.qualifications = qualifications; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}