package com.example.taskproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NoteAppication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("note-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800); // Tăng kích thước cho phù hợp

        // Load CSS file
        String cssPath = HelloApplication.class.getResource("/css/note.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.setTitle("Student Portal");
        stage.setScene(scene);
        stage.setMaximized(true); // Mở full màn hình
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
