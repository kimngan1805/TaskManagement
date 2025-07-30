package com.example.taskproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HelloController {

    @FXML
    private GridPane cardsGrid;

    @FXML
    private Label cardCountNumber;

    private int cardCount = 4; // Current card count
    private final String[] cardColors = {"card-purple", "card-orange", "card-yellow", "card-blue"};

    // Mock data cho users
    private final List<User> mockUsers = Arrays.asList(
            new User(1, "John Doe", "john.doe@example.com"),
            new User(2, "Jane Smith", "jane.smith@example.com"),
            new User(3, "Mike Johnson", "mike.johnson@example.com"),
            new User(4, "Sarah Wilson", "sarah.wilson@example.com"),
            new User(5, "David Brown", "david.brown@example.com"),
            new User(6, "Lisa Davis", "lisa.davis@example.com"),
            new User(7, "Alex Chen", "alex.chen@example.com"),
            new User(8, "Emma Taylor", "emma.taylor@example.com")
    );

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
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
        mainContainer.setPrefWidth(450);

        // Header with icon and close button
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(15);

        // Icon circle
        VBox iconContainer = new VBox();
        iconContainer.setAlignment(Pos.CENTER);
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
        HBox.setHgrow(spacer, Priority.ALWAYS);

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

        // Thay thế Duration bằng Member Search
        VBox memberGroup = createMemberSearchGroup();

        otherFieldsRow.getChildren().addAll(studentsGroup, memberGroup);

        // Q&A field
        VBox qaGroup = createFieldGroup("Q&A Count", createStyledTextField("20"));
        TextField qaField = (TextField) ((VBox) qaGroup.getChildren().get(1)).getChildren().get(0);

        formContainer.getChildren().addAll(nameGroup, descGroup, datesRow, otherFieldsRow, qaGroup);

        // Buttons
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(15);
        buttonContainer.setAlignment(Pos.CENTER);

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
            // Get selected members from member search component
            ObservableList<User> selectedMembers = getSelectedMembersFromGroup(memberGroup);

            ProjectData project = new ProjectData(
                    projectNameField.getText().trim(),
                    descriptionField.getText().trim(),
                    startDatePicker.getValue(),
                    dueDatePicker.getValue(),
                    studentsField.getText().trim(),
                    selectedMembers, // Pass selected members instead of duration
                    qaField.getText().trim()
            );
            addNewCard(project);
            dialogStage.close();
        });

        buttonContainer.getChildren().addAll(cancelButton, saveButton);

        mainContainer.getChildren().addAll(header, formContainer, buttonContainer);

        // Create scene with padding
        VBox sceneContainer = new VBox();
        sceneContainer.setAlignment(Pos.CENTER);
        sceneContainer.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
        sceneContainer.getChildren().add(mainContainer);

        Scene scene = new Scene(sceneContainer, 550, 750);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private VBox createMemberSearchGroup() {
        VBox group = new VBox();
        group.setSpacing(8);

        Label label = new Label("Add Members");
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #374151;");

        VBox memberContainer = new VBox();
        memberContainer.setSpacing(10);

        // Search TextField
        TextField searchField = createStyledTextField("Enter email to search...");
        searchField.setStyle(searchField.getStyle() + "; -fx-prompt-text-fill: #9ca3af;");

        // Suggestions ListView (initially hidden)
        ListView<User> suggestionsListView = new ListView<>();
        suggestionsListView.setPrefHeight(120);
        suggestionsListView.setVisible(false);
        suggestionsListView.setManaged(false);
        suggestionsListView.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; " +
                "-fx-border-radius: 0 0 10 10; -fx-background-radius: 0 0 10 10;");

        // Selected members container
        FlowPane selectedMembersPane = new FlowPane();
        selectedMembersPane.setHgap(8);
        selectedMembersPane.setVgap(8);

        // Custom cell factory for suggestions
        suggestionsListView.setCellFactory(listView -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox container = new HBox();
                    container.setSpacing(10);
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.setPadding(new Insets(8));

                    // Avatar
                    Label avatar = new Label("👤");
                    avatar.setStyle("-fx-font-size: 16px; -fx-background-color: #e8f2ff; " +
                            "-fx-background-radius: 15; -fx-padding: 5; -fx-min-width: 30; -fx-alignment: center;");

                    // User info
                    VBox userInfo = new VBox();
                    userInfo.setSpacing(2);

                    Label nameLabel = new Label(user.getName());
                    nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #1f2937;");

                    Label emailLabel = new Label(user.getEmail());
                    emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

                    userInfo.getChildren().addAll(nameLabel, emailLabel);
                    container.getChildren().addAll(avatar, userInfo);

                    setGraphic(container);
                    setText(null);
                }
            }
        });

        // Search functionality
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                suggestionsListView.setVisible(false);
                suggestionsListView.setManaged(false);
                return;
            }

            // Filter users based on email or name
            List<User> filteredUsers = mockUsers.stream()
                    .filter(user -> user.getEmail().toLowerCase().contains(newVal.toLowerCase()) ||
                            user.getName().toLowerCase().contains(newVal.toLowerCase()))
                    .filter(user -> !isUserSelected(user, selectedMembersPane))
                    .toList();

            if (!filteredUsers.isEmpty()) {
                suggestionsListView.setItems(FXCollections.observableArrayList(filteredUsers));
                suggestionsListView.setVisible(true);
                suggestionsListView.setManaged(true);
            } else {
                suggestionsListView.setVisible(false);
                suggestionsListView.setManaged(false);
            }
        });

        // Handle selection from suggestions
        suggestionsListView.setOnMouseClicked(event -> {
            User selectedUser = suggestionsListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                addSelectedMember(selectedUser, selectedMembersPane);
                searchField.clear();
                suggestionsListView.setVisible(false);
                suggestionsListView.setManaged(false);
            }
        });

        memberContainer.getChildren().addAll(searchField, suggestionsListView, selectedMembersPane);
        group.getChildren().addAll(label, memberContainer);

        // Store reference to selectedMembersPane for later retrieval
        group.setUserData(selectedMembersPane);

        return group;
    }

    private boolean isUserSelected(User user, FlowPane selectedMembersPane) {
        return selectedMembersPane.getChildren().stream()
                .anyMatch(node -> {
                    if (node.getUserData() instanceof User) {
                        return ((User) node.getUserData()).getId() == user.getId();
                    }
                    return false;
                });
    }

    private void addSelectedMember(User user, FlowPane selectedMembersPane) {
        HBox memberTag = new HBox();
        memberTag.setSpacing(8);
        memberTag.setAlignment(Pos.CENTER);
        memberTag.setPadding(new Insets(6, 10, 6, 10));
        memberTag.setStyle("-fx-background-color: #e8f2ff; -fx-background-radius: 20; -fx-border-color: #3b82f6; -fx-border-radius: 20;");
        memberTag.setUserData(user); // Store user data for later retrieval

        Label nameLabel = new Label(user.getName());
        nameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1e40af; -fx-font-weight: 600;");

        Button removeButton = new Button("×");
        removeButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                "-fx-text-fill: #6b7280; -fx-font-size: 14px; -fx-padding: 0; " +
                "-fx-min-width: 16; -fx-min-height: 16; -fx-cursor: hand;");
        removeButton.setOnAction(e -> selectedMembersPane.getChildren().remove(memberTag));

        memberTag.getChildren().addAll(nameLabel, removeButton);
        selectedMembersPane.getChildren().add(memberTag);
    }

    @SuppressWarnings("unchecked")
    private ObservableList<User> getSelectedMembersFromGroup(VBox memberGroup) {
        FlowPane selectedMembersPane = (FlowPane) memberGroup.getUserData();
        ObservableList<User> selectedUsers = FXCollections.observableArrayList();

        selectedMembersPane.getChildren().forEach(node -> {
            if (node.getUserData() instanceof User) {
                selectedUsers.add((User) node.getUserData());
            }
        });

        return selectedUsers;
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
            cardsGrid.getRowConstraints().add(new RowConstraints());
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
        menuBox.setAlignment(Pos.CENTER_RIGHT);
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
        dateStudentsBox.setAlignment(Pos.CENTER_LEFT);
        dateStudentsBox.setSpacing(5.0);

        Label dateIcon = new Label("📅");
        Label dateLabel = new Label(project.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dateLabel.getStyleClass().add("card-info");

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Label studentsIcon = new Label("👥");
        Label studentsLabel = new Label("Students: " + project.getStudents());
        studentsLabel.getStyleClass().add("card-info");

        dateStudentsBox.getChildren().addAll(dateIcon, dateLabel, spacer1, studentsIcon, studentsLabel);

        // Members and Q&A info
        HBox membersQABox = new HBox();
        membersQABox.setAlignment(Pos.CENTER_LEFT);
        membersQABox.setSpacing(5.0);

        Label membersIcon = new Label("👨‍👩‍👧‍👦");
        Label membersLabel = new Label("Members: " + project.getMembers().size());
        membersLabel.getStyleClass().add("card-info");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Label qaIcon = new Label("❓");
        Label qaLabel = new Label("Q&A: " + project.getQaCount());
        qaLabel.getStyleClass().add("card-info");

        membersQABox.getChildren().addAll(membersIcon, membersLabel, spacer2, qaIcon, qaLabel);

        infoBox.getChildren().addAll(dateStudentsBox, membersQABox);

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

    // User class for member management
    public static class User {
        private final int id;
        private final String name;
        private final String email;

        public User(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }

        @Override
        public String toString() {
            return name + " (" + email + ")";
        }
    }

    // Updated ProjectData class with members
    public static class ProjectData {
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
        public String getName() { return name; }
        public String getDescription() { return description; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getDueDate() { return dueDate; }
        public String getStudents() { return students; }
        public ObservableList<User> getMembers() { return members; }
        public String getQaCount() { return qaCount; }
    }
}