package com.example.meditrackaiproject.models;

public class Patient extends User {
    private int age;
    private String gender;

    public Patient() {
        super();
        setRole("Patient");
    }

    public Patient(String uid, String fullName, String email, String phone, int age, String gender) {
        super(uid, fullName, email, phone, "Patient");
        this.age = age;
        this.gender = gender;
    }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}