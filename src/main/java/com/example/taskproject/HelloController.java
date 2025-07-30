package com.example.taskproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class HelloController {

    @FXML
    private GridPane cardsGrid;

    @FXML
    private Label cardCountNumber;

    private int cardCount = 4; // Current card count
    private final String[] cardColors = {"card-purple", "card-orange", "card-yellow", "card-blue"};

    @FXML
    private void handleAddButton() {
        showAddProjectDialog();
    }

    private void showAddProjectDialog() {
        // Create custom stage instead of Dialog
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Add New Project");
        dialogStage.setResizable(false);

        // Create main container
        VBox mainContainer = new VBox();
        mainContainer.setSpacing(20);
        mainContainer.setPadding(new javafx.geometry.Insets(30));
        mainContainer.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
        mainContainer.setPrefWidth(400);

        // Header with icon and close button
        HBox header = new HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        header.setSpacing(15);

        // Icon circle
        VBox iconContainer = new VBox();
        iconContainer.setAlignment(javafx.geometry.Pos.CENTER);
        iconContainer.setPrefSize(60, 60);
        iconContainer.setStyle("-fx-background-color: #e8f2ff; -fx-background-radius: 30;");

        Label iconLabel = new Label("📝");
        iconLabel.setStyle("-fx-font-size: 24px;");
        iconContainer.getChildren().add(iconLabel);

        // Title
        Label titleLabel = new Label("Create Project");
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

        // Form fields container
        VBox formContainer = new VBox();
        formContainer.setSpacing(20);

        // Project Name
        VBox nameGroup = createFieldGroup("Project Name", createStyledTextField("Enter project name"));
        TextField projectNameField = (TextField) ((VBox) nameGroup.getChildren().get(1)).getChildren().get(0);

        // Description
        VBox descGroup = createFieldGroup("Description", createStyledTextArea("Enter project description"));
        TextArea descriptionField = (TextArea) ((VBox) descGroup.getChildren().get(1)).getChildren().get(0);

        // Date fields in a row
        HBox datesRow = new HBox();
        datesRow.setSpacing(15);

        VBox startDateGroup = createFieldGroup("Start Date", createStyledDatePicker());
        DatePicker startDatePicker = (DatePicker) ((VBox) startDateGroup.getChildren().get(1)).getChildren().get(0);
        startDatePicker.setValue(LocalDate.now());

        VBox dueDateGroup = createFieldGroup("Due Date", createStyledDatePicker());
        DatePicker dueDatePicker = (DatePicker) ((VBox) dueDateGroup.getChildren().get(1)).getChildren().get(0);
        dueDatePicker.setValue(LocalDate.now().plusDays(7));

        datesRow.getChildren().addAll(startDateGroup, dueDateGroup);

        // Other fields in a row
        HBox otherFieldsRow = new HBox();
        otherFieldsRow.setSpacing(15);

        VBox studentsGroup = createFieldGroup("Students", createStyledTextField("8"));
        TextField studentsField = (TextField) ((VBox) studentsGroup.getChildren().get(1)).getChildren().get(0);

        VBox durationGroup = createFieldGroup("Duration", createStyledTextField("45 Min"));
        TextField durationField = (TextField) ((VBox) durationGroup.getChildren().get(1)).getChildren().get(0);

        otherFieldsRow.getChildren().addAll(studentsGroup, durationGroup);

        // Q&A field
        VBox qaGroup = createFieldGroup("Q&A Count", createStyledTextField("20"));
        TextField qaField = (TextField) ((VBox) qaGroup.getChildren().get(1)).getChildren().get(0);

        formContainer.getChildren().addAll(nameGroup, descGroup, datesRow, otherFieldsRow, qaGroup);

        // Buttons
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(15);
        buttonContainer.setAlignment(javafx.geometry.Pos.CENTER);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-border-color: #d1d5db; " +
                "-fx-border-radius: 10; -fx-text-fill: #6b7280; -fx-font-size: 14px; " +
                "-fx-padding: 12 24 12 24; -fx-cursor: hand;");
        cancelButton.setOnAction(e -> dialogStage.close());

        Button saveButton = new Button("Create");
        saveButton.setStyle("-fx-background-color: #4f46e5; -fx-background-radius: 10; " +
                "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-padding: 12 24 12 24; -fx-cursor: hand;");

        // Enable/disable save button
        saveButton.setDisable(true);
        projectNameField.textProperty().addListener((obs, old, newVal) -> {
            saveButton.setDisable(newVal.trim().isEmpty());
        });

        saveButton.setOnAction(e -> {
            ProjectData project = new ProjectData(
                    projectNameField.getText().trim(),
                    descriptionField.getText().trim(),
                    startDatePicker.getValue(),
                    dueDatePicker.getValue(),
                    studentsField.getText().trim(),
                    durationField.getText().trim(),
                    qaField.getText().trim()
            );
            addNewCard(project);
            dialogStage.close();
        });

        buttonContainer.getChildren().addAll(cancelButton, saveButton);

        mainContainer.getChildren().addAll(header, formContainer, buttonContainer);

        // Create scene with padding
        VBox sceneContainer = new VBox();
        sceneContainer.setAlignment(javafx.geometry.Pos.CENTER);
        sceneContainer.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
        sceneContainer.getChildren().add(mainContainer);

        Scene scene = new Scene(sceneContainer, 500, 700);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
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

    private void addNewCard(ProjectData project) {
        // Create new card
        VBox newCard = createNewCard(project);

        // Calculate position in grid
        int currentCards = cardCount;
        int row = currentCards / 3;
        int col = currentCards % 3;

        // Add new row constraints if needed
        if (col == 0 && row > 0) {
            cardsGrid.getRowConstraints().add(new javafx.scene.layout.RowConstraints());
        }

        // Add card to grid
        cardsGrid.add(newCard, col, row);

        // Update card count
        cardCount++;
        cardCountNumber.setText(String.format("%02d", cardCount));
    }

    private VBox createNewCard(ProjectData project) {
        VBox card = new VBox();
        card.setSpacing(10.0);

        // Set card style class with color
        String colorClass = cardColors[(cardCount - 4) % cardColors.length];
        card.getStyleClass().addAll("game-card", colorClass);

        // Add click handler to navigate to detail page
        card.setOnMouseClicked(event -> navigateToProjectDetail(project));
        card.setStyle(card.getStyle() + "; -fx-cursor: hand;");

        // Menu button
        HBox menuBox = new HBox();
        menuBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        Label menuLabel = new Label("⋯");
        menuBox.getChildren().add(menuLabel);

        // Title and category
        VBox titleBox = new VBox();
        titleBox.setSpacing(5.0);

        Label titleLabel = new Label(project.getName());
        titleLabel.getStyleClass().add("card-title");

        Label categoryLabel = new Label(project.getDescription());
        categoryLabel.getStyleClass().add("card-category");

        titleBox.getChildren().addAll(titleLabel, categoryLabel);

        // Info section
        VBox infoBox = new VBox();
        infoBox.setSpacing(8.0);

        // Date and students info
        HBox dateStudentsBox = new HBox();
        dateStudentsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        dateStudentsBox.setSpacing(5.0);

        Label dateIcon = new Label("📅");
        Label dateLabel = new Label(project.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dateLabel.getStyleClass().add("card-info");

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, javafx.scene.layout.Priority.ALWAYS);

        Label studentsIcon = new Label("👥");
        Label studentsLabel = new Label("Students: " + project.getStudents());
        studentsLabel.getStyleClass().add("card-info");

        dateStudentsBox.getChildren().addAll(dateIcon, dateLabel, spacer1, studentsIcon, studentsLabel);

        // Duration and Q&A info
        HBox durationQABox = new HBox();
        durationQABox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        durationQABox.setSpacing(5.0);

        Label durationIcon = new Label("⏱️");
        Label durationLabel = new Label(project.getDuration());
        durationLabel.getStyleClass().add("card-info");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, javafx.scene.layout.Priority.ALWAYS);

        Label qaIcon = new Label("❓");
        Label qaLabel = new Label("Q&A: " + project.getQaCount());
        qaLabel.getStyleClass().add("card-info");

        durationQABox.getChildren().addAll(durationIcon, durationLabel, spacer2, qaIcon, qaLabel);

        infoBox.getChildren().addAll(dateStudentsBox, durationQABox);

        card.getChildren().addAll(menuBox, titleBox, infoBox);

        return card;
    }

    private void navigateToProjectDetail(ProjectData project) {
        try {
            Stage stage = (Stage) cardsGrid.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("project-detail.fxml"));
            Scene scene = new Scene(loader.load(), 1400, 900);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            // Pass project data to detail controller
            ProjectDetailController controller = loader.getController();
            controller.setProjectData(project);

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Data class for project information
    public static class ProjectData {
        private final String name;
        private final String description;
        private final LocalDate startDate;
        private final LocalDate dueDate;
        private final String students;
        private final String duration;
        private final String qaCount;

        public ProjectData(String name, String description, LocalDate startDate, LocalDate dueDate,
                           String students, String duration, String qaCount) {
            this.name = name;
            this.description = description;
            this.startDate = startDate;
            this.dueDate = dueDate;
            this.students = students;
            this.duration = duration;
            this.qaCount = qaCount;
        }

        // Getters
        public String getName() { return name; }
        public String getDescription() { return description; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getDueDate() { return dueDate; }
        public String getStudents() { return students; }
        public String getDuration() { return duration; }
        public String getQaCount() { return qaCount; }
    }
}