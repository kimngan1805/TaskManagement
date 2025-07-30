package com.example.taskproject.controller;

import com.example.taskproject.model.TaskItem;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class NoteController {

    // Header elements
    @FXML private Label noteTitle;
    @FXML private Label noteDescription;
    @FXML private Button addTaskBtn;

    // Tab elements
    @FXML private Button allTasksTab;
    @FXML private Button byStatusTab;
    @FXML private Button checklistTab;
    @FXML private StackPane contentArea;

    // All Tasks View
    @FXML private VBox allTasksView;
    @FXML private TextField searchField;
    @FXML private TableView<TaskItem> taskTable;
    @FXML private TableColumn<TaskItem, String> taskNameCol;
    @FXML private TableColumn<TaskItem, String> taskStatusCol;
    @FXML private TableColumn<TaskItem, String> taskAssignCol;
    @FXML private TableColumn<TaskItem, LocalDate> taskStartDateCol;
    @FXML private TableColumn<TaskItem, LocalDate> taskDueDateCol;
    @FXML private TableColumn<TaskItem, Void> taskActionsCol;

    // By Status View (Kanban)
    @FXML private VBox byStatusView;
    @FXML private VBox notStartedTasks;
    @FXML private VBox inProgressTasks;
    @FXML private VBox completeTasks;
    @FXML private Label notStartedCount;
    @FXML private Label inProgressCount;
    @FXML private Label completeCount;

    // Checklist View
    @FXML private VBox checklistView;
    @FXML private VBox checklistContainer;
    @FXML private Label completionProgress;

    private final ObservableList<TaskItem> taskList = FXCollections.observableArrayList();
    private String currentView = "allTasks";

    @FXML
    private void initialize() {
        setupTaskTable();
        setupSearchField();
        addSampleTasks();
        refreshAllViews();
    }

    private void setupTaskTable() {
        taskNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        taskAssignCol.setCellValueFactory(new PropertyValueFactory<>("assignedTo"));
        taskStartDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        taskDueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        // Custom status column with styling
        taskStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        taskStatusCol.setCellFactory(column -> new TableCell<TaskItem, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label statusLabel = new Label(status);
                    switch (status.toLowerCase()) {
                        case "not started":
                            statusLabel.getStyleClass().add("status-not-started");
                            break;
                        case "in progress":
                            statusLabel.getStyleClass().add("status-in-progress");
                            break;
                        case "complete":
                            statusLabel.getStyleClass().add("status-complete");
                            break;
                    }
                    setGraphic(statusLabel);
                    setText(null);
                }
            }
        });

        // Actions column
        taskActionsCol.setCellFactory(param -> new TableCell<TaskItem, Void>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox hbox = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("table-action-btn");
                deleteBtn.getStyleClass().add("table-action-btn");
                hbox.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    TaskItem task = getTableView().getItems().get(getIndex());
                    showEditTaskDialog(task);
                });

                deleteBtn.setOnAction(e -> {
                    TaskItem task = getTableView().getItems().get(getIndex());
                    taskList.remove(task);
                    refreshAllViews();
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

    private void setupSearchField() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                taskTable.setItems(taskList);
            } else {
                ObservableList<TaskItem> filteredList = taskList.filtered(task ->
                        task.getName().toLowerCase().contains(newVal.toLowerCase()) ||
                                task.getAssignedTo().toLowerCase().contains(newVal.toLowerCase()) ||
                                task.getStatus().toLowerCase().contains(newVal.toLowerCase())
                );
                taskTable.setItems(filteredList);
            }
        });
    }

    private void addSampleTasks() {
        taskList.addAll(Arrays.asList(
                new TaskItem("Design UI Components", "Not Started", "John Doe",
                        LocalDate.now(), LocalDate.now().plusDays(5)),
                new TaskItem("Backend API Development", "In Progress", "Jane Smith",
                        LocalDate.now().minusDays(2), LocalDate.now().plusDays(3)),
                new TaskItem("Database Setup", "Complete", "Mike Johnson",
                        LocalDate.now().minusDays(5), LocalDate.now().minusDays(1)),
                new TaskItem("User Authentication", "In Progress", "Sarah Wilson",
                        LocalDate.now().minusDays(1), LocalDate.now().plusDays(2)),
                new TaskItem("Testing & QA", "Not Started", "David Brown",
                        LocalDate.now().plusDays(1), LocalDate.now().plusDays(7))
        ));
    }

    @FXML
    private void showAllTasks() {
        switchToView("allTasks");
        updateTabStyles();
    }

    @FXML
    private void showByStatus() {
        switchToView("byStatus");
        updateTabStyles();
        refreshKanbanView();
    }

    @FXML
    private void showChecklist() {
        switchToView("checklist");
        updateTabStyles();
        refreshChecklistView();
    }

    private void switchToView(String viewName) {
        currentView = viewName;

        // Hide all views
        allTasksView.setVisible(false);
        allTasksView.setManaged(false);
        byStatusView.setVisible(false);
        byStatusView.setManaged(false);
        checklistView.setVisible(false);
        checklistView.setManaged(false);

        // Show selected view
        switch (viewName) {
            case "allTasks":
                allTasksView.setVisible(true);
                allTasksView.setManaged(true);
                break;
            case "byStatus":
                byStatusView.setVisible(true);
                byStatusView.setManaged(true);
                break;
            case "checklist":
                checklistView.setVisible(true);
                checklistView.setManaged(true);
                break;
        }
    }

    private void updateTabStyles() {
        // Remove active class from all tabs
        allTasksTab.getStyleClass().remove("tab-active");
        byStatusTab.getStyleClass().remove("tab-active");
        checklistTab.getStyleClass().remove("tab-active");

        // Add active class to current tab
        switch (currentView) {
            case "allTasks":
                allTasksTab.getStyleClass().add("tab-active");
                break;
            case "byStatus":
                byStatusTab.getStyleClass().add("tab-active");
                break;
            case "checklist":
                checklistTab.getStyleClass().add("tab-active");
                break;
        }
    }

    private void refreshKanbanView() {
        // Clear existing task cards
        notStartedTasks.getChildren().clear();
        inProgressTasks.getChildren().clear();
        completeTasks.getChildren().clear();

        int notStartedCnt = 0, inProgressCnt = 0, completeCnt = 0;

        // Populate kanban columns
        for (TaskItem task : taskList) {
            VBox taskCard = createKanbanTaskCard(task);

            switch (task.getStatus().toLowerCase()) {
                case "not started":
                    notStartedTasks.getChildren().add(taskCard);
                    notStartedCnt++;
                    break;
                case "in progress":
                    inProgressTasks.getChildren().add(taskCard);
                    inProgressCnt++;
                    break;
                case "complete":
                    completeTasks.getChildren().add(taskCard);
                    completeCnt++;
                    break;
            }
        }

        // Update counts
        notStartedCount.setText(String.valueOf(notStartedCnt));
        inProgressCount.setText(String.valueOf(inProgressCnt));
        completeCount.setText(String.valueOf(completeCnt));
    }

    private VBox createKanbanTaskCard(TaskItem task) {
        VBox card = new VBox();
        card.setSpacing(8);
        card.getStyleClass().add("kanban-task-card");
        card.setPadding(new Insets(12));

        Label taskName = new Label(task.getName());
        taskName.getStyleClass().add("kanban-task-name");
        taskName.setWrapText(true);

        Label assignedTo = new Label("👤 " + task.getAssignedTo());
        assignedTo.getStyleClass().add("kanban-task-assign");

        Label dueDate = new Label("📅 " + task.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd")));
        dueDate.getStyleClass().add("kanban-task-date");

        HBox actions = new HBox();
        actions.setSpacing(5);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button editBtn = new Button("✏️");
        editBtn.getStyleClass().add("kanban-action-btn");
        editBtn.setOnAction(e -> showEditTaskDialog(task));

        Button deleteBtn = new Button("🗑️");
        deleteBtn.getStyleClass().add("kanban-action-btn");
        deleteBtn.setOnAction(e -> {
            taskList.remove(task);
            refreshAllViews();
        });

        actions.getChildren().addAll(editBtn, deleteBtn);

        card.getChildren().addAll(taskName, assignedTo, dueDate, actions);
        return card;
    }

    private void refreshChecklistView() {
        checklistContainer.getChildren().clear();

        int completedCount = 0;
        int totalCount = taskList.size();

        for (TaskItem task : taskList) {
            HBox checklistItem = new HBox();
            checklistItem.setSpacing(12);
            checklistItem.setAlignment(Pos.CENTER_LEFT);
            checklistItem.getStyleClass().add("checklist-item");
            checklistItem.setPadding(new Insets(12));

            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(task.getStatus().equalsIgnoreCase("complete"));

            if (checkBox.isSelected()) {
                completedCount++;
            }

            Label taskLabel = new Label(task.getName());
            taskLabel.getStyleClass().add("checklist-task-name");

            // Strike through completed tasks
            if (checkBox.isSelected()) {
                taskLabel.getStyleClass().add("task-completed");
            }

            Label assignLabel = new Label("👤 " + task.getAssignedTo());
            assignLabel.getStyleClass().add("checklist-assign");

            Label dateLabel = new Label("📅 " + task.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            dateLabel.getStyleClass().add("checklist-date");

            checkBox.setOnAction(e -> {
                String newStatus = checkBox.isSelected() ? "Complete" : "Not Started";
                task.setStatus(newStatus);

                if (checkBox.isSelected()) {
                    taskLabel.getStyleClass().add("task-completed");
                } else {
                    taskLabel.getStyleClass().remove("task-completed");
                }

                refreshChecklistView();
                refreshKanbanView();
            });

            checklistItem.getChildren().addAll(checkBox, taskLabel, assignLabel, dateLabel);
            checklistContainer.getChildren().add(checklistItem);
        }

        completionProgress.setText(completedCount + "/" + totalCount + " completed");
    }

    private void refreshAllViews() {
        if (currentView.equals("byStatus")) {
            refreshKanbanView();
        } else if (currentView.equals("checklist")) {
            refreshChecklistView();
        }
    }

    @FXML
    private void showAddTaskDialog() {
        showTaskDialog(null, false);
    }

    private void showEditTaskDialog(TaskItem task) {
        showTaskDialog(task, true);
    }

    @FXML
    private void addTaskToNotStarted() {
        showTaskDialogWithStatus("Not Started");
    }

    @FXML
    private void addTaskToInProgress() {
        showTaskDialogWithStatus("In Progress");
    }

    @FXML
    private void addTaskToComplete() {
        showTaskDialogWithStatus("Complete");
    }

    private void showTaskDialogWithStatus(String status) {
        TaskItem newTask = new TaskItem("", status, "", LocalDate.now(), LocalDate.now().plusDays(7));
        showTaskDialog(newTask, false);
    }

    private void showTaskDialog(TaskItem existingTask, boolean isEdit) {
        // Create modern dialog similar to project dialog
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(isEdit ? "Edit Task" : "Add New Task");
        dialogStage.setResizable(false);

        VBox mainContainer = new VBox();
        mainContainer.setSpacing(25);
        mainContainer.setPadding(new Insets(35));
        mainContainer.setStyle("-fx-background-color: white; -fx-background-radius: 25; " +
                "-fx-border-color: rgba(76, 107, 182, 0.2); -fx-border-width: 1; -fx-border-radius: 25; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 30, 0, 0, 10);");
        mainContainer.setPrefWidth(500);

        // Header
        HBox header = createDialogHeader(isEdit ? "Edit Task" : "Create Task", dialogStage);

        // Form fields
        VBox formContainer = new VBox();
        formContainer.setSpacing(20);

        // Task Name
        TextField taskNameField = createStyledTextField("Enter task name");
        VBox nameGroup = createFieldGroup("Task Name", taskNameField);

        // Assigned To
        TextField assignedToField = createStyledTextField("Enter assignee name");
        VBox assignGroup = createFieldGroup("Assigned To", assignedToField);

        // Status
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.setItems(FXCollections.observableArrayList("Not Started", "In Progress", "Complete"));
        statusComboBox.setValue("Not Started");
        statusComboBox.setStyle(createStyledTextField("").getStyle());
        VBox statusGroup = createFieldGroup("Status", statusComboBox);

        // Date fields
        HBox datesRow = new HBox();
        datesRow.setSpacing(15);

        DatePicker startDatePicker = createStyledDatePicker();
        startDatePicker.setValue(LocalDate.now());
        VBox startDateGroup = createFieldGroup("Start Date", startDatePicker);

        DatePicker dueDatePicker = createStyledDatePicker();
        dueDatePicker.setValue(LocalDate.now().plusDays(7));
        VBox dueDateGroup = createFieldGroup("Due Date", dueDatePicker);

        datesRow.getChildren().addAll(startDateGroup, dueDateGroup);

        formContainer.getChildren().addAll(nameGroup, assignGroup, statusGroup, datesRow);

        // Populate fields if editing
        if (isEdit && existingTask != null) {
            taskNameField.setText(existingTask.getName());
            assignedToField.setText(existingTask.getAssignedTo());
            statusComboBox.setValue(existingTask.getStatus());
            startDatePicker.setValue(existingTask.getStartDate());
            dueDatePicker.setValue(existingTask.getDueDate());
        } else if (existingTask != null) {
            // For new task with preset status
            statusComboBox.setValue(existingTask.getStatus());
        }

        // Buttons
        HBox buttonContainer = createDialogButtons(dialogStage, isEdit, () -> {
            if (isEdit && existingTask != null) {
                // Update existing task
                existingTask.setName(taskNameField.getText().trim());
                existingTask.setAssignedTo(assignedToField.getText().trim());
                existingTask.setStatus(statusComboBox.getValue());
                existingTask.setStartDate(startDatePicker.getValue());
                existingTask.setDueDate(dueDatePicker.getValue());
            } else {
                // Create new task
                TaskItem newTask = new TaskItem(
                        taskNameField.getText().trim(),
                        statusComboBox.getValue(),
                        assignedToField.getText().trim(),
                        startDatePicker.getValue(),
                        dueDatePicker.getValue()
                );
                taskList.add(newTask);
            }
            refreshAllViews();
            dialogStage.close();
        }, taskNameField);

        mainContainer.getChildren().addAll(header, formContainer, buttonContainer);

        // Create scene
        VBox sceneContainer = new VBox();
        sceneContainer.setAlignment(Pos.CENTER);
        sceneContainer.setStyle("-fx-background-color: rgba(30, 41, 59, 0.4);");
        sceneContainer.getChildren().add(mainContainer);

        Scene scene = new Scene(sceneContainer, 600, 700);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private HBox createDialogHeader(String title, Stage dialogStage) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);

        // Icon
        VBox iconContainer = new VBox();
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setPrefSize(60, 60);
        iconContainer.setStyle("-fx-background-color: linear-gradient(135deg, rgba(76, 107, 182, 0.15), rgba(99, 102, 241, 0.15)); " +
                "-fx-background-radius: 30; -fx-border-color: rgba(76, 107, 182, 0.3); -fx-border-width: 2; -fx-border-radius: 30;");

        Label iconLabel = new Label("📝");
        iconLabel.setStyle("-fx-font-size: 24px;");
        iconContainer.getChildren().add(iconLabel);

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");

        // Close button
        Button closeButton = new Button("✕");
        closeButton.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-background-radius: 20; " +
                "-fx-border-color: rgba(226, 232, 240, 0.6); -fx-border-width: 1; -fx-border-radius: 20; " +
                "-fx-text-fill: #64748b; -fx-font-size: 16px; -fx-min-width: 35; -fx-min-height: 35; -fx-cursor: hand;");
        closeButton.setOnAction(e -> dialogStage.close());

        header.getChildren().addAll(iconContainer, titleLabel, closeButton);
        return header;
    }

    private HBox createDialogButtons(Stage dialogStage, boolean isEdit, Runnable onSave, TextField nameField) {
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(15);
        buttonContainer.setAlignment(Pos.CENTER);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-border-color: rgba(203, 213, 225, 0.8); " +
                "-fx-border-radius: 12; -fx-text-fill: #64748b; -fx-font-size: 14px; -fx-font-weight: 500; " +
                "-fx-padding: 12 24 12 24; -fx-cursor: hand;");
        cancelButton.setOnAction(e -> dialogStage.close());

        Button saveButton = new Button(isEdit ? "Update" : "Create");
        saveButton.setStyle("-fx-background-color: linear-gradient(135deg, #4C6BB6, #6366f1); -fx-background-radius: 12; " +
                "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; " +
                "-fx-padding: 12 24 12 24; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(76, 107, 182, 0.4), 12, 0, 0, 4);");

        // Enable/disable save button
        saveButton.setDisable(true);
        nameField.textProperty().addListener((obs, old, newVal) -> {
            saveButton.setDisable(newVal.trim().isEmpty());
        });

        saveButton.setOnAction(e -> onSave.run());

        buttonContainer.getChildren().addAll(cancelButton, saveButton);
        return buttonContainer;
    }

    private VBox createFieldGroup(String labelText, javafx.scene.Node field) {
        VBox group = new VBox();
        group.setSpacing(8);

        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #374151;");

        group.getChildren().addAll(label, field);
        return group;
    }

    private TextField createStyledTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-border-color: rgba(226, 232, 240, 0.8); " +
                "-fx-border-radius: 12; -fx-background-radius: 12; " +
                "-fx-padding: 12 16 12 16; -fx-font-size: 14px; -fx-font-weight: 500;");
        return field;
    }

    private DatePicker createStyledDatePicker() {
        DatePicker picker = new DatePicker();
        picker.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-border-color: rgba(226, 232, 240, 0.8); " +
                "-fx-border-radius: 12; -fx-background-radius: 12; " +
                "-fx-padding: 12 16 12 16; -fx-font-size: 14px; -fx-font-weight: 500;");
        return picker;
    }

    @FXML
    private void goToHome() {
        try {
            Stage stage = (Stage) noteTitle.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(loader.load(), 1400, 900);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}