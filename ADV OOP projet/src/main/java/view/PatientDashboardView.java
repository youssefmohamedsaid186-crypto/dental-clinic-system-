package view;

import database.AppointmentDAO;
import database.PaymentDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Appointment;
import model.Patient;
import model.Payment;
import model.User;

import java.time.LocalDate;
import java.time.LocalTime;

public class PatientDashboardView {

    private final Stage stage;
    private final User currentUser;
    private final Patient currentPatient;
    private final AppointmentDAO appointmentDAO;
    private final PaymentDAO paymentDAO;

    public PatientDashboardView(Stage stage, User user, Patient patient) {
        this.stage = stage;
        this.currentUser = user;
        this.currentPatient = patient;
        this.appointmentDAO = new AppointmentDAO();
        this.paymentDAO = new PaymentDAO();
    }

    public void show() {
        BorderPane root = new BorderPane();

        // Top Header Banner
        HBox header = new HBox();
        header.getStyleClass().add("header-banner");

        Label title = new Label("Patient Portal - Dental Clinic System");
        title.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String pName = currentPatient != null ? currentPatient.getName() : currentUser.getUsername();
        Label userLabel = new Label("Welcome, " + pName);
        userLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("button-danger");
        logoutBtn.setOnAction(e -> new LoginView(stage).show());

        HBox userBox = new HBox(15, userLabel, logoutBtn);
        userBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        header.getChildren().addAll(title, spacer, userBox);
        root.setTop(header);

        // Main Content VBox
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        // 1. Personal Details Card
        VBox profileCard = new VBox(10);
        profileCard.getStyleClass().add("card");

        Label cardTitle = new Label("My Personal Profile Details");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        GridPane profileGrid = new GridPane();
        profileGrid.setHgap(15);
        profileGrid.setVgap(8);

        if (currentPatient != null) {
            profileGrid.add(new Label("Full Name:"), 0, 0);
            Label nameVal = new Label(currentPatient.getName());
            nameVal.setStyle("-fx-font-weight: bold;");
            profileGrid.add(nameVal, 1, 0);

            profileGrid.add(new Label("Phone Number:"), 0, 1);
            profileGrid.add(new Label(currentPatient.getPhone()), 1, 1);

            profileGrid.add(new Label("Email Address:"), 2, 0);
            profileGrid.add(new Label(currentPatient.getEmail()), 3, 0);

            profileGrid.add(new Label("Date of Birth:"), 2, 1);
            profileGrid.add(new Label(currentPatient.getDob() != null ? currentPatient.getDob().toString() : "N/A"), 3, 1);

            profileGrid.add(new Label("Gender:"), 0, 2);
            profileGrid.add(new Label(currentPatient.getGender()), 1, 2);

            profileGrid.add(new Label("Address:"), 2, 2);
            profileGrid.add(new Label(currentPatient.getAddress()), 3, 2);
        } else {
            profileGrid.add(new Label("No linked patient record found for user: " + currentUser.getUsername()), 0, 0);
        }

        profileCard.getChildren().addAll(cardTitle, profileGrid);

        // 2. Personal Appointments Table
        Label apptTitle = new Label("My Appointments & Status");
        apptTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        TableView<Appointment> appointmentTable = new TableView<>();
        appointmentTable.setPrefHeight(200);
        appointmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Appointment, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(40);

        TableColumn<Appointment, String> doctorCol = new TableColumn<>("Doctor Name");
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));

        TableColumn<Appointment, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));

        TableColumn<Appointment, LocalTime> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));

        TableColumn<Appointment, Double> amountCol = new TableColumn<>("Amount Due ($)");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amountDue"));
        amountCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("$%.2f", item));
            }
        });

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    switch (item.toUpperCase()) {
                        case "PENDING":
                            badge.getStyleClass().add("badge-pending");
                            break;
                        case "ACCEPTED":
                            badge.getStyleClass().add("badge-accepted");
                            break;
                        case "REJECTED":
                            badge.getStyleClass().add("badge-rejected");
                            break;
                        case "PAID":
                            badge.getStyleClass().add("badge-paid");
                            break;
                        default:
                            badge.setStyle("-fx-padding: 4px;");
                            break;
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        TableColumn<Appointment, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));

        appointmentTable.getColumns().addAll(idCol, doctorCol, dateCol, timeCol, amountCol, statusCol, notesCol);

        // 3. Payment History Table
        Label payTitle = new Label("My Payment Receipts");
        payTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        TableView<Payment> paymentTable = new TableView<>();
        paymentTable.setPrefHeight(150);
        paymentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Payment, Integer> payIdCol = new TableColumn<>("Receipt ID");
        payIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Payment, Double> payAmtCol = new TableColumn<>("Amount Paid ($)");
        payAmtCol.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        payAmtCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("$%.2f", item));
            }
        });

        TableColumn<Payment, Object> payDateCol = new TableColumn<>("Date");
        payDateCol.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));

        TableColumn<Payment, String> payMethodCol = new TableColumn<>("Method");
        payMethodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));

        paymentTable.getColumns().addAll(payIdCol, payAmtCol, payDateCol, payMethodCol);

        // Load strictly personal data
        if (currentPatient != null) {
            appointmentTable.setItems(FXCollections.observableArrayList(appointmentDAO.getAppointmentsByPatientId(currentPatient.getId())));
            paymentTable.setItems(FXCollections.observableArrayList(paymentDAO.getPaymentsByPatientId(currentPatient.getId())));
        }

        VBox scrollBox = new VBox(15, profileCard, apptTitle, appointmentTable, payTitle, paymentTable);
        ScrollPane scrollPane = new ScrollPane(scrollBox);
        scrollPane.setFitToWidth(true);

        mainContent.getChildren().add(scrollPane);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 950, 650);
        if (getClass().getResource("/css/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        }
        stage.setTitle("Dental Clinic System - Patient Portal");
        stage.setScene(scene);
        stage.show();
    }
}
