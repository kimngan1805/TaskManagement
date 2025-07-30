package com.example.taskproject;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class ProjectDetailController {

    @FXML private Label projectTitle;
    @FXML private Label projectCategory;
    @FXML private Label projectStartDate;
    @FXML private Label projectDueDate;
    @FXML private Label projectStudents;
    @FXML private Label projectDuration;
    @FXML private Label projectDescription;
    @FXML private Label projectBreadcrumb;
    @FXML private Label progressPercentage;
    @FXML private ProgressBar progressBar;

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> taskNameCol;
    @FXML private TableColumn<Task, String> taskDescriptionCol;
    @FXML private TableColumn<Task, String> taskAssigneeCol;
    @FXML private TableColumn<Task, LocalDate> taskStartDateCol;
    @FXML private TableColumn<Task, LocalDate> taskDueDateCol;
    @FXML private TableColumn<Task, String> taskStatusCol;
    @FXML private TableColumn<Task, Void> taskActionsCol;

    private HelloController.ProjectData projectData;
    private final ObservableList<Task> taskList = FXCollections.observableArrayList();

    // Mock user emails for demo
    private final List<String> availableUsers = Arrays.asList(
            "john.doe@email.com",
            "jane.smith@email.com",
            "mike.wilson@email.com",
            "sarah.johnson@email.com",
            "david.brown@email.com"
    );

    @FXML
    private void initialize() {
        setupTaskTable();
        // Add some sample tasks
        addSampleTasks();
    }

    private void setupTaskTable() {
        taskNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        taskDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        taskAssigneeCol.setCellValueFactory(new PropertyValueFactory<>("assignee"));
        taskStartDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        taskDueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        taskStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Setup actions column with edit/delete buttons
        taskActionsCol.setCellFactory(param -> new TableCell<Task, Void>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox hbox = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("table-action-btn");
                deleteBtn.getStyleClass().add("table-action-btn");
                hbox.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    showTaskDialog(task, true);
                });

                deleteBtn.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    taskList.remove(task);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        taskTable.setItems(taskList);
    }

    private void addSampleTasks() {
        taskList.addAll(Arrays.asList(
                new Task("Design UI Mockups", "Create wireframes and mockups", "john.doe@email.com",
                        LocalDate.now(), LocalDate.now().plusDays(3), "In Progress"),
                new Task("Backend API", "Develop REST API endpoints", "jane.smith@email.com",
                        LocalDate.now().minusDays(2), LocalDate.now().plusDays(5), "Not Started"),
                new Task("Database Setup", "Configure database schema", "mike.wilson@email.com",
                        LocalDate.now().minusDays(1), LocalDate.now().plusDays(2), "Done")
        ));
    }

    public void setProjectData(HelloController.ProjectData data) {
        this.projectData = data;
        populateProjectDetails();
    }

    private void populateProjectDetails() {
        if (projectData != null) {
            projectTitle.setText(projectData.getName());
            projectCategory.setText(projectData.getDescription());
            projectStartDate.setText(projectData.getStartDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            projectDueDate.setText(projectData.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            projectStudents.setText(projectData.getStudents() + " Students");
            projectDuration.setText(projectData.getDuration());
            projectDescription.setText("This project focuses on " + projectData.getName() + ". " +
                    projectData.getDescription() + " is designed to help students learn effectively. " +
                    "The project involves collaborative work with " + projectData.getStudents() +
                    " students over a duration of " + projectData.getDuration() + ".");
            projectBreadcrumb.setText(projectData.getName());

            // Set random progress between 60-95%
            double progress = 0.6 + (Math.random() * 0.35);
            progressBar.setProgress(progress);
            progressPercentage.setText(String.format("%.0f%%", progress * 100));
        }
    }

    @FXML
    private void showAddTaskDialog() {
        showTaskDialog(null, false);
    }

    private void showTaskDialog(Task existingTask, boolean isEdit) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(isEdit ? "Edit Task" : "Add New Task");
        dialogStage.setResizable(false);

        // Create main container
        VBox mainContainer = new VBox();
        mainContainer.setSpacing(25);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
        mainContainer.setPrefWidth(500);

        // Header
        HBox header = createDialogHeader(isEdit ? "Edit Task" : "Create New Task", dialogStage);

        // Form fields
        VBox formContainer = new VBox();
        formContainer.setSpacing(20);

        // Task Name
        VBox nameGroup = createFieldGroup("Task Name", createStyledTextField("Enter task name"));
        TextField taskNameField = (TextField) ((VBox) nameGroup.getChildren().get(1)).getChildren().get(0);

        // Task Description
        VBox descGroup = createFieldGroup("Description", createStyledTextArea("Enter task description"));
        TextArea taskDescField = (TextArea) ((VBox) descGroup.getChildren().get(1)).getChildren().get(0);

        // Date fields
        HBox datesRow = new HBox();
        datesRow.setSpacing(15);

        VBox startDateGroup = createFieldGroup("Start Date", createStyledDatePicker());
        DatePicker startDatePicker = (DatePicker) ((VBox) startDateGroup.getChildren().get(1)).getChildren().get(0);
        startDatePicker.setValue(LocalDate.now());

        VBox dueDateGroup = createFieldGroup("Due Date", createStyledDatePicker());
        DatePicker dueDatePicker = (DatePicker) ((VBox) dueDateGroup.getChildren().get(1)).getChildren().get(0);
        dueDatePicker.setValue(LocalDate.now().plusDays(7));

        datesRow.getChildren().addAll(startDateGroup, dueDateGroup);

        // Email assignment
        VBox emailGroup = createFieldGroup("Assign To", createEmailComboBox());
        ComboBox<String> emailComboBox = (ComboBox<String>) ((VBox) emailGroup.getChildren().get(1)).getChildren().get(0);

        // Status
        VBox statusGroup = createFieldGroup("Status", createStatusComboBox());
        ComboBox<String> statusComboBox = (ComboBox<String>) ((VBox) statusGroup.getChildren().get(1)).getChildren().get(0);

        formContainer.getChildren().addAll(nameGroup, descGroup, datesRow, emailGroup, statusGroup);

        // Populate fields if editing
        if (isEdit && existingTask != null) {
            taskNameField.setText(existingTask.getName());
            taskDescField.setText(existingTask.getDescription());
            startDatePicker.setValue(existingTask.getStartDate());
            dueDatePicker.setValue(existingTask.getDueDate());
            emailComboBox.setValue(existingTask.getAssignee());
            statusComboBox.setValue(existingTask.getStatus());
        }

        // Buttons
        HBox buttonContainer = createDialogButtons(dialogStage, isEdit, () -> {
            Task task;
            if (isEdit && existingTask != null) {
                // Update existing task
                existingTask.setName(taskNameField.getText().trim());
                existingTask.setDescription(taskDescField.getText().trim());
                existingTask.setStartDate(startDatePicker.getValue());
                existingTask.setDueDate(dueDatePicker.getValue());
                existingTask.setAssignee(emailComboBox.getValue());
                existingTask.setStatus(statusComboBox.getValue());
                taskTable.refresh();
            } else {
                // Create new task
                task = new Task(
                        taskNameField.getText().trim(),
                        taskDescField.getText().trim(),
                        emailComboBox.getValue(),
                        startDatePicker.getValue(),
                        dueDatePicker.getValue(),
                        statusComboBox.getValue()
                );
                taskList.add(task);
            }
            dialogStage.close();
        }, taskNameField);

        mainContainer.getChildren().addAll(header, formContainer, buttonContainer);

        // Create scene
        VBox sceneContainer = new VBox();
        sceneContainer.setAlignment(Pos.CENTER);
        sceneContainer.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        sceneContainer.getChildren().add(mainContainer);

        Scene scene = new Scene(sceneContainer, 600, 750);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private HBox createDialogHeader(String title, Stage dialogStage) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(15);

        // Icon
        VBox iconContainer = new VBox();
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setPrefSize(60, 60);
        iconContainer.setStyle("-fx-background-color: #e8f2ff; -fx-background-radius: 30;");

        Label iconLabel = new Label("📋");
        iconLabel.setStyle("-fx-font-size: 24px;");
        iconContainer.getChildren().add(iconLabel);

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

        // Close button
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button closeButton = new Button("✕");
        closeButton.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 20; " +
                "-fx-border-color: transparent; -fx-text-fill: #666; " +
                "-fx-font-size: 16px; -fx-min-width: 35; -fx-min-height: 35;");
        closeButton.setOnAction(e -> dialogStage.close());

        header.getChildren().addAll(iconContainer, titleLabel, spacer, closeButton);
        return header;
    }

    private HBox createDialogButtons(Stage dialogStage, boolean isEdit, Runnable onSave, TextField nameField) {
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(15);
        buttonContainer.setAlignment(Pos.CENTER);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-border-color: #d1d5db; " +
                "-fx-border-radius: 10; -fx-text-fill: #6b7280; -fx-font-size: 14px; " +
                "-fx-padding: 12 24 12 24; -fx-cursor: hand;");
        cancelButton.setOnAction(e -> dialogStage.close());

        Button saveButton = new Button(isEdit ? "Update" : "Create");
        saveButton.setStyle("-fx-background-color: #4f46e5; -fx-background-radius: 10; " +
                "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-padding: 12 24 12 24; -fx-cursor: hand;");

        // Enable/disable save button
        saveButton.setDisable(true);
        nameField.textProperty().addListener((obs, old, newVal) -> {
            saveButton.setDisable(newVal.trim().isEmpty());
        });

        saveButton.setOnAction(e -> onSave.run());

        buttonContainer.getChildren().addAll(cancelButton, saveButton);
        return buttonContainer;
    }

    private ComboBox<String> createEmailComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList(availableUsers));
        comboBox.setEditable(true);
        comboBox.setPromptText("Select or enter email");
        comboBox.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #e5e7eb; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-padding: 12 16 12 16; -fx-font-size: 14px;");
        return comboBox;
    }

    private ComboBox<String> createStatusComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList("Not Started", "In Progress", "Done"));
        comboBox.setValue("Not Started");
        comboBox.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #e5e7eb; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-padding: 12 16 12 16; -fx-font-size: 14px;");
        return comboBox;
    }

    private VBox createFieldGroup(String labelText, javafx.scene.Node field) {
        VBox group = new VBox();
        group.setSpacing(8);

        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #374151;");

        VBox fieldContainer = new VBox();
        fieldContainer.getChildren().add(field);

        group.getChildren().addAll(label, fieldContainer);
        return group;
    }

    private TextField createStyledTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #e5e7eb; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-padding: 12 16 12 16; -fx-font-size: 14px;");
        return field;
    }

    private TextArea createStyledTextArea(String promptText) {
        TextArea area = new TextArea();
        area.setPromptText(promptText);
        area.setPrefRowCount(3);
        area.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #e5e7eb; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-padding: 12 16 12 16; -fx-font-size: 14px;");
        return area;
    }

    private DatePicker createStyledDatePicker() {
        DatePicker picker = new DatePicker();
        picker.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #e5e7eb; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-padding: 12 16 12 16; -fx-font-size: 14px;");
        return picker;
    }

    @FXML
    private void goBackHome() {
        try {
            Stage stage = (Stage) projectTitle.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(loader.load(), 1400, 900);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Task model class
    public static class Task {
        private SimpleStringProperty name;
        private SimpleStringProperty description;
        private SimpleStringProperty assignee;
        private LocalDate startDate;
        private LocalDate dueDate;
        private SimpleStringProperty status;

        public Task(String name, String description, String assignee, LocalDate startDate, LocalDate dueDate, String status) {
            this.name = new SimpleStringProperty(name);
            this.description = new SimpleStringProperty(description);
            this.assignee = new SimpleStringProperty(assignee);
            this.startDate = startDate;
            this.dueDate = dueDate;
            this.status = new SimpleStringProperty(status);
        }

        // Getters and Setters
        public String getName() { return name.get(); }
        public void setName(String name) { this.name.set(name); }
        public SimpleStringProperty nameProperty() { return name; }

        public String getDescription() { return description.get(); }
        public void setDescription(String description) { this.description.set(description); }
        public SimpleStringProperty descriptionProperty() { return description; }

        public String getAssignee() { return assignee.get(); }
        public void setAssignee(String assignee) { this.assignee.set(assignee); }
        public SimpleStringProperty assigneeProperty() { return assignee; }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getDueDate() { return dueDate; }
        public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

        public String getStatus() { return status.get(); }
        public void setStatus(String status) { this.status.set(status); }
        public SimpleStringProperty statusProperty() { return status; }
    }
}