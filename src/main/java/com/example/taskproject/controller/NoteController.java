package com.example.taskproject.controller;

import com.example.taskproject.model.NoteItem;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class NoteController {
    // Header elements
    @FXML private Button newNoteBtn;
    @FXML private Button searchBtn;
    @FXML private HBox searchContainer;
    @FXML private TextField searchField;

    // Filter and sort elements
    @FXML private ComboBox<String> filterComboBox;
    @FXML private ComboBox<String> sortComboBox;

    // Notes grid
    @FXML private FlowPane notesGrid;
    @FXML private VBox emptyState;

    private final ObservableList<NoteItem> notesList = FXCollections.observableArrayList();
    private boolean isGridView = true;

    @FXML
    private void initialize() {
        setupComboBoxes();
        setupSearchField();
        addSampleNotes();
        refreshNotesGrid();
    }

    private void setupComboBoxes() {
        filterComboBox.setValue("All Notes");
        sortComboBox.setValue("Last Modified");

        filterComboBox.setOnAction(e -> refreshNotesGrid());
        sortComboBox.setOnAction(e -> refreshNotesGrid());
    }

    private void setupSearchField() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshNotesGrid());
    }

    private void addSampleNotes() {
        notesList.addAll(
                new NoteItem("Project Planning", "Planning and organizing the new mobile app project with team requirements and deadlines.",
                        5, 12, LocalDateTime.now().minusDays(1), "In Progress"),
                new NoteItem("Meeting Notes", "Weekly team meeting notes covering project updates, challenges, and next steps.",
                        8, 8, LocalDateTime.now().minusDays(3), "Completed"),
                new NoteItem("Design Ideas", "Brainstorming session for UI/UX improvements and new feature concepts.",
                        3, 15, LocalDateTime.now().minusDays(2), "Not Started"),
                new NoteItem("Code Review", "Technical review notes and feedback for the latest sprint deliverables.",
                        6, 6, LocalDateTime.now().minusDays(5), "Completed"),
                new NoteItem("Research Notes", "Market research and competitor analysis for product positioning strategy.",
                        4, 20, LocalDateTime.now().minusDays(1), "In Progress")
        );
    }

    private void refreshNotesGrid() {
        notesGrid.getChildren().clear();

        ObservableList<NoteItem> filteredNotes = getFilteredNotes();

        if (filteredNotes.isEmpty()) {
            emptyState.setVisible(true);
            emptyState.setManaged(true);
            notesGrid.setVisible(false);
            notesGrid.setManaged(false);
        } else {
            emptyState.setVisible(false);
            emptyState.setManaged(false);
            notesGrid.setVisible(true);
            notesGrid.setManaged(true);

            for (NoteItem note : filteredNotes) {
                VBox noteCard = createNoteCard(note);
                notesGrid.getChildren().add(noteCard);
            }
        }
    }

    private ObservableList<NoteItem> getFilteredNotes() {
        ObservableList<NoteItem> filtered = FXCollections.observableArrayList(notesList);

        // Apply search filter
        String searchText = searchField.getText();
        if (searchText != null && !searchText.trim().isEmpty()) {
            filtered = filtered.filtered(note ->
                    note.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                            note.getDescription().toLowerCase().contains(searchText.toLowerCase())
            );
        }

        // Apply status filter
        String statusFilter = filterComboBox.getValue();
        if (statusFilter != null && !statusFilter.equals("All Notes")) {
            switch (statusFilter) {
                case "Recent":
                    filtered = filtered.filtered(note ->
                            note.getLastModified().isAfter(LocalDateTime.now().minusDays(7))
                    );
                    break;
                case "Favorites":
                    // Could add a favorite field to NoteItem later
                    break;
                case "Completed":
                    filtered = filtered.filtered(note ->
                            note.getStatus().equalsIgnoreCase("Completed")
                    );
                    break;
                case "In Progress":
                    filtered = filtered.filtered(note ->
                            note.getStatus().equalsIgnoreCase("In Progress")
                    );
                    break;
            }
        }

        // Apply sorting
        String sortBy = sortComboBox.getValue();
        if (sortBy != null) {
            switch (sortBy) {
                case "Last Modified":
                    filtered.sort((a, b) -> b.getLastModified().compareTo(a.getLastModified()));
                    break;
                case "Created Date":
                    filtered.sort((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()));
                    break;
                case "Title A-Z":
                    filtered.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                    break;
                case "Title Z-A":
                    filtered.sort((a, b) -> b.getTitle().compareToIgnoreCase(a.getTitle()));
                    break;
            }
        }

        return filtered;
    }

    private VBox createNoteCard(NoteItem note) {
        VBox card = new VBox();
        card.setSpacing(15);
        card.getStyleClass().add("note-card");

        // Header with icon and actions
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(12);
        header.getStyleClass().add("note-card-header");

        // Note icon
        VBox iconContainer = new VBox();
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.getStyleClass().add("note-icon");
        Label iconLabel = new Label("📝");
        iconLabel.setStyle("-fx-font-size: 20px;");
        iconContainer.getChildren().add(iconLabel);

        // Title and status
        VBox titleContainer = new VBox();
        titleContainer.setSpacing(5);

        Label titleLabel = new Label(note.getTitle());
        titleLabel.getStyleClass().add("note-title");

        Label statusBadge = new Label(note.getStatus());
        statusBadge.getStyleClass().addAll("note-status-badge", "status-" + note.getStatus().toLowerCase().replace(" ", "-"));

        titleContainer.getChildren().addAll(titleLabel, statusBadge);

        header.getChildren().addAll(iconContainer, titleContainer);

        // Description
        Label descLabel = new Label(note.getDescription());
        descLabel.getStyleClass().add("note-description");
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(60);

        // Stats
        HBox stats = new HBox();
        stats.setSpacing(15);
        stats.getStyleClass().add("note-stats");

        VBox tasksStats = new VBox();
        tasksStats.setAlignment(Pos.CENTER);
        tasksStats.getStyleClass().add("note-stat-item");
        Label tasksLabel = new Label("TASKS");
        tasksLabel.getStyleClass().add("note-stat-label");
        Label tasksValue = new Label(String.valueOf(note.getTotalTasks()));
        tasksValue.getStyleClass().add("note-stat-value");
        tasksStats.getChildren().addAll(tasksLabel, tasksValue);

        VBox completedStats = new VBox();
        completedStats.setAlignment(Pos.CENTER);
        completedStats.getStyleClass().add("note-stat-item");
        Label completedLabel = new Label("COMPLETED");
        completedLabel.getStyleClass().add("note-stat-label");
        Label completedValue = new Label(String.valueOf(note.getCompletedTasks()));
        completedValue.getStyleClass().add("note-stat-value");
        completedStats.getChildren().addAll(completedLabel, completedValue);

        stats.getChildren().addAll(tasksStats, completedStats);

        // Footer with date and actions
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setSpacing(10);
        footer.getStyleClass().add("note-footer");

        Label dateLabel = new Label("Modified " + note.getLastModified().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dateLabel.getStyleClass().add("note-date");

        HBox actions = new HBox();
        actions.setSpacing(8);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.getStyleClass().add("note-actions");

        Button openBtn = new Button("📖");
        openBtn.getStyleClass().add("note-action-btn");
        openBtn.setOnAction(e -> openNoteDetail(note));
        openBtn.setTooltip(new Tooltip("Open Note"));

        Button editBtn = new Button("✏️");
        editBtn.getStyleClass().add("note-action-btn");
        editBtn.setOnAction(e -> editNote(note));
        editBtn.setTooltip(new Tooltip("Edit Note"));

        Button deleteBtn = new Button("🗑️");
        deleteBtn.getStyleClass().add("note-action-btn");
        deleteBtn.setOnAction(e -> deleteNote(note));
        deleteBtn.setTooltip(new Tooltip("Delete Note"));

        actions.getChildren().addAll(openBtn, editBtn, deleteBtn);

        footer.getChildren().addAll(dateLabel, actions);

        card.getChildren().addAll(header, descLabel, stats, footer);

        // Add click handler to open note detail
        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                openNoteDetail(note);
            }
        });

        return card;
    }

    @FXML
    private void showNewNoteDialog() {
        showNoteDialog(null, false);
    }

    private void editNote(NoteItem note) {
        showNoteDialog(note, true);
    }

    private void deleteNote(NoteItem note) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Note");
        alert.setHeaderText("Delete \"" + note.getTitle() + "\"?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                notesList.remove(note);
                refreshNotesGrid();
            }
        });
    }

    private void openNoteDetail(NoteItem note) {
        try {
            // Load the existing note-view.fxml (your detailed task management interface)
            Stage stage = (Stage) newNoteBtn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/taskproject/note-view.fxml"));
            Scene scene = new Scene(loader.load(), 1400, 900);
            scene.getStylesheets().add(getClass().getResource("/css/note.css").toExternalForm());

            // Pass the note data to the NoteController if needed
            NoteController controller = loader.getController();
            // You can add a method to set the current note in NoteController

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open note detail view.");
        }
    }

    private void showNoteDialog(NoteItem existingNote, boolean isEdit) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(isEdit ? "Edit Note" : "Create New Note");
        dialogStage.setResizable(false);

        VBox mainContainer = new VBox();
        mainContainer.setSpacing(25);
        mainContainer.setPadding(new Insets(35));
        mainContainer.setStyle("-fx-background-color: white; -fx-background-radius: 25; " +
                "-fx-border-color: rgba(102, 126, 234, 0.2); -fx-border-width: 1; -fx-border-radius: 25; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 30, 0, 0, 10);");
        mainContainer.setPrefWidth(500);

        // Header
        HBox header = createDialogHeader(isEdit ? "Edit Note" : "Create Note", dialogStage);

        // Form fields
        VBox formContainer = new VBox();
        formContainer.setSpacing(20);

        // Note Title
        TextField titleField = createStyledTextField("Enter note title");
        VBox titleGroup = createFieldGroup("Note Title", titleField);

        // Note Description
        TextArea descArea = new TextArea();
        descArea.setPromptText("Enter note description");
        descArea.setPrefRowCount(4);
        descArea.setWrapText(true);
        descArea.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-border-color: rgba(226, 232, 240, 0.8); " +
                "-fx-border-radius: 12; -fx-background-radius: 12; " +
                "-fx-padding: 12 16 12 16; -fx-font-size: 14px; -fx-font-weight: 500;");
        VBox descGroup = createFieldGroup("Description", descArea);

        // Status
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.setItems(FXCollections.observableArrayList("Not Started", "In Progress", "Completed"));
        statusComboBox.setValue("Not Started");
        statusComboBox.setStyle(createStyledTextField("").getStyle());
        VBox statusGroup = createFieldGroup("Status", statusComboBox);

        formContainer.getChildren().addAll(titleGroup, descGroup, statusGroup);

        // Populate fields if editing
        if (isEdit && existingNote != null) {
            titleField.setText(existingNote.getTitle());
            descArea.setText(existingNote.getDescription());
            statusComboBox.setValue(existingNote.getStatus());
        }

        // Buttons
        HBox buttonContainer = createDialogButtons(dialogStage, isEdit, () -> {
            if (titleField.getText().trim().isEmpty()) {
                showAlert("Validation Error", "Please enter a note title.");
                return;
            }

            if (isEdit && existingNote != null) {
                // Update existing note
                existingNote.setTitle(titleField.getText().trim());
                existingNote.setDescription(descArea.getText().trim());
                existingNote.setStatus(statusComboBox.getValue());
                existingNote.setLastModified(LocalDateTime.now());
            } else {
                // Create new note
                NoteItem newNote = new NoteItem(
                        titleField.getText().trim(),
                        descArea.getText().trim(),
                        0, // Initial task count
                        0, // Initial completed tasks
                        LocalDateTime.now(),
                        statusComboBox.getValue()
                );
                notesList.add(newNote);
            }
            refreshNotesGrid();
            dialogStage.close();
        }, titleField);

        mainContainer.getChildren().addAll(header, formContainer, buttonContainer);

        // Create scene
        VBox sceneContainer = new VBox();
        sceneContainer.setAlignment(Pos.CENTER);
        sceneContainer.setStyle("-fx-background-color: rgba(30, 41, 59, 0.4);");
        sceneContainer.getChildren().add(mainContainer);

        Scene scene = new Scene(sceneContainer, 600, 650);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    @FXML
    private void hideSearchBar() {
        searchContainer.setVisible(false);
        searchContainer.setManaged(false);
        searchField.clear();
    }

    @FXML
    private void toggleGridView() {
        isGridView = !isGridView;
        // Could implement list view vs grid view here
        refreshNotesGrid();
    }

    @FXML
    private void goToHome() {
        try {
            Stage stage = (Stage) newNoteBtn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/taskproject/hello-view.fxml"));
            Scene scene = new Scene(loader.load(), 1400, 900);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper methods
    private HBox createDialogHeader(String title, Stage dialogStage) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);

        // Icon
        VBox iconContainer = new VBox();
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setPrefSize(60, 60);
        iconContainer.setStyle("-fx-background-color: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15)); " +
                "-fx-background-radius: 30; -fx-border-color: rgba(102, 126, 234, 0.3); -fx-border-width: 2; -fx-border-radius: 30;");

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

    private HBox createDialogButtons(Stage dialogStage, boolean isEdit, Runnable onSave, TextField titleField) {
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(15);
        buttonContainer.setAlignment(Pos.CENTER);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-border-color: rgba(203, 213, 225, 0.8); " +
                "-fx-border-radius: 12; -fx-text-fill: #64748b; -fx-font-size: 14px; -fx-font-weight: 500; " +
                "-fx-padding: 12 24 12 24; -fx-cursor: hand;");
        cancelButton.setOnAction(e -> dialogStage.close());

        Button saveButton = new Button(isEdit ? "Update" : "Create");
        saveButton.setStyle("-fx-background-color: linear-gradient(135deg, #667eea, #764ba2); -fx-background-radius: 12; " +
                "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; " +
                "-fx-padding: 12 24 12 24; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.4), 12, 0, 0, 4);");

        // Enable/disable save button
        saveButton.setDisable(true);
        titleField.textProperty().addListener((obs, old, newVal) -> {
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}