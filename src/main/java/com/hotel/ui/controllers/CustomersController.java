package com.hotel.ui.controllers;

import com.hotel.model.Customer;
import com.hotel.service.BookingService;
import com.hotel.dao.CustomerDAO;
import com.hotel.dao.impl.CustomerDAOImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.List;

/**
 * CustomersController — View all customers, search by phone.
 * SOLID: S — only handles customers UI.
 */
public class CustomersController {

    private final CustomerDAO    customerDAO    = new CustomerDAOImpl();
    private final BookingService bookingService = new BookingService();

    private TableView<Customer> table;
    private Label               messageLabel;
    private TextField           searchField;

    public Node getView() {
        VBox view = new VBox(20);
        view.getStyleClass().add("screen");
        view.setPadding(new Insets(28));

        Label title = new Label("Customers");
        title.getStyleClass().add("screen-title");

        messageLabel = new Label("");

        VBox searchCard = buildSearchBar();

        Label tableTitle = new Label("All Customers");
        tableTitle.getStyleClass().add("section-title");

        table = buildCustomersTable();
        refreshTable();

        view.getChildren().addAll(title, searchCard, messageLabel, tableTitle, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return view;
    }

    // -------------------------------------------------------
    // Search Bar
    // -------------------------------------------------------
    private VBox buildSearchBar() {
        VBox card = new VBox(14);
        card.getStyleClass().add("form-card");

        Label formTitle = new Label("Search Customer");
        formTitle.getStyleClass().add("section-title");

        searchField = new TextField();
        searchField.setPromptText("Enter phone number to search...");
        searchField.setMaxWidth(Double.MAX_VALUE);

        Button searchBtn = new Button("🔍 Search");
        searchBtn.getStyleClass().add("btn-primary");
        searchBtn.setOnAction(e -> handleSearch());

        Button showAllBtn = new Button("Show All");
        showAllBtn.getStyleClass().add("btn-secondary");
        showAllBtn.setOnAction(e -> {
            searchField.clear();
            refreshTable();
            messageLabel.setText("");
        });

        HBox row = new HBox(10, searchField, searchBtn, showAllBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        card.getChildren().addAll(formTitle, row);
        return card;
    }

    // -------------------------------------------------------
    // Handle Search
    // -------------------------------------------------------
    private void handleSearch() {
        String phone = searchField.getText().trim();
        if (phone.isEmpty()) {
            showMessage("Please enter a phone number to search.", false);
            return;
        }

        Customer customer = customerDAO.getCustomerByPhone(phone);
        if (customer != null) {
            table.setItems(FXCollections.observableArrayList(customer));
            showMessage("Customer found!", true);
        } else {
            table.setItems(FXCollections.observableArrayList());
            showMessage("No customer found with phone: " + phone, false);
        }
    }

    // -------------------------------------------------------
    // Customers Table
    // -------------------------------------------------------
    @SuppressWarnings("unchecked")
    private TableView<Customer> buildCustomersTable() {
        TableView<Customer> table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Customer, Number> colId      = new TableColumn<>("ID");
        TableColumn<Customer, String> colName    = new TableColumn<>("Name");
        TableColumn<Customer, String> colPhone   = new TableColumn<>("Phone");
        TableColumn<Customer, String> colEmail   = new TableColumn<>("Email");
        TableColumn<Customer, String> colIdProof = new TableColumn<>("ID Proof");
        TableColumn<Customer, String> colBookings= new TableColumn<>("Total Bookings");

        colId     .setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()));
        colName   .setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colPhone  .setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        colEmail  .setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getEmail() != null ? data.getValue().getEmail() : "—"));
        colIdProof.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getIdProof() != null ? data.getValue().getIdProof() : "—"));
        colBookings.setCellValueFactory(data -> new SimpleStringProperty(
                String.valueOf(bookingService.getBookingsByCustomer(data.getValue().getId()).size())
        ));

        colId.setMaxWidth(50);
        table.getColumns().addAll(colId, colName, colPhone, colEmail, colIdProof, colBookings);
        table.setPlaceholder(new Text("No customers found."));
        return table;
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------
    private void refreshTable() {
        List<Customer> customers = customerDAO.getAllCustomers();
        table.setItems(FXCollections.observableArrayList(customers));
    }

    private void showMessage(String msg, boolean success) {
        messageLabel.setText(msg);
        messageLabel.getStyleClass().removeAll("msg-success", "msg-error");
        messageLabel.getStyleClass().add(success ? "msg-success" : "msg-error");
    }
}