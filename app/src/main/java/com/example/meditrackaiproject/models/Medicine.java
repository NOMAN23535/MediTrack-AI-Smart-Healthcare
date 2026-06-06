package com.example.meditrackaiproject.models;

public class Medicine {
    private String name;
    private String dosage;
    private String duration;
    private String frequency;
    private boolean morning;
    private boolean afternoon;
    private boolean evening;
    private boolean night;
    private boolean beforeMeal;
    private boolean afterMeal;
    private boolean beforeSleep;
    private String instructions;
    private int stockQuantity;
    private boolean continuous;
    private boolean notificationsEnabled = true; // New field

    public Medicine() {}

    public Medicine(String name, String dosage, String duration, String frequency) {
        this.name = name;
        this.dosage = dosage;
        this.duration = duration;
        this.frequency = frequency;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public boolean isMorning() { return morning; }
    public void setMorning(boolean morning) { this.morning = morning; }
    public boolean isAfternoon() { return afternoon; }
    public void setAfternoon(boolean afternoon) { this.afternoon = afternoon; }
    public boolean isEvening() { return evening; }
    public void setEvening(boolean evening) { this.evening = evening; }
    public boolean isNight() { return night; }
    public void setNight(boolean night) { this.night = night; }
    public boolean isBeforeMeal() { return beforeMeal; }
    public void setBeforeMeal(boolean beforeMeal) { this.beforeMeal = beforeMeal; }
    public boolean isAfterMeal() { return afterMeal; }
    public void setAfterMeal(boolean afterMeal) { this.afterMeal = afterMeal; }
    public boolean isBeforeSleep() { return beforeSleep; }
    public void setBeforeSleep(boolean beforeSleep) { this.beforeSleep = beforeSleep; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public boolean isContinuous() { return continuous; }
    public void setContinuous(boolean continuous) { this.continuous = continuous; }
    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }
}