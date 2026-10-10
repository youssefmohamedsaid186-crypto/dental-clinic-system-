package view;

import controller.DoctorController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Appointment;
import model.Doctor;
import model.User;

import java.time.LocalDate;
import java.time.LocalTime;

public class DoctorDashboardView {

    private final Stage stage;
    private final User currentUser;
    private final Doctor currentDoctor;
    private final DoctorController doctorController;
    private TableView<Appointment> appointmentTable;
    private socket.SocketClient socketClient;

    public DoctorDashboardView(Stage stage, User user, Doctor doctor) {
        this.stage = stage;
        this.currentUser = user;
        this.currentDoctor = doctor;
        this.doctorController = new DoctorController();
        initSocketClient();
    }

    private void initSocketClient() {
        socketClient = new socket.SocketClient();
        socketClient.setOnNotificationListener(msg -> {
            // Filter notification for current doctor (or all if ID matches)
            if (currentDoctor == null || msg.getDoctorId() == currentDoctor.getId() || msg.getDoctorId() <= 0) {
                javafx.application.Platform.runLater(() -> {
                    refreshTable();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Real-Time Notification");
                    alert.setHeaderText("🔔 New Appointment Request");
                    alert.setContentText("Patient: " + msg.getPatientName() + "\nDate: " + msg.getDate() + "\nTime: " + msg.getTime());
                    alert.show();
                });
            }
        });
        socketClient.connect();
    }

    public void show() {
        BorderPane root = new BorderPane();

        // Top Header Banner
        HBox header = new HBox();
        header.getStyleClass().add("header-banner");

        Label title = new Label("Doctor Portal - Dental Clinic System");
        title.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String docName = currentDoctor != null ? currentDoctor.getName() : currentUser.getUsername();
        Label userLabel = new Label("Welcome, " + docName);
        userLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("button-danger");
        logoutBtn.setOnAction(e -> {
            if (socketClient != null) {
                socketClient.disconnect();
            }
            new LoginView(stage).show();
        });

        HBox userBox = new HBox(15, userLabel, logoutBtn);
        userBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        header.getChildren().addAll(title, spacer, userBox);
        root.setTop(header);

        // TabPane Navigation
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Tab 1: Appointments
        Tab appointmentsTab = new Tab("My Assigned Appointments", createAppointmentsView());

        // Tab 2: Inventory Resources
        ResourceView resourceView = new ResourceView();
        Tab resourceTab = new Tab("Clinic Tool Status", resourceView.getView());

        tabPane.getTabs().addAll(appointmentsTab, resourceTab);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 950, 650);
        if (getClass().getResource("/css/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        }
        stage.setTitle("Dental Clinic System - Doctor Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createAppointmentsView() {
        VBox mainBox = new VBox(15);
        mainBox.setPadding(new Insets(20));

        Label sectionTitle = new Label("Assigned Patient Appointments");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        appointmentTable = new TableView<>();
        appointmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Appointment, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(40);

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient Name");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));

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

        appointmentTable.getColumns().addAll(idCol, patientCol, dateCol, timeCol, amountCol, statusCol, notesCol);
        refreshTable();

        // Action Buttons Row
        HBox actionBox = new HBox(15);
        actionBox.setPadding(new Insets(10, 0, 0, 0));

        Button acceptBtn = new Button("Accept Appointment");
        acceptBtn.getStyleClass().add("button-success");

        Button rejectBtn = new Button("Reject Appointment");
        rejectBtn.getStyleClass().add("button-danger");

        Button refreshBtn = new Button("Refresh List");
        refreshBtn.getStyleClass().add("button-secondary");

        actionBox.getChildren().addAll(acceptBtn, rejectBtn, refreshBtn);

        // Event Handlers
        acceptBtn.setOnAction(e -> {
            Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Please select an appointment from the table first.").showAndWait();
                return;
            }
            if (doctorController.acceptAppointment(selected.getId())) {
                refreshTable();
                new Alert(Alert.AlertType.INFORMATION, "Appointment status updated to ACCEPTED.").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update appointment status.").showAndWait();
            }
        });

        rejectBtn.setOnAction(e -> {
            Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Please select an appointment from the table first.").showAndWait();
                return;
            }
            if (doctorController.rejectAppointment(selected.getId())) {
                refreshTable();
                new Alert(Alert.AlertType.INFORMATION, "Appointment status updated to REJECTED.").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update appointment status.").showAndWait();
            }
        });

        refreshBtn.setOnAction(e -> refreshTable());

        mainBox.getChildren().addAll(sectionTitle, appointmentTable, actionBox);
        return mainBox;
    }

    private void refreshTable() {
        if (currentDoctor != null) {
            appointmentTable.setItems(FXCollections.observableArrayList(doctorController.getAppointmentsForDoctor(currentDoctor.getId())));
        }
    }
}
