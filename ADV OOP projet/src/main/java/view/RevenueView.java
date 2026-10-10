package view;

import controller.RevenueController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Payment;

import java.sql.Timestamp;

public class RevenueView {

    private final RevenueController revenueController;
    private TableView<Payment> tableView;
    private Label totalRevenueLabel;

    public RevenueView() {
        this.revenueController = new RevenueController();
    }

    public VBox getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        // Header Bar
        HBox headerBar = new HBox();
        Label titleLabel = new Label("Clinic Revenue & Financial Overview");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = new Button("Refresh Report");
        refreshBtn.getStyleClass().add("button-secondary");
        refreshBtn.setOnAction(e -> refreshData());

        headerBar.getChildren().addAll(titleLabel, spacer, refreshBtn);

        // Stat Card
        VBox statCard = new VBox(5);
        statCard.getStyleClass().add("stat-card");

        totalRevenueLabel = new Label("$0.00");
        totalRevenueLabel.getStyleClass().add("stat-value");

        Label statSub = new Label("Total Collected Revenue");
        statSub.getStyleClass().add("stat-label");

        statCard.getChildren().addAll(totalRevenueLabel, statSub);

        // TableView setup
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Payment, Integer> idCol = new TableColumn<>("Payment ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Payment, String> patientCol = new TableColumn<>("Patient Name");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));

        TableColumn<Payment, Double> amountCol = new TableColumn<>("Amount Paid ($)");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        amountCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        TableColumn<Payment, Timestamp> dateCol = new TableColumn<>("Payment Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));

        TableColumn<Payment, String> methodCol = new TableColumn<>("Payment Method");
        methodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));

        TableColumn<Payment, String> statusCol = new TableColumn<>("Status");
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
                    badge.getStyleClass().add("badge-paid");
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        tableView.getColumns().addAll(idCol, patientCol, amountCol, dateCol, methodCol, statusCol);

        refreshData();

        root.getChildren().addAll(headerBar, statCard, tableView);
        return root;
    }

    public void refreshData() {
        if (revenueController != null) {
            double total = revenueController.getTotalRevenue();
            if (totalRevenueLabel != null) {
                totalRevenueLabel.setText(String.format("$%.2f", total));
            }
            if (tableView != null) {
                tableView.setItems(FXCollections.observableArrayList(revenueController.getPaymentHistory()));
            }
        }
    }
}
