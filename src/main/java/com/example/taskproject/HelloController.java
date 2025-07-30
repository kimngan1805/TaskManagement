package com.example.taskproject;

import com.example.taskproject.model.ProjectData;
import com.example.taskproject.model.User;

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

    // Project categories for dropdown
    private final List<String> projectCategories = Arrays.asList(
            "Web Development",
            "Mobile App",
            "Data Analysis",
            "Machine Learning",
            "UI/UX Design",
            "Game Development",
            "Research",
            "Marketing Campaign"
    );

    // Priority levels
    private final List<String> priorityLevels = Arrays.asList(
            "Low",
            "Medium",
            "High",
            "Critical"
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

        // Create main container with modern styling
        VBox mainContainer = new VBox();
        mainContainer.setSpacing(25);
        mainContainer.setPadding(new Insets(35));
        mainContainer.setStyle("-fx-background-color: white; -fx-background-radius: 25; " +
                "-fx-border-color: rgba(76, 107, 182, 0.2); -fx-border-width: 1; -fx-border-radius: 25; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 30, 0, 0, 10);");
        mainContainer.setPrefWidth(550);

        // Modern header with gradient icon
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);

        // Modern icon container
        VBox iconContainer = new VBox();
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setPrefSize(70, 70);
        iconContainer.setStyle("-fx-background-color: linear-gradient(135deg, rgba(76, 107, 182, 0.15), rgba(99, 102, 241, 0.15)); " +
                "-fx-background-radius: 35; -fx-border-color: rgba(76, 107, 182, 0.3); -fx-border-width: 2; -fx-border-radius: 35;");

        Label iconLabel = new Label("🚀");
        iconLabel.setStyle("-fx-font-size: 32px;");
        iconContainer.getChildren().add(iconLabel);

        // Modern title
        Label titleLabel = new Label("Create New Project");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

        // Modern close button
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeButton = new Button("✕");
        closeButton.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-background-radius: 25; " +
                "-fx-border-color: rgba(226, 232, 240, 0.6); -fx-border-width: 1; -fx-border-radius: 25; " +
                "-fx-text-fill: #64748b; -fx-font-size: 18px; -fx-min-width: 45; -fx-min-height: 45; -fx-cursor: hand;");
        closeButton.setOnAction(e -> dialogStage.close());

        header.getChildren().addAll(iconContainer, titleLabel, spacer, closeButton);

        // Form fields container
        VBox formContainer = new VBox();
        formContainer.setSpacing(25);

        // Project Name
        VBox nameGroup = createModernFieldGroup("Project Name", createModernTextField("Enter project name"));
        TextField projectNameField = (TextField) ((VBox) nameGroup.getChildren().get(1)).getChildren().get(0);

        // Description
        VBox descGroup = createModernFieldGroup("Description", createModernTextArea("Enter project description"));
        TextArea descriptionField = (TextArea) ((VBox) descGroup.getChildren().get(1)).getChildren().get(0);

        // Date fields in a row
        HBox datesRow = new HBox();
        datesRow.setSpacing(20);

        VBox startDateGroup = createModernFieldGroup("Start Date", createModernDatePicker());
        DatePicker startDatePicker = (DatePicker) ((VBox) startDateGroup.getChildren().get(1)).getChildren().get(0);
        startDatePicker.setValue(LocalDate.now());

        VBox dueDateGroup = createModernFieldGroup("Due Date", createModernDatePicker());
        DatePicker dueDatePicker = (DatePicker) ((VBox) dueDateGroup.getChildren().get(1)).getChildren().get(0);
        dueDatePicker.setValue(LocalDate.now().plusDays(14));

        datesRow.getChildren().addAll(startDateGroup, dueDateGroup);

        // Project details in a row
        HBox projectDetailsRow = new HBox();
        projectDetailsRow.setSpacing(20);

        // Project Category dropdown
        VBox categoryGroup = createModernFieldGroup("Category", createCategoryComboBox());
        ComboBox<String> categoryField = (ComboBox<String>) ((VBox) categoryGroup.getChildren().get(1)).getChildren().get(0);

        // Member Search
        VBox memberGroup = createMemberSearchGroup();

        projectDetailsRow.getChildren().addAll(categoryGroup, memberGroup);

        // Priority and Budget row
        HBox priorityBudgetRow = new HBox();
        priorityBudgetRow.setSpacing(20);

        // Priority level
        VBox priorityGroup = createModernFieldGroup("Priority", createPriorityComboBox());
        ComboBox<String> priorityField = (ComboBox<String>) ((VBox) priorityGroup.getChildren().get(1)).getChildren().get(0);

        // Estimated Budget
        VBox budgetGroup = createModernFieldGroup("Budget (USD)", createModernTextField("Enter estimated budget"));
        TextField budgetField = (TextField) ((VBox) budgetGroup.getChildren().get(1)).getChildren().get(0);
        budgetField.setPromptText("e.g., 5000");

        priorityBudgetRow.getChildren().addAll(priorityGroup, budgetGroup);

        formContainer.getChildren().addAll(nameGroup, descGroup, datesRow, projectDetailsRow, priorityBudgetRow);

        // Modern buttons
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(20);
        buttonContainer.setAlignment(Pos.CENTER);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-border-color: rgba(203, 213, 225, 0.8); " +
                "-fx-border-radius: 15; -fx-text-fill: #64748b; -fx-font-size: 16px; -fx-font-weight: 500; " +
                "-fx-padding: 16 32 16 32; -fx-cursor: hand;");
        cancelButton.setOnAction(e -> dialogStage.close());

        Button saveButton = new Button("Create Project");
        saveButton.setStyle("-fx-background-color: linear-gradient(135deg, #4C6BB6, #6366f1); -fx-background-radius: 15; " +
                "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: 600; " +
                "-fx-padding: 16 32 16 32; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(76, 107, 182, 0.4), 15, 0, 0, 5);");

        // Enable/disable save button
        saveButton.setDisable(true);
        projectNameField.textProperty().addListener((obs, old, newVal) -> {
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

        saveButton.setOnAction(e -> {
            // Get selected members from member search component
            ObservableList<User> selectedMembers = getSelectedMembersFromGroup(memberGroup);

            ProjectData project = new ProjectData(
                    projectNameField.getText().trim(),
                    descriptionField.getText().trim(),
                    startDatePicker.getValue(),
                    dueDatePicker.getValue(),
                    categoryField.getValue() != null ? categoryField.getValue() : "General", // Category instead of students
                    selectedMembers,
                    priorityField.getValue() + " | $" + (budgetField.getText().trim().isEmpty() ? "0" : budgetField.getText().trim()) // Priority and budget instead of Q&A
            );
            addNewCard(project);
            dialogStage.close();
        });

        buttonContainer.getChildren().addAll(cancelButton, saveButton);

        mainContainer.getChildren().addAll(header, formContainer, buttonContainer);

        // Create scene with modern backdrop
        VBox sceneContainer = new VBox();
        sceneContainer.setAlignment(Pos.CENTER);
        sceneContainer.setStyle("-fx-background-color: rgba(30, 41, 59, 0.4);");
        sceneContainer.getChildren().add(mainContainer);

        Scene scene = new Scene(sceneContainer, 650, 850);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private ComboBox<String> createCategoryComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList(projectCategories));
        comboBox.setPromptText("Select project category");
        comboBox.setValue("Web Development"); // Default selection
        comboBox.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-border-color: rgba(226, 232, 240, 0.8); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; " +
                "-fx-padding: 16 20 16 20; -fx-font-size: 15px; -fx-font-weight: 500;");
        return comboBox;
    }

    private ComboBox<String> createPriorityComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList(priorityLevels));
        comboBox.setPromptText("Select priority");
        comboBox.setValue("Medium"); // Default selection
        comboBox.setStyle("-fx-background-color: rgba(248, 250, 252, 0.8); -fx-border-color: rgba(226, 232, 240, 0.8); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; " +
                "-fx-padding: 16 20 16 20; -fx-font-size: 15px; -fx-font-weight: 500;");
        return comboBox;
    }

    private VBox createMemberSearchGroup() {
        VBox group = new VBox();
        group.setSpacing(12);

        Label label = new Label("Team Members");
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");

        VBox memberContainer = new VBox();
        memberContainer.setSpacing(12);

        // Search TextField with modern styling
        TextField searchField = createModernTextField("Search team members...");
        searchField.setStyle(searchField.getStyle() + "; -fx-prompt-text-fill: rgba(100, 116, 139, 0.7);");

        // Suggestions ListView (initially hidden)
        ListView<User> suggestionsListView = new ListView<>();
        suggestionsListView.setPrefHeight(120);
        suggestionsListView.setVisible(false);
        suggestionsListView.setManaged(false);
        suggestionsListView.setStyle("-fx-background-color: white; -fx-border-color: rgba(226, 232, 240, 0.8); " +
                "-fx-border-radius: 0 0 15 15; -fx-background-radius: 0 0 15 15;");

        // Selected members container
        FlowPane selectedMembersPane = new FlowPane();
        selectedMembersPane.setHgap(10);
        selectedMembersPane.setVgap(10);

        // Custom cell factory for suggestions with modern styling
        suggestionsListView.setCellFactory(listView -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox container = new HBox();
                    container.setSpacing(12);
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.setPadding(new Insets(10));

                    // Modern avatar
                    VBox avatar = new VBox();
                    avatar.setAlignment(Pos.CENTER);
                    avatar.setPrefSize(35, 35);
                    avatar.setStyle("-fx-background-color: linear-gradient(135deg, #4C6BB6, #6366f1); " +
                            "-fx-background-radius: 17.5; -fx-effect: dropshadow(gaussian, rgba(76, 107, 182, 0.3), 5, 0, 0, 2);");

                    Label avatarLabel = new Label(user.getName().substring(0, 1).toUpperCase());
                    avatarLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
                    avatar.getChildren().add(avatarLabel);

                    // User info
                    VBox userInfo = new VBox();
                    userInfo.setSpacing(2);

                    Label nameLabel = new Label(user.getName());
                    nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");

                    Label emailLabel = new Label(user.getEmail());
                    emailLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

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
        memberTag.setSpacing(10);
        memberTag.setAlignment(Pos.CENTER);
        memberTag.setPadding(new Insets(8, 15, 8, 15));
        memberTag.setStyle("-fx-background-color: rgba(76, 107, 182, 0.1); -fx-background-radius: 25; " +
                "-fx-border-color: rgba(76, 107, 182, 0.3); -fx-border-radius: 25; -fx-border-width: 1;");
        memberTag.setUserData(user); // Store user data for later retrieval

        Label nameLabel = new Label(user.getName());
        nameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #4C6BB6; -fx-font-weight: 600;");

        Button removeButton = new Button("×");
        removeButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                "-fx-text-fill: #64748b; -fx-font-size: 16px; -fx-padding: 0; " +
                "-fx-min-width: 18; -fx-min-height: 18; -fx-cursor: hand;");
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
        area.setPrefRowCount(3);
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

        // Info section with updated information
        VBox infoBox = new VBox();
        infoBox.setSpacing(8.0);

        // Date and category info
        HBox dateCategoryBox = new HBox();
        dateCategoryBox.setAlignment(Pos.CENTER_LEFT);
        dateCategoryBox.setSpacing(5.0);

        Label dateIcon = new Label("📅");
        Label dateLabel = new Label(project.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dateLabel.getStyleClass().add("card-info");

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Label categoryIcon = new Label("📂");
        Label categoryDisplayLabel = new Label("Type: " + project.getStudents()); // Using students field for category
        categoryDisplayLabel.getStyleClass().add("card-info");

        dateCategoryBox.getChildren().addAll(dateIcon, dateLabel, spacer1, categoryIcon, categoryDisplayLabel);

        // Members and Priority/Budget info
        HBox membersPriorityBox = new HBox();
        membersPriorityBox.setAlignment(Pos.CENTER_LEFT);
        membersPriorityBox.setSpacing(5.0);

        Label membersIcon = new Label("👨‍👩‍👧‍👦");
        Label membersLabel = new Label("Team: " + project.getMembers().size());
        membersLabel.getStyleClass().add("card-info");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Label priorityIcon = new Label("⚡");
        Label priorityLabel = new Label(project.getQaCount()); // Using qaCount field for priority/budget
        priorityLabel.getStyleClass().add("card-info");

        membersPriorityBox.getChildren().addAll(membersIcon, membersLabel, spacer2, priorityIcon, priorityLabel);

        infoBox.getChildren().addAll(dateCategoryBox, membersPriorityBox);

        card.getChildren().addAll(menuBox, titleBox, infoBox);

        return card;
    }

    private void navigateToProjectDetail(ProjectData project) {
        try {
            Stage stage = (Stage) cardsGrid.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("project-detail.fxml"));
            Scene scene = new Scene(loader.load(), 1400, 900);
            scene.getStylesheets().add(getClass().getResource("/css/project-detail.css").toExternalForm());

            // Pass project data to detail controller
            ProjectDetailController controller = loader.getController();
            controller.setProjectData(project);

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}