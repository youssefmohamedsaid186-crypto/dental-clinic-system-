package view;

import controller.ReceptionController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Appointment;
import model.Doctor;
import model.Patient;
import model.Receptionist;
import model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReceptionDashboardView {

    private final Stage stage;
    private final User currentUser;
    private final Receptionist currentReceptionist;
    private final ReceptionController receptionController;

    private TableView<Appointment> appointmentTable;
    private ComboBox<Patient> patientComboBox;
    private ComboBox<Doctor> doctorComboBox;

    public ReceptionDashboardView(Stage stage, User user, Receptionist receptionist) {
        this.stage = stage;
        this.currentUser = user;
        this.currentReceptionist = receptionist;
        this.receptionController = new ReceptionController();
    }

    public void show() {
        BorderPane root = new BorderPane();

        // Top Header Banner
        HBox header = new HBox();
        header.getStyleClass().add("header-banner");

        Label title = new Label("Reception Desk - Dental Clinic System");
        title.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String recepName = currentReceptionist != null ? currentReceptionist.getName() : currentUser.getUsername();
        Label userLabel = new Label("Welcome, " + recepName + " (Receptionist)");
        userLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("button-danger");
        logoutBtn.setOnAction(e -> new LoginView(stage).show());

        HBox userBox = new HBox(15, userLabel, logoutBtn);
        userBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        header.getChildren().addAll(title, spacer, userBox);
        root.setTop(header);

        // TabPane Navigation
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Tab 1: Reception Operations (Patient Reg & Appointments)
        Tab recepTab = new Tab("Patient & Appointment Scheduling", createReceptionOperationsView());

        // Tab 2: Inventory Resources
        ResourceView resourceView = new ResourceView();
        Tab resourceTab = new Tab("Clinic Tool Status", resourceView.getView());

        // Tab 3: Revenue Overview
        RevenueView revenueView = new RevenueView();
        Tab revenueTab = new Tab("Revenue Reports", revenueView.getView());
        revenueTab.setOnSelectionChanged(e -> {
            if (revenueTab.isSelected()) {
                revenueView.refreshData();
            }
        });

        tabPane.getTabs().addAll(recepTab, resourceTab, revenueTab);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1050, 720);
        if (getClass().getResource("/css/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        }
        stage.setTitle("Dental Clinic System - Receptionist Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createReceptionOperationsView() {
        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(20));

        // 1. Top Section: Patient Registration & Appointment Scheduling
        HBox formsBox = new HBox(20);

        // Patient Registration Card
        VBox patientCard = new VBox(10);
        patientCard.getStyleClass().add("card");
        HBox.setHgrow(patientCard, Priority.ALWAYS);

        Label pTitle = new Label("Register New Patient");
        pTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        GridPane pGrid = new GridPane();
        pGrid.setHgap(8);
        pGrid.setVgap(8);

        TextField nameField = new TextField(); nameField.setPromptText("Full Name");
        TextField phoneField = new TextField(); phoneField.setPromptText("Phone Number");
        TextField emailField = new TextField(); emailField.setPromptText("Email Address");
        DatePicker dobPicker = new DatePicker(LocalDate.of(1990, 1, 1));
        ComboBox<String> genderCombo = new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Other"));
        genderCombo.setValue("Male");
        TextField addressField = new TextField(); addressField.setPromptText("Address");

        pGrid.add(new Label("Name:"), 0, 0); pGrid.add(nameField, 1, 0);
        pGrid.add(new Label("Phone:"), 0, 1); pGrid.add(phoneField, 1, 1);
        pGrid.add(new Label("Email:"), 0, 2); pGrid.add(emailField, 1, 2);
        pGrid.add(new Label("DOB:"), 0, 3); pGrid.add(dobPicker, 1, 3);
        pGrid.add(new Label("Gender:"), 0, 4); pGrid.add(genderCombo, 1, 4);
        pGrid.add(new Label("Address:"), 0, 5); pGrid.add(addressField, 1, 5);

        Button regPatientBtn = new Button("Register Patient");
        regPatientBtn.getStyleClass().add("button-primary");
        pGrid.add(regPatientBtn, 1, 6);

        patientCard.getChildren().addAll(pTitle, pGrid);

        // Appointment Creation Card
        VBox apptCard = new VBox(10);
        apptCard.getStyleClass().add("card");
        HBox.setHgrow(apptCard, Priority.ALWAYS);

        Label aTitle = new Label("Schedule New Appointment");
        aTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        GridPane aGrid = new GridPane();
        aGrid.setHgap(8);
        aGrid.setVgap(8);

        patientComboBox = new ComboBox<>();
        patientComboBox.setPromptText("Select Patient");

        doctorComboBox = new ComboBox<>();
        doctorComboBox.setPromptText("Select Doctor");

        DatePicker apptDatePicker = new DatePicker(LocalDate.now());

        ComboBox<String> timeCombo = new ComboBox<>(FXCollections.observableArrayList(
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "14:00", "14:30", "15:00", "15:30", "16:00"
        ));
        timeCombo.setValue("10:00");

        TextField amountField = new TextField("150.00");
        TextField notesField = new TextField(); notesField.setPromptText("Reason / Notes");

        aGrid.add(new Label("Patient:"), 0, 0); aGrid.add(patientComboBox, 1, 0);
        aGrid.add(new Label("Doctor:"), 0, 1); aGrid.add(doctorComboBox, 1, 1);
        aGrid.add(new Label("Date:"), 0, 2); aGrid.add(apptDatePicker, 1, 2);
        aGrid.add(new Label("Time:"), 0, 3); aGrid.add(timeCombo, 1, 3);
        aGrid.add(new Label("Amount ($):"), 0, 4); aGrid.add(amountField, 1, 4);
        aGrid.add(new Label("Notes:"), 0, 5); aGrid.add(notesField, 1, 5);

        Button scheduleBtn = new Button("Schedule Appointment");
        scheduleBtn.getStyleClass().add("button-success");
        aGrid.add(scheduleBtn, 1, 6);

        apptCard.getChildren().addAll(aTitle, aGrid);
        formsBox.getChildren().addAll(patientCard, apptCard);

        // Populate dropdowns
        refreshDropdowns();

        // Register Patient Event
        regPatientBtn.setOnAction(e -> {
            Patient registered = receptionController.registerPatient(
                nameField.getText(), phoneField.getText(), emailField.getText(),
                dobPicker.getValue(), genderCombo.getValue(), addressField.getText()
            );
            if (registered != null) {
                refreshDropdowns();
                patientComboBox.setValue(registered);
                nameField.clear(); phoneField.clear(); emailField.clear(); addressField.clear();
                new Alert(Alert.AlertType.INFORMATION, "Patient registered successfully! Selected in dropdown.").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to register patient. Check inputs.").showAndWait();
            }
        });

        // Schedule Appointment Event
        scheduleBtn.setOnAction(e -> {
            Patient p = patientComboBox.getValue();
            Doctor d = doctorComboBox.getValue();
            LocalDate date = apptDatePicker.getValue();
            String timeStr = timeCombo.getValue();

            if (p == null || d == null || date == null || timeStr == null) {
                new Alert(Alert.AlertType.WARNING, "Please select Patient, Doctor, Date, and Time.").showAndWait();
                return;
            }

            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                LocalTime time = LocalTime.parse(timeStr);
                boolean success = receptionController.createAppointment(p.getId(), d.getId(), date, time, amount, notesField.getText());

                if (success) {
                    refreshAppointmentTable();
                    notesField.clear();
                    new Alert(Alert.AlertType.INFORMATION, "Appointment scheduled with status PENDING!").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to schedule appointment.").showAndWait();
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid amount format or time value.").showAndWait();
            }
        });

        // 2. Appointments Table Section
        VBox tableBox = new VBox(10);
        HBox tableHeader = new HBox();
        Label tableTitle = new Label("All Appointments");
        tableTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button payBtn = new Button("Process Payment (Mark PAID)");
        payBtn.getStyleClass().add("button-primary");

        Button refreshBtn = new Button("Refresh List");
        refreshBtn.getStyleClass().add("button-secondary");

        tableHeader.getChildren().addAll(tableTitle, spacer, payBtn, new Label("  "), refreshBtn);

        appointmentTable = new TableView<>();
        appointmentTable.setPrefHeight(250);
        appointmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Appointment, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(40);

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient Name");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));

        TableColumn<Appointment, String> doctorCol = new TableColumn<>("Doctor Name");
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));

        TableColumn<Appointment, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));

        TableColumn<Appointment, LocalTime> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));

        TableColumn<Appointment, Double> amountCol = new TableColumn<>("Amount ($)");
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

        appointmentTable.getColumns().addAll(idCol, patientCol, doctorCol, dateCol, timeCol, amountCol, statusCol);
        refreshAppointmentTable();

        // Process Payment Event
        payBtn.setOnAction(e -> {
            Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Please select an appointment from the table to process payment.").showAndWait();
                return;
            }
            if ("PAID".equalsIgnoreCase(selected.getStatus())) {
                new Alert(Alert.AlertType.INFORMATION, "This appointment is already marked as PAID.").showAndWait();
                return;
            }

            ChoiceDialog<String> methodDialog = new ChoiceDialog<>("CASH", "CASH", "CREDIT_CARD", "DEBIT_CARD", "INSURANCE");
            methodDialog.setTitle("Process Payment");
            methodDialog.setHeaderText("Processing payment for " + selected.getPatientName());
            methodDialog.setContentText("Select Payment Method:");

            methodDialog.showAndWait().ifPresent(method -> {
                boolean success = receptionController.processPayment(selected, method);
                if (success) {
                    refreshAppointmentTable();
                    new Alert(Alert.AlertType.INFORMATION, "Payment of $" + String.format("%.2f", selected.getAmountDue()) + " processed successfully!\nAppointment status changed to PAID.").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to process payment.").showAndWait();
                }
            });
        });

        refreshBtn.setOnAction(e -> refreshAppointmentTable());

        tableBox.getChildren().addAll(tableHeader, appointmentTable);

        VBox scrollContent = new VBox(15, formsBox, tableBox);
        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);

        mainBox.getChildren().add(scrollPane);
        return mainBox;
    }

    private void refreshDropdowns() {
        if (patientComboBox != null) {
            patientComboBox.setItems(FXCollections.observableArrayList(receptionController.getAllPatients()));
        }
        if (doctorComboBox != null) {
            doctorComboBox.setItems(FXCollections.observableArrayList(receptionController.getAllDoctors()));
        }
    }

    private void refreshAppointmentTable() {
        if (appointmentTable != null) {
            appointmentTable.setItems(FXCollections.observableArrayList(receptionController.getAllAppointments()));
        }
    }
}
