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
    private final String students;
    private final ObservableList<User> members;
    private final String qaCount;

    public ProjectData(String name, String description, LocalDate startDate, LocalDate dueDate,
                       String students, ObservableList<User> members, String qaCount) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.students = students;
        this.members = members;
        this.qaCount = qaCount;
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

    public String getStudents() {
        return students;
    }

    public ObservableList<User> getMembers() {
        return members;
    }

    public String getQaCount() {
        return qaCount;
    }

    @Override
    public String toString() {
        return "ProjectData{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", dueDate=" + dueDate +
                ", students='" + students + '\'' +
                ", members=" + members.size() + " members" +
                ", qaCount='" + qaCount + '\'' +
                '}';
    }
}