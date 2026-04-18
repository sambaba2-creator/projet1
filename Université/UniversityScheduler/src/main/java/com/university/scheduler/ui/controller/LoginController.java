package com.university.scheduler.ui.controller;

import com.university.scheduler.dao.UserDAO;
import com.university.scheduler.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private CheckBox showPasswordCheck;
    // registration fields
    @FXML private TextField regNameField;
    @FXML private TextField regEmailField;
    @FXML private PasswordField regPasswordField;
    @FXML private TextField regPasswordVisibleField;
    @FXML private CheckBox showRegPasswordCheck;
    @FXML private ComboBox<String> regRoleCombo;

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        this.userDAO = new UserDAO();
        if (regRoleCombo != null) {
            regRoleCombo.setItems(javafx.collections.FXCollections.observableArrayList("STUDENT", "TEACHER"));
        }
        try {
            if (passwordVisibleField != null && passwordField != null) {
                Bindings.bindBidirectional(passwordVisibleField.textProperty(), passwordField.textProperty());
                passwordVisibleField.setVisible(false);
                passwordVisibleField.setManaged(false);
            }
            if (regPasswordVisibleField != null && regPasswordField != null) {
                Bindings.bindBidirectional(regPasswordVisibleField.textProperty(), regPasswordField.textProperty());
                regPasswordVisibleField.setVisible(false);
                regPasswordVisibleField.setManaged(false);
            }

            if (showPasswordCheck != null) {
                showPasswordCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    if (passwordVisibleField != null && passwordField != null) {
                        passwordVisibleField.setVisible(newVal);
                        passwordVisibleField.setManaged(newVal);
                        passwordField.setVisible(!newVal);
                        passwordField.setManaged(!newVal);
                    }
                });
            }

            if (showRegPasswordCheck != null) {
                showRegPasswordCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    if (regPasswordVisibleField != null && regPasswordField != null) {
                        regPasswordVisibleField.setVisible(newVal);
                        regPasswordVisibleField.setManaged(newVal);
                        regPasswordField.setVisible(!newVal);
                        regPasswordField.setManaged(!newVal);
                    }
                });
            }
        } catch (Exception e) {
            logger.debug("Password visibility binding skipped: {}", e.getMessage());
        }
    }

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Email et mot de passe requis", Alert.AlertType.ERROR);
            return;
        }

        if (userDAO.authenticate(email, password)) {
            User user = userDAO.getUserByEmail(email);
            logger.info("User {} logged in with role {}", user.getName(), user.getRole());
            
            try {
                loadDashboard(user);
            } catch (IOException e) {
                logger.error("Error loading dashboard", e);
                showAlert("Erreur", "Erreur lors du chargement du tableau de bord", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Erreur", "Identifiants invalides", Alert.AlertType.ERROR);
            passwordField.clear();
        }
    }

    private void loadDashboard(User user) throws IOException {
        String fxmlFile;
        
        switch (user.getRole()) {
            case "ADMIN":
                fxmlFile = "/fxml/admin_dashboard.fxml";
                break;
            case "SCHEDULE_MANAGER":
                fxmlFile = "/fxml/manager_dashboard.fxml";
                break;
            case "TEACHER":
                fxmlFile = "/fxml/teacher_dashboard.fxml";
                break;
            case "STUDENT":
                fxmlFile = "/fxml/student_dashboard.fxml";
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + user.getRole());
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Scene scene = new Scene(loader.load(), 1200, 800);
        
        String css = getClass().getResource("/css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("University Scheduler - " + user.getRole());
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.show();

        // Close login window
        Stage loginStage = (Stage) emailField.getScene().getWindow();
        loginStage.close();
    }

    @FXML
    public void handleRegister() {
        String name = regNameField.getText();
        String email = regEmailField.getText();
        String password = regPasswordField.getText();
        String role = regRoleCombo.getValue();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Erreur", "Tous les champs sont requis", Alert.AlertType.ERROR);
            return;
        }

        // Simple check for existing email
        if (userDAO.getUserByEmail(email) != null) {
            showAlert("Erreur", "Un utilisateur avec cet email existe déjà", Alert.AlertType.ERROR);
            return;
        }

        User user = new User(name, email, password, role);
        int userId = userDAO.createUser(user);
        boolean userCreated = userId > 0 || userDAO.getUserByEmail(email) != null;

        if (userCreated) {
            showToast("Vous êtes inscrit avec succès.");
            // clear registration fields
            regNameField.clear();
            regEmailField.clear();
            regPasswordField.clear();
            regRoleCombo.setValue(null);
            // also clear login inputs to reflect fresh state
            emailField.clear();
            passwordField.clear();
        } else {
            showAlert("Erreur", "Erreur lors de l'inscription", Alert.AlertType.ERROR);
        }
    }

    private void showToast(String message) {
        showToast(message, "-fx-background-color: rgba(0,0,0,0.75); -fx-text-fill: white;");
    }

    private void showToast(String message, String textCss) {
        try {
            Stage owner = (Stage) emailField.getScene().getWindow();
            Stage toastStage = new Stage();
            toastStage.initOwner(owner);
            toastStage.initStyle(StageStyle.TRANSPARENT);

            Label text = new Label(message);
            text.setStyle(textCss + " -fx-padding: 10 20; -fx-background-radius: 6;");

            StackPane root = new StackPane(text);
            root.setStyle("-fx-padding: 6;");

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            toastStage.setScene(scene);

            // position at bottom center of owner
            double toastWidth = Math.min(420, owner.getWidth() - 60);
            toastStage.setWidth(toastWidth);
            toastStage.setHeight(60);
            double x = owner.getX() + owner.getWidth() / 2 - toastWidth / 2;
            double y = owner.getY() + owner.getHeight() - 140;
            toastStage.setX(x);
            toastStage.setY(y);

            toastStage.show();

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            PauseTransition wait = new PauseTransition(Duration.seconds(3));
            wait.setOnFinished(e -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(ev -> toastStage.close());
                fadeOut.play();
            });
            wait.play();
        } catch (Exception e) {
            logger.error("Error showing toast", e);
        }
    }


    private void showAlert(String title, String content, Alert.AlertType type) {
        if (type == Alert.AlertType.ERROR) {
            showToast(content, "-fx-background-color: rgba(192,57,43,0.95); -fx-text-fill: white;");
        } else if (type == Alert.AlertType.INFORMATION) {
            showToast(content, "-fx-background-color: rgba(39,174,96,0.95); -fx-text-fill: white;");
        } else {
            showToast(content, "-fx-background-color: rgba(44,62,80,0.95); -fx-text-fill: white;");
        }
    }
}
