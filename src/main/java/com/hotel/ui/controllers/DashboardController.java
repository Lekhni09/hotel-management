package com.hotel.ui.controllers;

import com.hotel.model.Booking;
import com.hotel.service.BookingService;
import com.hotel.service.RoomService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.List;

/**
 * DashboardController — shows stats cards + recent bookings table.
 *
 * SOLID: S — only responsible for dashboard UI.
 */
public class DashboardController {

    private final RoomService    roomService    = new RoomService();
    private final BookingService bookingService = new BookingService();

    public Node getView() {
        VBox view = new VBox(24);
        view.getStyleClass().add("screen");
        view.setPadding(new Insets(28));

        // Title
        Label title = new Label("Dashboard");
        title.getStyleClass().add("screen-title");

        // Stats row
        HBox statsRow = buildStatsRow();

        // Recent bookings
        Label tableTitle = new Label("Recent Bookings");
        tableTitle.getStyleClass().add("section-title");

        TableView<Booking> table = buildBookingsTable();

        view.getChildren().addAll(title, statsRow, tableTitle, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return view;
    }

    // -------------------------------------------------------
    // Stats cards row
    // -------------------------------------------------------
    private HBox buildStatsRow() {
        long totalRooms     = roomService.getAllRooms().size();
        long availableRooms = roomService.getAvailableRoomCount();
        long occupiedRooms  = roomService.getOccupiedRoomCount();
        double revenue      = bookingService.getTotalRevenue();
        int totalBookings   = bookingService.getTotalBookingsCount();

        HBox row = new HBox(16);
        row.getChildren().addAll(
                statCard("Total Rooms",      String.valueOf(totalRooms),     "stat-card"),
                statCard("Available",        String.valueOf(availableRooms), "stat-card-green"),
                statCard("Occupied",         String.valueOf(occupiedRooms),  "stat-card-amber"),
                statCard("Total Bookings",   String.valueOf(totalBookings),  "stat-card"),
                statCard("Total Revenue",    "₹" + String.format("%.0f", revenue), "stat-card-blue")
        );
        return row;
    }

    // -------------------------------------------------------
    // Individual stat card
    // -------------------------------------------------------
    private VBox statCard(String label, String value, String styleClass) {
        VBox card = new VBox(6);
        card.getStyleClass().addAll("stat-card", styleClass);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-label");

        Label val = new Label(value);
        val.getStyleClass().add("stat-value");

        card.getChildren().addAll(lbl, val);
        return card;
    }

    // -------------------------------------------------------
    // Recent bookings table
    // -------------------------------------------------------
    @SuppressWarnings("unchecked")
    private TableView<Booking> buildBookingsTable() {
        TableView<Booking> table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Booking, Integer> colId       = new TableColumn<>("Booking ID");
        TableColumn<Booking, String>  colCustomer = new TableColumn<>("Customer");
        TableColumn<Booking, String>  colRoom     = new TableColumn<>("Room");
        TableColumn<Booking, String>  colCheckIn  = new TableColumn<>("Check-in");
        TableColumn<Booking, String>  colCheckOut = new TableColumn<>("Check-out");
        TableColumn<Booking, String>  colAmount   = new TableColumn<>("Amount");
        TableColumn<Booking, String>  colStatus   = new TableColumn<>("Status");

        colId      .setCellValueFactory(new PropertyValueFactory<>("id"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colRoom    .setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRoomNumber() + " (" + data.getValue().getRoomType() + ")"));
        colCheckIn .setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCheckIn().toString()));
        colCheckOut.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCheckOut().toString()));
        colAmount  .setCellValueFactory(data ->
                new SimpleStringProperty("₹" + String.format("%.0f", data.getValue().getTotalAmount())));
        colStatus  .setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus().name()));

        // Color-code status column
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "ACTIVE"    -> setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
                    case "COMPLETED" -> setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: bold;");
                    case "CANCELLED" -> setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                }
            }
        });

        table.getColumns().addAll(colId, colCustomer, colRoom, colCheckIn, colCheckOut, colAmount, colStatus);

        List<Booking> bookings = bookingService.getAllBookings();
        table.setItems(FXCollections.observableArrayList(bookings));

        if (bookings.isEmpty()) {
            table.setPlaceholder(new Text("No bookings yet."));
        }

        return table;
    }
}