package com.example.taskproject.model;

import javafx.collections.ObservableList;
import java.time.LocalDate;

/**
 * Model class representing project data with all necessary information
 * including members, dates, and project details
 */
public class ProjectData {
    private final String name;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate dueDate;
    private final String category; // Changed from students to category
    private final ObservableList<User> members;
    private final String priorityAndBudget; // Changed from qaCount to priorityAndBudget

    public ProjectData(String name, String description, LocalDate startDate, LocalDate dueDate,
                       String category, ObservableList<User> members, String priorityAndBudget) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.category = category;
        this.members = members;
        this.priorityAndBudget = priorityAndBudget;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getCategory() {
        return category;
    }

    // Keep old method names for backward compatibility
    public String getStudents() {
        return category; // Return category for backward compatibility
    }

    public ObservableList<User> getMembers() {
        return members;
    }

    public String getPriorityAndBudget() {
        return priorityAndBudget;
    }

    // Keep old method name for backward compatibility
    public String getQaCount() {
        return priorityAndBudget; // Return priorityAndBudget for backward compatibility
    }

    @Override
    public String toString() {
        return "ProjectData{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", dueDate=" + dueDate +
                ", category='" + category + '\'' +
                ", members=" + members.size() + " members" +
                ", priorityAndBudget='" + priorityAndBudget + '\'' +
                '}';
    }
}