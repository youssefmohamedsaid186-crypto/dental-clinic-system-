package view;

import controller.AdminController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Doctor;
import model.Receptionist;
import model.User;

public class AdminDashboardView {

    private final Stage stage;
    private final User currentUser;
    private final AdminController adminController;

    public AdminDashboardView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.adminController = new AdminController();
    }

    public void show() {
        BorderPane root = new BorderPane();

        // Top Header Banner
        HBox header = new HBox();
        header.getStyleClass().add("header-banner");
        
        Label title = new Label("Admin Dashboard - Dental Clinic System");
        title.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("Welcome, " + currentUser.getUsername() + " (ADMIN)");
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

        // Tab 1: Staff Roster & Registration
        Tab staffTab = new Tab("Staff Roster & Accounts", createStaffManagementView());
        
        // Tab 2: Inventory Resources
        ResourceView resourceView = new ResourceView();
        Tab resourceTab = new Tab("Clinic Inventory", resourceView.getView());

        // Tab 3: Revenue & Payments
        RevenueView revenueView = new RevenueView();
        Tab revenueTab = new Tab("Financial Revenue", revenueView.getView());
        revenueTab.setOnSelectionChanged(e -> {
            if (revenueTab.isSelected()) {
                revenueView.refreshData();
            }
        });

        tabPane.getTabs().addAll(staffTab, resourceTab, revenueTab);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1000, 700);
        if (getClass().getResource("/css/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        }
        stage.setTitle("Dental Clinic System - Admin Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createStaffManagementView() {
        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(20));

        // 1. Staff Registration Forms Box
        HBox formsBox = new HBox(20);

        // Doctor Registration Card
        VBox docCard = new VBox(10);
        docCard.getStyleClass().add("card");
        HBox.setHgrow(docCard, Priority.ALWAYS);

        Label docTitle = new Label("Register New Doctor");
        docTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        GridPane docGrid = new GridPane();
        docGrid.setHgap(8);
        docGrid.setVgap(8);

        TextField docUserField = new TextField();
        docUserField.setPromptText("Username");
        PasswordField docPassField = new PasswordField();
        docPassField.setPromptText("Password");
        TextField docNameField = new TextField();
        docNameField.setPromptText("Full Name");
        TextField docSpecField = new TextField();
        docSpecField.setPromptText("Specialization");
        TextField docPhoneField = new TextField();
        docPhoneField.setPromptText("Phone Number");
        TextField docEmailField = new TextField();
        docEmailField.setPromptText("Email Address");

        docGrid.add(new Label("Username:"), 0, 0); docGrid.add(docUserField, 1, 0);
        docGrid.add(new Label("Password:"), 0, 1); docGrid.add(docPassField, 1, 1);
        docGrid.add(new Label("Name:"), 0, 2); docGrid.add(docNameField, 1, 2);
        docGrid.add(new Label("Specialization:"), 0, 3); docGrid.add(docSpecField, 1, 3);
        docGrid.add(new Label("Phone:"), 0, 4); docGrid.add(docPhoneField, 1, 4);
        docGrid.add(new Label("Email:"), 0, 5); docGrid.add(docEmailField, 1, 5);

        Button addDocBtn = new Button("Add Doctor");
        addDocBtn.getStyleClass().add("button-primary");
        docGrid.add(addDocBtn, 1, 6);

        docCard.getChildren().addAll(docTitle, docGrid);

        // Receptionist Registration Card
        VBox recepCard = new VBox(10);
        recepCard.getStyleClass().add("card");
        HBox.setHgrow(recepCard, Priority.ALWAYS);

        Label recepTitle = new Label("Register New Receptionist");
        recepTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        GridPane recepGrid = new GridPane();
        recepGrid.setHgap(8);
        recepGrid.setVgap(8);

        TextField recepUserField = new TextField();
        recepUserField.setPromptText("Username");
        PasswordField recepPassField = new PasswordField();
        recepPassField.setPromptText("Password");
        TextField recepNameField = new TextField();
        recepNameField.setPromptText("Full Name");
        TextField recepPhoneField = new TextField();
        recepPhoneField.setPromptText("Phone Number");
        TextField recepEmailField = new TextField();
        recepEmailField.setPromptText("Email Address");

        recepGrid.add(new Label("Username:"), 0, 0); recepGrid.add(recepUserField, 1, 0);
        recepGrid.add(new Label("Password:"), 0, 1); recepGrid.add(recepPassField, 1, 1);
        recepGrid.add(new Label("Name:"), 0, 2); recepGrid.add(recepNameField, 1, 2);
        recepGrid.add(new Label("Phone:"), 0, 3); recepGrid.add(recepPhoneField, 1, 3);
        recepGrid.add(new Label("Email:"), 0, 4); recepGrid.add(recepEmailField, 1, 4);

        Button addRecepBtn = new Button("Add Receptionist");
        addRecepBtn.getStyleClass().add("button-primary");
        recepGrid.add(addRecepBtn, 1, 5);

        recepCard.getChildren().addAll(recepTitle, recepGrid);
        formsBox.getChildren().addAll(docCard, recepCard);

        // 2. Staff Roster Tables
        Label docListLabel = new Label("Doctor Roster");
        docListLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<Doctor> docTable = new TableView<>();
        docTable.setPrefHeight(180);
        docTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Doctor, Integer> dIdCol = new TableColumn<>("ID");
        dIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Doctor, String> dNameCol = new TableColumn<>("Name");
        dNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Doctor, String> dSpecCol = new TableColumn<>("Specialization");
        dSpecCol.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        TableColumn<Doctor, String> dPhoneCol = new TableColumn<>("Phone");
        dPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<Doctor, String> dEmailCol = new TableColumn<>("Email");
        dEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        docTable.getColumns().addAll(dIdCol, dNameCol, dSpecCol, dPhoneCol, dEmailCol);

        Label recepListLabel = new Label("Receptionist Roster");
        recepListLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<Receptionist> recepTable = new TableView<>();
        recepTable.setPrefHeight(180);
        recepTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Receptionist, Integer> rIdCol = new TableColumn<>("ID");
        rIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Receptionist, String> rNameCol = new TableColumn<>("Name");
        rNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Receptionist, String> rPhoneCol = new TableColumn<>("Phone");
        rPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<Receptionist, String> rEmailCol = new TableColumn<>("Email");
        rEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        recepTable.getColumns().addAll(rIdCol, rNameCol, rPhoneCol, rEmailCol);

        // Helper refresh
        Runnable refreshTables = () -> {
            docTable.setItems(FXCollections.observableArrayList(adminController.getAllDoctors()));
            recepTable.setItems(FXCollections.observableArrayList(adminController.getAllReceptionists()));
        };
        refreshTables.run();

        // Button Actions
        addDocBtn.setOnAction(e -> {
            boolean success = adminController.registerDoctor(
                docUserField.getText(), docPassField.getText(), docNameField.getText(),
                docSpecField.getText(), docPhoneField.getText(), docEmailField.getText()
            );
            if (success) {
                refreshTables.run();
                docUserField.clear(); docPassField.clear(); docNameField.clear();
                docSpecField.clear(); docPhoneField.clear(); docEmailField.clear();
                new Alert(Alert.AlertType.INFORMATION, "Doctor account created successfully!").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to create doctor account. Username may already exist.").showAndWait();
            }
        });

        addRecepBtn.setOnAction(e -> {
            boolean success = adminController.registerReceptionist(
                recepUserField.getText(), recepPassField.getText(), recepNameField.getText(),
                recepPhoneField.getText(), recepEmailField.getText()
            );
            if (success) {
                refreshTables.run();
                recepUserField.clear(); recepPassField.clear(); recepNameField.clear();
                recepPhoneField.clear(); recepEmailField.clear();
                new Alert(Alert.AlertType.INFORMATION, "Receptionist account created successfully!").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to create receptionist account. Username may already exist.").showAndWait();
            }
        });

        VBox scrollContent = new VBox(15, formsBox, docListLabel, docTable, recepListLabel, recepTable);
        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);

        mainBox.getChildren().add(scrollPane);
        return mainBox;
    }
}
