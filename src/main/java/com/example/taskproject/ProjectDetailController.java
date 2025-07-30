package com.example.taskproject;

import com.example.taskproject.model.ProjectData;
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

    private ProjectData projectData;
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
        System.out.println("Initialize called");
        System.out.println("taskNameCol is null: " + (taskNameCol == null));
        System.out.println("taskTable is null: " + (taskTable == null));

        setupTaskTable();
        addSampleTasks();
    }

    private void setupTaskTable() {
        taskNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        taskDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        taskAssigneeCol.setCellValueFactory(new PropertyValueFactory<>("assignee"));
        taskStartDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        taskDueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        // Custom cell factory for status column with modern styling
        taskStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        taskStatusCol.setCellFactory(column -> new TableCell<Task, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    Label statusLabel = new Label(status);
                    switch (status.toLowerCase()) {
                        case "not started":
                            statusLabel.getStyleClass().add("status-not-started");
                            break;
                        case "in progress":
                            statusLabel.getStyleClass().add("status-in-progress");
                            break;
                        case "done":
                            statusLabel.getStyleClass().add("status-done");
                            break;
                        default:
                            statusLabel.setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #6b7280; " +
                                    "-fx-background-radius: 20; -fx-padding: 4 12 4 12; -fx-font-weight: 600; -fx-font-size: 12px;");
                    }
                    setGraphic(statusLabel);
                    setText(null);
                }
            }
        });

        // Setup actions column with modern styled buttons
        taskActionsCol.setCellFactory(param -> new TableCell<Task, Void>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox hbox = new HBox(8, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("table-action-btn");
                deleteBtn.getStyleClass().add("table-action-btn");
                hbox.setAlignment(Pos.CENTER);

                // Add tooltips for better UX
                editBtn.setTooltip(new Tooltip("Edit Task"));
                deleteBtn.setTooltip(new Tooltip("Delete Task"));

                editBtn.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    showTaskDialog(task, true);
                });

                deleteBtn.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    // Add confirmation dialog
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Task");
                    alert.setHeaderText("Are you sure you want to delete this task?");
                    alert.setContentText("Task: " + task.getName());

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            taskList.remove(task);
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        taskTable.setItems(taskList);

        // Add row selection styling
        taskTable.setRowFactory(tv -> {
            TableRow<Task> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Task task = row.getItem();
                    showTaskDialog(task, true);
                }
            });
            return row;
        });
    }

    private void addSampleTasks() {
        taskList.addAll(Arrays.asList(
                new Task("Design UI Mockups", "Create wireframes and mockups for the project interface", "john.doe@email.com",
                        LocalDate.now().minusDays(1), LocalDate.now().plusDays(3), "In Progress"),
                new Task("Backend API Development", "Develop REST API endpoints for data management", "jane.smith@email.com",
                        LocalDate.now().minusDays(2), LocalDate.now().plusDays(5), "Not Started"),
                new Task("Database Setup", "Configure database schema and initial data", "mike.wilson@email.com",
                        LocalDate.now().minusDays(3), LocalDate.now().plusDays(2), "Done"),
                new Task("User Authentication", "Implement login and registration system", "sarah.johnson@email.com",
                        LocalDate.now(), LocalDate.now().plusDays(4), "In Progress"),
                new Task("Testing & QA", "Comprehensive testing of all components", "david.brown@email.com",
                        LocalDate.now().plusDays(1), LocalDate.now().plusDays(6), "Not Started")
        ));
    }

    public void setProjectData(ProjectData data) {
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

            // Enhanced member information display
            int memberCount = projectData.getMembers().size();
            if (memberCount > 0) {
                projectDuration.setText(memberCount + " Members");
            } else {
                projectDuration.setText("No Members");
            }

            // Enhanced description with member information
            StringBuilder memberNames = new StringBuilder();
            if (memberCount > 0) {
                for (int i = 0; i < Math.min(3, memberCount); i++) {
                    if (i > 0) memberNames.append(", ");
                    memberNames.append(projectData.getMembers().get(i).getName());
                }
                if (memberCount > 3) {
                    memberNames.append(" and ").append(memberCount - 3).append(" more");
                }
            }

            String descriptionText = "🎯 " + projectData.getName() + " is a comprehensive educational project designed to enhance learning experiences. " +
                    "This " + projectData.getDescription().toLowerCase() + " initiative involves " + projectData.getStudents() + " students";

            if (memberCount > 0) {
                descriptionText += " working alongside " + memberCount + " dedicated team members including " + memberNames.toString();
            }

            descriptionText += ". The project emphasizes collaborative learning, practical application, and skill development through interactive engagement and structured activities.";
            projectDescription.setText(descriptionText);

            projectBreadcrumb.setText(projectData.getName());

            // Enhanced progress calculation with animation effect
            double progress = 0.65 + (Math.random() * 0.30); // 65-95% range
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

        // Create main container with modern styling
        VBox mainContainer = new VBox();
        mainContainer.setSpacing(25);
        mainContainer.setPadding(new Insets(35));
        mainContainer.setStyle("-fx-background-color: white; -fx-background-radius: 25; " +
                "-fx-border-color: rgba(226, 232, 240, 0.6); -fx-border-width: 1; -fx-border-radius: 25; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 30, 0, 0, 10);");
        mainContainer.setPrefWidth(550);

        // Modern header
        HBox header = createModernDialogHeader(isEdit ? "Edit Task" : "Create New Task", dialogStage);

        // Form fields with modern styling
        VBox formContainer = new VBox();
        formContainer.setSpacing(25);

        // Task Name with modern styling
        VBox nameGroup = createModernFieldGroup("Task Name", createModernTextField("Enter task name"));
        TextField taskNameField = (TextField) ((VBox) nameGroup.getChildren().get(1)).getChildren().get(0);

        // Task Description
        VBox descGroup = createModernFieldGroup("Description", createModernTextArea("Enter detailed task description"));
        TextArea taskDescField = (TextArea) ((VBox) descGroup.getChildren().get(1)).getChildren().get(0);

        // Date fields in modern layout
        HBox datesRow = new HBox();
        datesRow.setSpacing(20);

        VBox startDateGroup = createModernFieldGroup("Start Date", createModernDatePicker());
        DatePicker startDatePicker = (DatePicker) ((VBox) startDateGroup.getChildren().get(1)).getChildren().get(0);
        startDatePicker.setValue(LocalDate.now());

        VBox dueDateGroup = createModernFieldGroup("Due Date", createModernDatePicker());
        DatePicker dueDatePicker = (DatePicker) ((VBox) dueDateGroup.getChildren().get(1)).getChildren().get(0);
        dueDatePicker.setValue(LocalDate.now().plusDays(7));

        datesRow.getChildren().addAll(startDateGroup, dueDateGroup);

        // Email assignment with enhanced styling
        VBox emailGroup = createModernFieldGroup("Assign To", createModernEmailComboBox());
        ComboBox<String> emailComboBox = (ComboBox<String>) ((VBox) emailGroup.getChildren().get(1)).getChildren().get(0);

        // Status with modern design
        VBox statusGroup = createModernFieldGroup("Status", createModernStatusComboBox());
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

        // Modern buttons
        HBox buttonContainer = createModernDialogButtons(dialogStage, isEdit, () -> {
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

        // Create scene with modern backdrop
        VBox sceneContainer = new VBox();
        sceneContainer.setAlignment(Pos.CENTER);
        sceneContainer.setStyle("-fx-background-color: rgba(30, 41, 59, 0.4);");
        sceneContainer.getChildren().add(mainContainer);

        Scene scene = new Scene(sceneContainer, 650, 800);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private HBox createModernDialogHeader(String title, Stage dialogStage) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);

        // Modern icon container
        VBox iconContainer = new VBox();
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setPrefSize(70, 70);
        iconContainer.setStyle("-fx-background-color: linear-gradient(135deg, rgba(76, 107, 182, 0.1), rgba(99, 102, 241, 0.1)); " +
                "-fx-background-radius: 35; -fx-border-color: rgba(76, 107, 182, 0.2); -fx-border-width: 1; -fx-border-radius: 35;");

        Label iconLabel = new Label("📋");
        iconLabel.setStyle("-fx-font-size: 30px;");
        iconContainer.getChildren().add(iconLabel);

        // Modern title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");

        // Modern close button
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button closeButton = new Button("✕");
        closeButton.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-background-radius: 25; " +
                "-fx-border-color: rgba(226, 232, 240, 0.6); -fx-border-width: 1; -fx-border-radius: 25; " +
                "-fx-text-fill: #64748b; -fx-font-size: 18px; -fx-min-width: 45; -fx-min-height: 45; -fx-cursor: hand;");
        closeButton.setOnAction(e -> dialogStage.close());
        closeButton.setOnMouseEntered(e -> closeButton.setStyle(closeButton.getStyle() +
                "; -fx-background-color: rgba(239, 68, 68, 0.1); -fx-text-fill: #dc2626;"));
        closeButton.setOnMouseExited(e -> closeButton.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-background-radius: 25; " +
                "-fx-border-color: rgba(226, 232, 240, 0.6); -fx-border-width: 1; -fx-border-radius: 25; " +
                "-fx-text-fill: #64748b; -fx-font-size: 18px; -fx-min-width: 45; -fx-min-height: 45; -fx-cursor: hand;"));

        header.getChildren().addAll(iconContainer, titleLabel, spacer, closeButton);
        return header;
    }

    private HBox createModernDialogButtons(Stage dialogStage, boolean isEdit, Runnable onSave, TextField nameField) {
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(20);
        buttonContainer.setAlignment(Pos.CENTER);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-border-color: rgba(203, 213, 225, 0.8); " +
                "-fx-border-radius: 15; -fx-text-fill: #64748b; -fx-font-size: 16px; -fx-font-weight: 500; " +
                "-fx-padding: 16 32 16 32; -fx-cursor: hand;");
        cancelButton.setOnAction(e -> dialogStage.close());

        // Add hover effects
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle(cancelButton.getStyle() +
                "; -fx-background-color: rgba(248, 250, 252, 0.8);"));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: transparent; -fx-border-color: rgba(203, 213, 225, 0.8); " +
                "-fx-border-radius: 15; -fx-text-fill: #64748b; -fx-font-size: 16px; -fx-font-weight: 500; " +
                "-fx-padding: 16 32 16 32; -fx-cursor: hand;"));

        Button saveButton = new Button(isEdit ? "Update Task" : "Create Task");
        saveButton.setStyle("-fx-background-color: linear-gradient(135deg, #4C6BB6, #6366f1); -fx-background-radius: 15; " +
                "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: 600; " +
                "-fx-padding: 16 32 16 32; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(76, 107, 182, 0.4), 15, 0, 0, 5);");

        // Enable/disable save button with modern styling
        saveButton.setDisable(true);
        nameField.textProperty().addListener((obs, old, newVal) -> {
            boolean isEmpty = newVal.trim().isEmpty();
            saveButton.setDisable(isEmpty);
            if (isEmpty) {
                saveButton.setStyle("-fx-background-color: rgba(203, 213, 225, 0.5); -fx-background-radius: 15; " +
                        "-fx-text-fill: rgba(100, 116, 139, 0.7); -fx-font-size: 16px; -fx-font-weight: 600; " +
                        "-fx-padding: 16 32 16 32;");
            } else {
                saveButton.setStyle("-fx-background-color: linear-gradient(135deg, #4C6BB6, #6366f1); -fx-background-radius: 15; " +
                        "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: 600; " +
                        "-fx-padding: 16 32 16 32; -fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(76, 107, 182, 0.4), 15, 0, 0, 5);");
            }
        });

        saveButton.setOnAction(e -> onSave.run());

        buttonContainer.getChildren().addAll(cancelButton, saveButton);
        return buttonContainer;
    }

    private ComboBox<String> createModernEmailComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList(availableUsers));
        comboBox.setEditable(true);
        comboBox.setPromptText("Select or enter email address");
        comboBox.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-border-color: rgba(226, 232, 240, 0.8); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; " +
                "-fx-padding: 16 20 16 20; -fx-font-size: 15px; -fx-font-weight: 500;");
        return comboBox;
    }

    private ComboBox<String> createModernStatusComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList("Not Started", "In Progress", "Done"));
        comboBox.setValue("Not Started");
        comboBox.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-border-color: rgba(226, 232, 240, 0.8); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; " +
                "-fx-padding: 16 20 16 20; -fx-font-size: 15px; -fx-font-weight: 500;");
        return comboBox;
    }

    private VBox createModernFieldGroup(String labelText, javafx.scene.Node field) {
        VBox group = new VBox();
        group.setSpacing(12);

        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");

        VBox fieldContainer = new VBox();
        fieldContainer.getChildren().add(field);

        group.getChildren().addAll(label, fieldContainer);
        return group;
    }

    private TextField createModernTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-border-color: rgba(226, 232, 240, 0.8); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; " +
                "-fx-padding: 16 20 16 20; -fx-font-size: 15px; -fx-font-weight: 500; " +
                "-fx-prompt-text-fill: rgba(100, 116, 139, 0.7);");

        // Add focus effects
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(field.getStyle() + "; -fx-border-color: #4C6BB6; -fx-border-width: 2;");
            } else {
                field.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-border-color: rgba(226, 232, 240, 0.8); " +
                        "-fx-border-radius: 15; -fx-background-radius: 15; " +
                        "-fx-padding: 16 20 16 20; -fx-font-size: 15px; -fx-font-weight: 500; " +
                        "-fx-prompt-text-fill: rgba(100, 116, 139, 0.7);");
            }
        });

        return field;
    }

    private TextArea createModernTextArea(String promptText) {
        TextArea area = new TextArea();
        area.setPromptText(promptText);
        area.setPrefRowCount(4);
        area.setWrapText(true);
        area.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-border-color: rgba(226, 232, 240, 0.8); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; " +
                "-fx-padding: 16 20 16 20; -fx-font-size: 15px; -fx-font-weight: 500; " +
                "-fx-prompt-text-fill: rgba(100, 116, 139, 0.7);");

        // Add focus effects
        area.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                area.setStyle(area.getStyle() + "; -fx-border-color: #4C6BB6; -fx-border-width: 2;");
            } else {
                area.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-border-color: rgba(226, 232, 240, 0.8); " +
                        "-fx-border-radius: 15; -fx-background-radius: 15; " +
                        "-fx-padding: 16 20 16 20; -fx-font-size: 15px; -fx-font-weight: 500; " +
                        "-fx-prompt-text-fill: rgba(100, 116, 139, 0.7);");
            }
        });

        return area;
    }

    private DatePicker createModernDatePicker() {
        DatePicker picker = new DatePicker();
        picker.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-border-color: rgba(226, 232, 240, 0.8); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; " +
                "-fx-padding: 16 20 16 20; -fx-font-size: 15px; -fx-font-weight: 500;");

        // Add focus effects
        picker.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                picker.setStyle(picker.getStyle() + "; -fx-border-color: #4C6BB6; -fx-border-width: 2;");
            } else {
                picker.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-border-color: rgba(226, 232, 240, 0.8); " +
                        "-fx-border-radius: 15; -fx-background-radius: 15; " +
                        "-fx-padding: 16 20 16 20; -fx-font-size: 15px; -fx-font-weight: 500;");
            }
        });

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

    // Enhanced Task model class with modern features
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

        // Getters and Setters with enhanced functionality
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

        // Additional utility methods
        public boolean isOverdue() {
            return LocalDate.now().isAfter(dueDate) && !"Done".equals(getStatus());
        }

        public long getDaysUntilDue() {
            return LocalDate.now().until(dueDate).getDays();
        }

        @Override
        public String toString() {
            return "Task{" +
                    "name='" + getName() + '\'' +
                    ", assignee='" + getAssignee() + '\'' +
                    ", status='" + getStatus() + '\'' +
                    ", dueDate=" + dueDate +
                    '}';
        }
    }
}