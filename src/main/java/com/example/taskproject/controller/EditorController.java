package com.example.taskproject.controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EditorController {

    // Header elements
    @FXML private TextField documentTitle;
    @FXML private Label documentDescription;

    // Editor elements
    @FXML private ScrollPane editorScrollPane;
    @FXML private VBox editorContent;
    @FXML private HBox initialBlock;
    @FXML private Button addBlockBtn;
    @FXML private VBox blockTypeMenu;

    // Document state
    private List<EditorBlock> documentBlocks = new ArrayList<>();
    private EditorBlock currentEditingBlock;
    private boolean isMenuVisible = false;

    @FXML
    private void initialize() {
        setupEditor();
        setupDocumentTitle();
        setupKeyboardShortcuts();
    }

    private void setupEditor() {
        // Hide menu when clicking outside
        editorScrollPane.setOnMouseClicked(e -> hideBlockMenu());

        // Setup initial focus
        addBlockBtn.requestFocus();
    }

    private void setupDocumentTitle() {
        documentTitle.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                documentTitle.setText("Untitled Document");
            }
        });

        // Add placeholder styling
        documentTitle.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && documentTitle.getText().trim().isEmpty()) {
                documentTitle.setText("Untitled Document");
            }
        });
    }

    private void setupKeyboardShortcuts() {
        editorContent.setOnKeyPressed(e -> {
            if (e.isControlDown()) {
                switch (e.getCode()) {
                    case S:
                        saveDocument();
                        e.consume();
                        break;
                    case N:
                        addTextBlock();
                        e.consume();
                        break;
                    case SLASH:
                        showBlockMenu();
                        e.consume();
                        break;
                }
            }
        });
    }

    @FXML
    private void showBlockMenu() {
        isMenuVisible = !isMenuVisible;
        blockTypeMenu.setVisible(isMenuVisible);
        blockTypeMenu.setManaged(isMenuVisible);

        if (isMenuVisible) {
            // Position menu near the add button
            blockTypeMenu.getStyleClass().add("menu-visible");
        } else {
            blockTypeMenu.getStyleClass().remove("menu-visible");
        }
    }

    private void hideBlockMenu() {
        if (isMenuVisible) {
            isMenuVisible = false;
            blockTypeMenu.setVisible(false);
            blockTypeMenu.setManaged(false);
            blockTypeMenu.getStyleClass().remove("menu-visible");
        }
    }

    // Basic Block Types
    @FXML
    private void addTextBlock() {
        EditorBlock block = createTextBlock("", "text");
        addBlockToEditor(block);
        hideBlockMenu();
    }

    @FXML
    private void addHeading1() {
        EditorBlock block = createTextBlock("", "heading1");
        addBlockToEditor(block);
        hideBlockMenu();
    }

    @FXML
    private void addHeading2() {
        EditorBlock block = createTextBlock("", "heading2");
        addBlockToEditor(block);
        hideBlockMenu();
    }

    @FXML
    private void addHeading3() {
        EditorBlock block = createTextBlock("", "heading3");
        addBlockToEditor(block);
        hideBlockMenu();
    }

    @FXML
    private void addBulletList() {
        EditorBlock block = createListBlock("bullet");
        addBlockToEditor(block);
        hideBlockMenu();
    }

    @FXML
    private void addNumberedList() {
        EditorBlock block = createListBlock("numbered");
        addBlockToEditor(block);
        hideBlockMenu();
    }

    @FXML
    private void addTodoList() {
        EditorBlock block = createTodoBlock();
        addBlockToEditor(block);
        hideBlockMenu();
    }

    @FXML
    private void addToggleList() {
        EditorBlock block = createToggleBlock();
        addBlockToEditor(block);
        hideBlockMenu();
    }

    // Advanced Blocks
    @FXML
    private void addPageBlock() {
        EditorBlock block = createPageBlock();
        addBlockToEditor(block);
        hideBlockMenu();
    }

    @FXML
    private void addCalloutBlock() {
        EditorBlock block = createCalloutBlock();
        addBlockToEditor(block);
        hideBlockMenu();
    }
    private EditorBlock createQuoteBlock() {
        VBox blockContainer = new VBox();
        blockContainer.setSpacing(5);
        blockContainer.getStyleClass().addAll("editor-block", "block-quote");

        HBox blockHeader = createBlockHeader(blockContainer);

        HBox quoteContainer = new HBox();
        quoteContainer.setSpacing(10);
        quoteContainer.getStyleClass().add("quote-container");

        VBox quoteLine = new VBox();
        quoteLine.getStyleClass().add("quote-line");
        quoteLine.setPrefWidth(4);

        TextArea textArea = new TextArea();
        textArea.setPromptText("Enter a quote...");
        textArea.getStyleClass().add("quote-text");
        textArea.setPrefRowCount(2);
        textArea.setWrapText(true);
        HBox.setHgrow(textArea, Priority.ALWAYS);

        quoteContainer.getChildren().addAll(quoteLine, textArea);
        blockContainer.getChildren().addAll(blockHeader, quoteContainer);

        return new EditorBlock("quote", blockContainer, textArea);
    }
    @FXML
    private void addQuoteBlock() {
        EditorBlock block = createQuoteBlock();
        addBlockToEditor(block);
        hideBlockMenu();
    }

    @FXML
    private void addLinkBlock() {
        EditorBlock block = createLinkBlock();
        addBlockToEditor(block);
        hideBlockMenu();
    }

    // Media Blocks
    @FXML
    private void addImageBlock() {
        EditorBlock block = createImageBlock();
        addBlockToEditor(block);
        hideBlockMenu();
    }

    @FXML
    private void addVideoBlock() {
        EditorBlock block = createVideoBlock();
        addBlockToEditor(block);
        hideBlockMenu();
    }

    // Database Blocks
    @FXML
    private void addTableBlock() {
        EditorBlock block = createTableBlock();
        addBlockToEditor(block);
        hideBlockMenu();
    }

    @FXML
    private void addBoardBlock() {
        EditorBlock block = createBoardBlock();
        addBlockToEditor(block);
        hideBlockMenu();
    }

    @FXML
    private void addCalendarBlock() {
        EditorBlock block = createCalendarBlock();
        addBlockToEditor(block);
        hideBlockMenu();
    }

    // Block Creation Methods
    private EditorBlock createTextBlock(String content, String type) {
        VBox blockContainer = new VBox();
        blockContainer.setSpacing(5);
        blockContainer.getStyleClass().addAll("editor-block", "block-" + type);

        HBox blockHeader = new HBox();
        blockHeader.setSpacing(8);
        blockHeader.setAlignment(Pos.CENTER_LEFT);

        Button addBtn = new Button("+");
        addBtn.getStyleClass().add("block-add-btn");
        addBtn.setOnAction(e -> showBlockMenu());

        Button dragHandle = new Button("⋮⋮");
        dragHandle.getStyleClass().add("block-drag-handle");

        Button deleteBtn = new Button("🗑️");
        deleteBtn.getStyleClass().add("block-delete-btn");
        deleteBtn.setOnAction(e -> removeBlock(blockContainer));

        TextArea textArea = new TextArea(content);
        textArea.getStyleClass().addAll("block-text-area", type + "-text");
        textArea.setWrapText(true);
        textArea.setPrefRowCount(type.equals("text") ? 3 : 1);

        // Set placeholder based on type
        switch (type) {
            case "heading1":
                textArea.setPromptText("Heading 1");
                break;
            case "heading2":
                textArea.setPromptText("Heading 2");
                break;
            case "heading3":
                textArea.setPromptText("Heading 3");
                break;
            default:
                textArea.setPromptText("Type something...");
        }

        blockHeader.getChildren().addAll(addBtn, dragHandle, deleteBtn);
        blockContainer.getChildren().addAll(blockHeader, textArea);

        return new EditorBlock(type, blockContainer, textArea);
    }

    private EditorBlock createListBlock(String listType) {
        VBox blockContainer = new VBox();
        blockContainer.setSpacing(5);
        blockContainer.getStyleClass().addAll("editor-block", "block-list");

        HBox blockHeader = createBlockHeader(blockContainer);

        VBox listContainer = new VBox();
        listContainer.setSpacing(3);
        listContainer.getStyleClass().add("list-container");

        // Add first list item
        HBox listItem = createListItem(listType, "", listContainer);
        listContainer.getChildren().add(listItem);

        blockContainer.getChildren().addAll(blockHeader, listContainer);

        return new EditorBlock("list-" + listType, blockContainer, listContainer);
    }

    private HBox createListItem(String listType, String content, VBox listContainer) {
        HBox listItem = new HBox();
        listItem.setSpacing(8);
        listItem.setAlignment(Pos.CENTER_LEFT);
        listItem.getStyleClass().add("list-item");

        Label bullet;
        if (listType.equals("bullet")) {
            bullet = new Label("•");
            bullet.getStyleClass().add("bullet-point");
        } else {
            int itemNumber = listContainer.getChildren().size() + 1;
            bullet = new Label(itemNumber + ".");
            bullet.getStyleClass().add("number-point");
        }

        TextField textField = new TextField(content);
        textField.setPromptText("List item");
        textField.getStyleClass().add("list-item-text");
        HBox.setHgrow(textField, Priority.ALWAYS);

        // Add new item on Enter
        textField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                HBox newItem = createListItem(listType, "", listContainer);
                int currentIndex = listContainer.getChildren().indexOf(listItem);
                listContainer.getChildren().add(currentIndex + 1, newItem);
                ((TextField) ((HBox) listContainer.getChildren().get(currentIndex + 1))
                        .getChildren().get(1)).requestFocus();

                // Update numbers for numbered list
                if (listType.equals("numbered")) {
                    updateNumberedList(listContainer);
                }
            }
        });

        Button removeBtn = new Button("×");
        removeBtn.getStyleClass().add("remove-item-btn");
        removeBtn.setOnAction(e -> {
            listContainer.getChildren().remove(listItem);
            if (listType.equals("numbered")) {
                updateNumberedList(listContainer);
            }
        });

        listItem.getChildren().addAll(bullet, textField, removeBtn);
        return listItem;
    }

    private void updateNumberedList(VBox listContainer) {
        for (int i = 0; i < listContainer.getChildren().size(); i++) {
            HBox item = (HBox) listContainer.getChildren().get(i);
            Label numberLabel = (Label) item.getChildren().get(0);
            numberLabel.setText((i + 1) + ".");
        }
    }

    private EditorBlock createTodoBlock() {
        VBox blockContainer = new VBox();
        blockContainer.setSpacing(5);
        blockContainer.getStyleClass().addAll("editor-block", "block-todo");

        HBox blockHeader = createBlockHeader(blockContainer);

        VBox todoContainer = new VBox();
        todoContainer.setSpacing(5);
        todoContainer.getStyleClass().add("todo-container");

        HBox todoItem = createTodoItem("", todoContainer);
        todoContainer.getChildren().add(todoItem);

        blockContainer.getChildren().addAll(blockHeader, todoContainer);

        return new EditorBlock("todo", blockContainer, todoContainer);
    }

    private HBox createTodoItem(String content, VBox todoContainer) {
        HBox todoItem = new HBox();
        todoItem.setSpacing(8);
        todoItem.setAlignment(Pos.CENTER_LEFT);
        todoItem.getStyleClass().add("todo-item");

        CheckBox checkBox = new CheckBox();
        checkBox.getStyleClass().add("todo-checkbox");

        TextField textField = new TextField(content);
        textField.setPromptText("To-do item");
        textField.getStyleClass().add("todo-item-text");
        HBox.setHgrow(textField, Priority.ALWAYS);

        // Strike-through when completed
        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                textField.getStyleClass().add("completed-todo");
            } else {
                textField.getStyleClass().remove("completed-todo");
            }
        });

        // Add new item on Enter
        textField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                HBox newItem = createTodoItem("", todoContainer);
                int currentIndex = todoContainer.getChildren().indexOf(todoItem);
                todoContainer.getChildren().add(currentIndex + 1, newItem);
                ((TextField) ((HBox) todoContainer.getChildren().get(currentIndex + 1))
                        .getChildren().get(1)).requestFocus();
            }
        });

        Button removeBtn = new Button("×");
        removeBtn.getStyleClass().add("remove-item-btn");
        removeBtn.setOnAction(e -> todoContainer.getChildren().remove(todoItem));

        todoItem.getChildren().addAll(checkBox, textField, removeBtn);
        return todoItem;
    }

    private EditorBlock createToggleBlock() {
        VBox blockContainer = new VBox();
        blockContainer.setSpacing(5);
        blockContainer.getStyleClass().addAll("editor-block", "block-toggle");

        HBox blockHeader = createBlockHeader(blockContainer);

        HBox toggleHeader = new HBox();
        toggleHeader.setSpacing(8);
        toggleHeader.setAlignment(Pos.CENTER_LEFT);
        toggleHeader.getStyleClass().add("toggle-header");

        Button toggleBtn = new Button("▶");
        toggleBtn.getStyleClass().add("toggle-btn");

        TextField titleField = new TextField();
        titleField.setPromptText("Toggle title");
        titleField.getStyleClass().add("toggle-title");
        HBox.setHgrow(titleField, Priority.ALWAYS);

        VBox toggleContent = new VBox();
        toggleContent.setSpacing(5);
        toggleContent.getStyleClass().add("toggle-content");
        toggleContent.setVisible(false);
        toggleContent.setManaged(false);

        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Toggle content...");
        contentArea.getStyleClass().add("toggle-content-area");
        contentArea.setPrefRowCount(3);

        toggleContent.getChildren().add(contentArea);

        // Toggle functionality
        toggleBtn.setOnAction(e -> {
            boolean isExpanded = toggleContent.isVisible();
            toggleContent.setVisible(!isExpanded);
            toggleContent.setManaged(!isExpanded);
            toggleBtn.setText(isExpanded ? "▶" : "▼");
        });

        toggleHeader.getChildren().addAll(toggleBtn, titleField);
        blockContainer.getChildren().addAll(blockHeader, toggleHeader, toggleContent);

        return new EditorBlock("toggle", blockContainer, titleField);
    }

    private EditorBlock createCalloutBlock() {
        VBox blockContainer = new VBox();
        blockContainer.setSpacing(5);
        blockContainer.getStyleClass().addAll("editor-block", "block-callout");

        HBox blockHeader = createBlockHeader(blockContainer);

        HBox calloutContainer = new HBox();
        calloutContainer.setSpacing(10);
        calloutContainer.getStyleClass().add("callout-container");

        Label iconLabel = new Label("💡");
        iconLabel.getStyleClass().add("callout-icon");

        TextArea textArea = new TextArea();
        textArea.setPromptText("Write a callout...");
        textArea.getStyleClass().add("callout-text");
        textArea.setPrefRowCount(2);
        textArea.setWrapText(true);
        HBox.setHgrow(textArea, Priority.ALWAYS);

        calloutContainer.getChildren().addAll(iconLabel, textArea);
        blockContainer.getChildren().addAll(blockHeader, calloutContainer);

        return new EditorBlock("callout", blockContainer, textArea);
    }

    private EditorBlock createPageBlock() {
        VBox blockContainer = new VBox();
        blockContainer.setSpacing(5);
        blockContainer.getStyleClass().addAll("editor-block", "block-page");

        HBox blockHeader = createBlockHeader(blockContainer);

        HBox pageContainer = new HBox();
        pageContainer.setSpacing(10);
        pageContainer.setAlignment(Pos.CENTER_LEFT);
        pageContainer.getStyleClass().add("page-container");

        Label pageIcon = new Label("📄");
        pageIcon.getStyleClass().add("page-icon");

        TextField pageTitle = new TextField();
        pageTitle.setPromptText("Page title");
        pageTitle.getStyleClass().add("page-title");
        HBox.setHgrow(pageTitle, Priority.ALWAYS);

        Button openBtn = new Button("Open");
        openBtn.getStyleClass().add("page-open-btn");
        openBtn.setOnAction(e -> {
            // Open page functionality
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Page Link");
            alert.setHeaderText("Navigate to: " + pageTitle.getText());
            alert.setContentText("This would open the linked page in a real implementation.");
            alert.showAndWait();
        });

        pageContainer.getChildren().addAll(pageIcon, pageTitle, openBtn);
        blockContainer.getChildren().addAll(blockHeader, pageContainer);

        return new EditorBlock("page", blockContainer, pageTitle);
    }

    private EditorBlock createLinkBlock() {
        VBox blockContainer = new VBox();
        blockContainer.setSpacing(5);
        blockContainer.getStyleClass().addAll("editor-block", "block-link");

        HBox blockHeader = createBlockHeader(blockContainer);

        VBox linkContainer = new VBox();
        linkContainer.setSpacing(8);
        linkContainer.getStyleClass().add("link-container");

        TextField urlField = new TextField();
        urlField.setPromptText("Enter URL...");
        urlField.getStyleClass().add("link-url");

        TextField titleField = new TextField();
        titleField.setPromptText("Link title (optional)");
        titleField.getStyleClass().add("link-title");

        HBox linkPreview = new HBox();
        linkPreview.setSpacing(8);
        linkPreview.setAlignment(Pos.CENTER_LEFT);
        linkPreview.getStyleClass().add("link-preview");

        Label linkIcon = new Label("🔗");
        Label linkLabel = new Label("No link");
        linkLabel.getStyleClass().add("link-label");

        linkPreview.getChildren().addAll(linkIcon, linkLabel);

        urlField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                linkLabel.setText(newVal.length() > 50 ? newVal.substring(0, 50) + "..." : newVal);
            } else {
                linkLabel.setText("No link");
            }
        });

        linkContainer.getChildren().addAll(urlField, titleField, linkPreview);
        blockContainer.getChildren().addAll(blockHeader, linkContainer);

        return new EditorBlock("link", blockContainer, urlField);
    }

    private EditorBlock createImageBlock() {
        VBox blockContainer = new VBox();
        blockContainer.setSpacing(5);
        blockContainer.getStyleClass().addAll("editor-block", "block-image");

        HBox blockHeader = createBlockHeader(blockContainer);

        VBox imageContainer = new VBox();
        imageContainer.setSpacing(10);
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.getStyleClass().add("image-container");

        Button uploadBtn = new Button("🖼️ Upload Image");
        uploadBtn.getStyleClass().add("upload-btn");
        uploadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
            );
            File selectedFile = fileChooser.showOpenDialog(blockContainer.getScene().getWindow());
            if (selectedFile != null) {
                Label imageLabel = new Label("📷 " + selectedFile.getName());
                imageLabel.getStyleClass().add("image-placeholder");
                imageContainer.getChildren().clear();
                imageContainer.getChildren().addAll(imageLabel, createImageCaption());
            }
        });

        TextField urlField = new TextField();
        urlField.setPromptText("Or paste image URL...");
        urlField.getStyleClass().add("image-url");

        imageContainer.getChildren().addAll(uploadBtn, urlField);
        blockContainer.getChildren().addAll(blockHeader, imageContainer);

        return new EditorBlock("image", blockContainer, urlField);
    }

    private TextField createImageCaption() {
        TextField caption = new TextField();
        caption.setPromptText("Add a caption...");
        caption.getStyleClass().add("image-caption");
        return caption;
    }

    private EditorBlock createVideoBlock() {
        VBox blockContainer = new VBox();
        blockContainer.setSpacing(5);
        blockContainer.getStyleClass().addAll("editor-block", "block-video");

        HBox blockHeader = createBlockHeader(blockContainer);

        VBox videoContainer = new VBox();
        videoContainer.setSpacing(10);
        videoContainer.setAlignment(Pos.CENTER);
        videoContainer.getStyleClass().add("video-container");

        TextField urlField = new TextField();
        urlField.setPromptText("Paste YouTube, Vimeo, or video URL...");
        urlField.getStyleClass().add("video-url");

        Label videoPlaceholder = new Label("🎥 Video Preview");
        videoPlaceholder.getStyleClass().add("video-placeholder");

        Button loadBtn = new Button("Load Video");
        loadBtn.getStyleClass().add("load-video-btn");
        loadBtn.setOnAction(e -> {
            String url = urlField.getText().trim();
            if (!url.isEmpty()) {
                videoPlaceholder.setText("🎥 " + (url.length() > 30 ? url.substring(0, 30) + "..." : url));
            }
        });

        videoContainer.getChildren().addAll(urlField, loadBtn, videoPlaceholder);
        blockContainer.getChildren().addAll(blockHeader, videoContainer);

        return new EditorBlock("video", blockContainer, urlField);
    }

    private EditorBlock createTableBlock() {
        VBox blockContainer = new VBox();
        blockContainer.setSpacing(5);
        blockContainer.getStyleClass().addAll("editor-block", "block-table");

        HBox blockHeader = createBlockHeader(blockContainer);

        VBox tableContainer = new VBox();
        tableContainer.setSpacing(5);
        tableContainer.getStyleClass().add("table-container");

        // Table controls
        HBox tableControls = new HBox();
        tableControls.setSpacing(8);
        tableControls.setAlignment(Pos.CENTER_LEFT);
        tableControls.getStyleClass().add("table-controls");

        Button addRowBtn = new Button("+ Row");
        Button addColBtn = new Button("+ Column");
        addRowBtn.getStyleClass().add("table-control-btn");
        addColBtn.getStyleClass().add("table-control-btn");

        tableControls.getChildren().addAll(addRowBtn, addColBtn);

        // Simple table representation
        VBox simpleTable = new VBox();
        simpleTable.setSpacing(2);
        simpleTable.getStyleClass().add("simple-table");

        // Header row
        HBox headerRow = createTableRow(true);
        simpleTable.getChildren().add(headerRow);

        // Data rows
        for (int i = 0; i < 2; i++) {
            HBox dataRow = createTableRow(false);
            simpleTable.getChildren().add(dataRow);
        }

        addRowBtn.setOnAction(e -> {
            HBox newRow = createTableRow(false);
            simpleTable.getChildren().add(newRow);
        });

        tableContainer.getChildren().addAll(tableControls, simpleTable);
        blockContainer.getChildren().addAll(blockHeader, tableContainer);

        return new EditorBlock("table", blockContainer, simpleTable);
    }

    private HBox createTableRow(boolean isHeader) {
        HBox row = new HBox();
        row.setSpacing(1);
        row.getStyleClass().add(isHeader ? "table-header-row" : "table-data-row");

        for (int i = 0; i < 3; i++) {
            TextField cell = new TextField();
            cell.getStyleClass().add(isHeader ? "table-header-cell" : "table-data-cell");
            cell.setPromptText(isHeader ? "Header " + (i + 1) : "Cell");
            HBox.setHgrow(cell, Priority.ALWAYS);
            row.getChildren().add(cell);
        }

        return row;
    }

    private EditorBlock createBoardBlock() {
        VBox blockContainer = new VBox();
        blockContainer.setSpacing(5);
        blockContainer.getStyleClass().addAll("editor-block", "block-board");

        HBox blockHeader = createBlockHeader(blockContainer);

        VBox boardContainer = new VBox();
        boardContainer.setSpacing(10);
        boardContainer.getStyleClass().add("board-container");

        Label boardTitle = new Label("📋 Kanban Board");
        boardTitle.getStyleClass().add("board-title");

        HBox boardColumns = new HBox();
        boardColumns.setSpacing(10);
        boardColumns.getStyleClass().add("board-columns");

        // Create sample columns
        String[] columnNames = {"To Do", "In Progress", "Done"};
        for (String columnName : columnNames) {
            VBox column = new VBox();
            column.setSpacing(5);
            column.getStyleClass().add("board-column");

            Label columnHeader = new Label(columnName);
            columnHeader.getStyleClass().add("board-column-header");

            VBox columnContent = new VBox();
            columnContent.setSpacing(3);
            columnContent.getStyleClass().add("board-column-content");

            // Add sample card
            Label sampleCard = new Label("Sample card");
            sampleCard.getStyleClass().add("board-card");
            columnContent.getChildren().add(sampleCard);

            Button addCardBtn = new Button("+ Add card");
            addCardBtn.getStyleClass().add("add-card-btn");

            column.getChildren().addAll(columnHeader, columnContent, addCardBtn);
            HBox.setHgrow(column, Priority.ALWAYS);
            boardColumns.getChildren().add(column);
        }

        boardContainer.getChildren().addAll(boardTitle, boardColumns);
        blockContainer.getChildren().addAll(blockHeader, boardContainer);

        return new EditorBlock("board", blockContainer, boardContainer);
    }

    private EditorBlock createCalendarBlock() {
        VBox blockContainer = new VBox();
        blockContainer.setSpacing(5);
        blockContainer.getStyleClass().addAll("editor-block", "block-calendar");

        HBox blockHeader = createBlockHeader(blockContainer);

        VBox calendarContainer = new VBox();
        calendarContainer.setSpacing(10);
        calendarContainer.getStyleClass().add("calendar-container");

        HBox calendarHeader = new HBox();
        calendarHeader.setSpacing(10);
        calendarHeader.setAlignment(Pos.CENTER_LEFT);
        calendarHeader.getStyleClass().add("calendar-header");

        Label calendarIcon = new Label("📅");
        Label calendarTitle = new Label("Calendar View");
        calendarTitle.getStyleClass().add("calendar-title");

        Button todayBtn = new Button("Today");
        todayBtn.getStyleClass().add("calendar-nav-btn");

        calendarHeader.getChildren().addAll(calendarIcon, calendarTitle, todayBtn);

        // Simple calendar grid placeholder
        VBox calendarGrid = new VBox();
        calendarGrid.setSpacing(2);
        calendarGrid.getStyleClass().add("calendar-grid");

        // Week days header
        HBox weekDays = new HBox();
        weekDays.getStyleClass().add("calendar-week-header");
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : days) {
            Label dayLabel = new Label(day);
            dayLabel.getStyleClass().add("calendar-day-header");
            HBox.setHgrow(dayLabel, Priority.ALWAYS);
            weekDays.getChildren().add(dayLabel);
        }

        // Sample week
        HBox sampleWeek = new HBox();
        sampleWeek.getStyleClass().add("calendar-week");
        for (int i = 1; i <= 7; i++) {
            VBox dayCell = new VBox();
            dayCell.getStyleClass().add("calendar-day-cell");
            Label dayNumber = new Label(String.valueOf(i));
            dayNumber.getStyleClass().add("calendar-day-number");
            dayCell.getChildren().add(dayNumber);
            HBox.setHgrow(dayCell, Priority.ALWAYS);
            sampleWeek.getChildren().add(dayCell);
        }

        calendarGrid.getChildren().addAll(weekDays, sampleWeek);
        calendarContainer.getChildren().addAll(calendarHeader, calendarGrid);
        blockContainer.getChildren().addAll(blockHeader, calendarContainer);

        return new EditorBlock("calendar", blockContainer, calendarContainer);
    }

    private HBox createBlockHeader(VBox blockContainer) {
        HBox blockHeader = new HBox();
        blockHeader.setSpacing(8);
        blockHeader.setAlignment(Pos.CENTER_LEFT);
        blockHeader.getStyleClass().add("block-header");

        Button addBtn = new Button("+");
        addBtn.getStyleClass().add("block-add-btn");
        addBtn.setOnAction(e -> showBlockMenu());

        Button dragHandle = new Button("⋮⋮");
        dragHandle.getStyleClass().add("block-drag-handle");

        Button deleteBtn = new Button("🗑️");
        deleteBtn.getStyleClass().add("block-delete-btn");
        deleteBtn.setOnAction(e -> removeBlock(blockContainer));

        blockHeader.getChildren().addAll(addBtn, dragHandle, deleteBtn);
        return blockHeader;
    }

    private void addBlockToEditor(EditorBlock block) {
        documentBlocks.add(block);

        // Remove initial block if it's still there and empty
        if (editorContent.getChildren().contains(initialBlock)) {
            editorContent.getChildren().remove(initialBlock);
        }

        editorContent.getChildren().add(block.getContainer());

        // Focus on the new block's input field
        if (block.getInputNode() != null) {
            block.getInputNode().requestFocus();
        }

        // Add new empty block at the end
        addEmptyBlockAtEnd();
    }

    private void addEmptyBlockAtEnd() {
        HBox emptyBlock = new HBox();
        emptyBlock.setSpacing(8);
        emptyBlock.setAlignment(Pos.CENTER_LEFT);
        emptyBlock.getStyleClass().add("editor-block");

        Button addBtn = new Button("+");
        addBtn.getStyleClass().add("add-block-btn");
        addBtn.setOnAction(e -> showBlockMenu());

        Label hint = new Label("Click + to add content or press / for commands");
        hint.getStyleClass().add("empty-block-hint");

        emptyBlock.getChildren().addAll(addBtn, hint);
        editorContent.getChildren().add(emptyBlock);
    }

    private void removeBlock(VBox blockContainer) {
        documentBlocks.removeIf(block -> block.getContainer() == blockContainer);
        editorContent.getChildren().remove(blockContainer);

        // If no blocks left, show initial block
        if (documentBlocks.isEmpty()) {
            editorContent.getChildren().add(initialBlock);
        }
    }

    @FXML
    private void saveDocument() {
        String content = generateDocumentContent();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Document Saved");
        alert.setHeaderText("Document: " + documentTitle.getText());
        alert.setContentText("Document saved successfully!\n\nBlocks: " + documentBlocks.size() +
                "\nSaved at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        alert.showAndWait();
    }

    @FXML
    private void exportDocument() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Document");
        fileChooser.setInitialFileName(documentTitle.getText() + ".txt");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showSaveDialog(documentTitle.getScene().getWindow());
        if (file != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Complete");
            alert.setHeaderText("Document Exported");
            alert.setContentText("Document exported to: " + file.getAbsolutePath());
            alert.showAndWait();
        }
    }

    private String generateDocumentContent() {
        StringBuilder content = new StringBuilder();
        content.append("Document: ").append(documentTitle.getText()).append("\n");
        content.append("Created: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n\n");

        for (EditorBlock block : documentBlocks) {
            content.append("Block Type: ").append(block.getType()).append("\n");
            content.append("Content: [Block content would be extracted here]\n\n");
        }

        return content.toString();
    }

    @FXML
    private void goToHome() {
        try {
            Stage stage = (Stage) documentTitle.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(loader.load(), 1400, 900);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // EditorBlock inner class
    private static class EditorBlock {
        private final String type;
        private final VBox container;
        private final javafx.scene.Node inputNode;

        public EditorBlock(String type, VBox container, javafx.scene.Node inputNode) {
            this.type = type;
            this.container = container;
            this.inputNode = inputNode;
        }

        public String getType() { return type; }
        public VBox getContainer() { return container; }
        public javafx.scene.Node getInputNode() { return inputNode; }
    }
}