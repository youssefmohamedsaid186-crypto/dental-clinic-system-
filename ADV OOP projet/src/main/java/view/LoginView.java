package view;

import controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Doctor;
import model.Patient;
import model.Receptionist;
import model.User;

public class LoginView {

    private final Stage stage;
    private final LoginController loginController;

    public LoginView(Stage stage) {
        this.stage = stage;
        this.loginController = new LoginController();
    }

    public void show() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 30px;");

        // Login Card Container
        VBox card = new VBox(15);
        card.setMaxWidth(400);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER_LEFT);

        // Header Title
        Label titleLabel = new Label("Dental Clinic System");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        Label subtitleLabel = new Label("Sign in to access your portal");
        subtitleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        VBox headerBox = new VBox(4, titleLabel, subtitleLabel);

        // Input Fields
        Label userLabel = new Label("Username");
        userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        TextField userField = new TextField();
        userField.setPromptText("Enter username");
        userField.getStyleClass().add("text-field");

        Label passLabel = new Label("Password");
        passLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter password");
        passField.getStyleClass().add("password-field");

        // Error Feedback Label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12px;");
        errorLabel.setVisible(false);

        // Login Button
        Button loginBtn = new Button("Sign In");
        loginBtn.getStyleClass().add("button-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        // Quick Demo Accounts Helper Box
        VBox helpBox = new VBox(4);
        helpBox.setStyle("-fx-background-color: #F1F5F9; -fx-padding: 10px; -fx-background-radius: 6px;");
        Label helpTitle = new Label("Demo Accounts:");
        helpTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #475569;");
        Label helpAdmin = new Label("• Admin: admin / admin123");
        Label helpDoctor = new Label("• Doctor: dr_smith / doctor123");
        Label helpRecep = new Label("• Receptionist: recep_john / recep123");
        Label helpPatient = new Label("• Patient: patient_alice / patient123");

        helpAdmin.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        helpDoctor.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        helpRecep.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        helpPatient.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        helpBox.getChildren().addAll(helpTitle, helpAdmin, helpDoctor, helpRecep, helpPatient);

        // Action Listener
        loginBtn.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();

            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                errorLabel.setText("Please enter both username and password.");
                errorLabel.setVisible(true);
                return;
            }

            User user = loginController.login(username, password);

            if (user == null) {
                errorLabel.setText("Invalid username or password.");
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
                routeUser(user);
            }
        });

        card.getChildren().addAll(headerBox, userLabel, userField, passLabel, passField, errorLabel, loginBtn, helpBox);
        root.getChildren().add(card);

        Scene scene = new Scene(root, 900, 650);
        if (getClass().getResource("/css/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        }
        stage.setTitle("Dental Clinic System - Login");
        stage.setScene(scene);
        stage.show();
    }

    private void routeUser(User user) {
        switch (user.getRole().toUpperCase()) {
            case "ADMIN":
                new AdminDashboardView(stage, user).show();
                break;
            case "DOCTOR":
                Doctor doctor = loginController.getDoctorProfile(user.getId());
                new DoctorDashboardView(stage, user, doctor).show();
                break;
            case "RECEPTIONIST":
                Receptionist receptionist = loginController.getReceptionistProfile(user.getId());
                new ReceptionDashboardView(stage, user, receptionist).show();
                break;
            case "PATIENT":
                Patient patient = loginController.getPatientProfile(user.getId());
                new PatientDashboardView(stage, user, patient).show();
                break;
            default:
                Alert alert = new Alert(Alert.AlertType.ERROR, "Unknown user role: " + user.getRole());
                alert.showAndWait();
                break;
        }
    }
}
