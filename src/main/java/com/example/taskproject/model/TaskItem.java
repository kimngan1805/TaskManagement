package com.example.taskproject.model;

import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDate;

/**
 * Model class representing a task item in the note management system
 * Used for task tracking, status management, and assignment
 */
public class TaskItem {
    private SimpleStringProperty name;
    private SimpleStringProperty status;
    private SimpleStringProperty assignedTo;
    private LocalDate startDate;
    private LocalDate dueDate;

    public TaskItem(String name, String status, String assignedTo, LocalDate startDate, LocalDate dueDate) {
        this.name = new SimpleStringProperty(name);
        this.status = new SimpleStringProperty(status);
        this.assignedTo = new SimpleStringProperty(assignedTo);
        this.startDate = startDate;
        this.dueDate = dueDate;
    }

    // Getters and Setters
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public String getAssignedTo() {
        return assignedTo.get();
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo.set(assignedTo);
    }

    public SimpleStringProperty assignedToProperty() {
        return assignedTo;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // Utility methods
    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && !"Complete".equals(getStatus());
    }

    public long getDaysUntilDue() {
        return LocalDate.now().until(dueDate).getDays();
    }

    public boolean isComplete() {
        return "Complete".equalsIgnoreCase(getStatus());
    }

    public boolean isInProgress() {
        return "In Progress".equalsIgnoreCase(getStatus());
    }

    public boolean isNotStarted() {
        return "Not Started".equalsIgnoreCase(getStatus());
    }

    @Override
    public String toString() {
        return "TaskItem{" +
                "name='" + getName() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", assignedTo='" + getAssignedTo() + '\'' +
                ", startDate=" + startDate +
                ", dueDate=" + dueDate +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TaskItem taskItem = (TaskItem) obj;
        return getName().equals(taskItem.getName()) &&
                getAssignedTo().equals(taskItem.getAssignedTo()) &&
                startDate.equals(taskItem.startDate);
    }

    @Override
    public int hashCode() {
        return getName().hashCode() + getAssignedTo().hashCode() + startDate.hashCode();
    }
}