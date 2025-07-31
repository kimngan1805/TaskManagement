package com.example.taskproject.model;


import java.time.LocalDateTime;

public class NoteItem {
    private String title;
    private String description;
    private int totalTasks;
    private int completedTasks;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;
    private String status;

    public NoteItem(String title, String description, int totalTasks, int completedTasks,
                    LocalDateTime lastModified, String status) {
        this.title = title;
        this.description = description;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.createdDate = LocalDateTime.now();
        this.lastModified = lastModified;
        this.status = status;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.lastModified = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.lastModified = LocalDateTime.now();
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.lastModified = LocalDateTime.now();
    }

    public double getCompletionPercentage() {
        if (totalTasks == 0) return 0.0;
        return (double) completedTasks / totalTasks * 100.0;
    }

    @Override
    public String toString() {
        return "NoteItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", totalTasks=" + totalTasks +
                ", completedTasks=" + completedTasks +
                ", status='" + status + '\'' +
                '}';
    }
}
