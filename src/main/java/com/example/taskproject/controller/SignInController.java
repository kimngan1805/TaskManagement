package com.example.taskproject.controller;
import com.example.taskproject.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SignInController {
    @FXML
    private TextField emailLogin;
    @FXML
    private AnchorPane rootPane;

    @FXML
    private PasswordField passwordLogin;
    @FXML
    private VBox loginPane;
    @FXML
    private VBox mainPane;

    @FXML
    private VBox registerPane;

    @FXML
    private void showRegisterForm() {
        mainPane.setVisible(false);
        registerPane.setVisible(true);
    }

    @FXML
    private void backToMain() {
        registerPane.setVisible(false);
        mainPane.setVisible(true);
    }
    @FXML
    private void showLoginForm() {
        mainPane.setVisible(false);
        registerPane.setVisible(false);
        loginPane.setVisible(true);
    }
    // 👇 NEW: Back from login form
    @FXML
    private void backFromLogin() {
        loginPane.setVisible(false);
        mainPane.setVisible(true);
    }

    // 👇 NEW: From login → sign up
    @FXML
    private void goToRegisterFromLogin() {
        loginPane.setVisible(false);
        registerPane.setVisible(true);
    }
    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800); // Kích thước mặc định

            // Load CSS
            String cssPath = HelloApplication.class.getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);

            Stage stage = new Stage();
            stage.setTitle("Student Portal");
            stage.setScene(scene);
            stage.setMaximized(true); // Mở full màn hình
            stage.show();

            // Đóng cửa sổ login hiện tại
            ((Stage)(((javafx.scene.Node)event.getSource()).getScene().getWindow())).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
