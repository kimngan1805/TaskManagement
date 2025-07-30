package com.example.taskproject.model;
import javafx.beans.property.*;
import java.time.LocalDate;
public class Task {
    private final StringProperty name;
    private final StringProperty description;
    private final ObjectProperty<LocalDate> dueDate;
    private final StringProperty assignedEmail;
    private final StringProperty status;

    public Task(String name, String description, LocalDate dueDate, String assignedEmail, String status) {
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.assignedEmail = new SimpleStringProperty(assignedEmail);
        this.status = new SimpleStringProperty(status);
    }

    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public ObjectProperty<LocalDate> dueDateProperty() { return dueDate; }
    public StringProperty assignedEmailProperty() { return assignedEmail; }
    public StringProperty statusProperty() { return status; }
}