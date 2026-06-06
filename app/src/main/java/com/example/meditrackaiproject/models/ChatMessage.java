package com.example.meditrackaiproject.models;

public class ChatMessage {
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";

    private String message;
    private String role;
    private long timestamp;

    public ChatMessage() {}

    public ChatMessage(String message, String role) {
        this.message = message;
        this.role = role;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}