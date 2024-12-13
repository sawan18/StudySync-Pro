package com.studyplatform.models;

import java.util.Date;

public class Notification {
    private int id;
    private String title;
    private String description;
    private Date timestamp;
    private boolean isRead;
    private boolean isDeleted;

    public Notification() {
        this.timestamp = new Date();
        this.isRead = false;
    }

    public Notification(String title, String description) {
        this.title = title;
        this.description = description;
        this.timestamp = new Date();
        this.isRead = false;
        this.isDeleted = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void markAsRead() { this.isRead = true; }

    public boolean isDeleted() { return isDeleted; }
    public void markAsDeleted() { this.isDeleted = true; }
}
