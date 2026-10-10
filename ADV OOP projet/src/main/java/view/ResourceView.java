package view;

import controller.ResourceController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Resource;

public class ResourceView {

    private final ResourceController resourceController;
    private TableView<Resource> tableView;

    public ResourceView() {
        this.resourceController = new ResourceController();
    }

    public VBox getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        // Header Section
        Label titleLabel = new Label("Clinic Inventory & Resource Management");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        // TableView setup
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Resource, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Resource, String> nameCol = new TableColumn<>("Tool / Resource Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("toolName"));

        TableColumn<Resource, Integer> totalCol = new TableColumn<>("Total Qty");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalQty"));

        TableColumn<Resource, Integer> availCol = new TableColumn<>("Available Qty");
        availCol.setCellValueFactory(new PropertyValueFactory<>("availableQty"));

        TableColumn<Resource, String> statusCol = new TableColumn<>("Status");
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
                        case "AVAILABLE":
                            badge.getStyleClass().add("badge-accepted");
                            break;
                        case "LOW_STOCK":
                            badge.getStyleClass().add("badge-pending");
                            break;
                        case "OUT_OF_STOCK":
                            badge.getStyleClass().add("badge-rejected");
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

        tableView.getColumns().addAll(idCol, nameCol, totalCol, availCol, statusCol);
        refreshData();

        // Add Resource Form
        VBox formCard = new VBox(10);
        formCard.getStyleClass().add("card");

        Label formTitle = new Label("Add / Update Inventory Item");
        formTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        nameField.setPromptText("Tool Name");

        TextField totalQtyField = new TextField();
        totalQtyField.setPromptText("Total Quantity");

        TextField availQtyField = new TextField();
        availQtyField.setPromptText("Available Quantity");

        Button addBtn = new Button("Save Resource");
        addBtn.getStyleClass().add("button-primary");

        grid.add(new Label("Tool Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Total Qty:"), 2, 0);
        grid.add(totalQtyField, 3, 0);
        grid.add(new Label("Available Qty:"), 0, 1);
        grid.add(availQtyField, 1, 1);
        grid.add(addBtn, 3, 1);

        addBtn.setOnAction(e -> {
            try {
                String toolName = nameField.getText();
                int totalQty = Integer.parseInt(totalQtyField.getText().trim());
                int availQty = Integer.parseInt(availQtyField.getText().trim());

                if (resourceController.addResource(toolName, totalQty, availQty)) {
                    refreshData();
                    nameField.clear();
                    totalQtyField.clear();
                    availQtyField.clear();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Resource saved successfully!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save resource. Check input values.");
                    alert.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Total and Available quantities must be valid integers.");
                alert.showAndWait();
            }
        });

        formCard.getChildren().addAll(formTitle, grid);
        root.getChildren().addAll(titleLabel, tableView, formCard);
        return root;
    }

    public void refreshData() {
        if (tableView != null) {
            tableView.setItems(FXCollections.observableArrayList(resourceController.getAllResources()));
        }
    }
}
