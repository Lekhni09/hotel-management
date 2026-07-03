package com.hotel.ui.controllers;

import com.hotel.model.Room;
import com.hotel.service.RoomService;
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
 * RoomsController — View and manage all rooms.
 * SOLID: S — only handles rooms UI.
 */
public class RoomsController {

    private final RoomService roomService = new RoomService();
    private TableView<Room>   table;
    private Label             messageLabel;

    public Node getView() {
        VBox view = new VBox(20);
        view.getStyleClass().add("screen");
        view.setPadding(new Insets(28));

        Label title = new Label("Rooms");
        title.getStyleClass().add("screen-title");

        messageLabel = new Label("");
        messageLabel.getStyleClass().add("msg-success");

        VBox formCard = buildAddRoomForm();

        Label tableTitle = new Label("All Rooms");
        tableTitle.getStyleClass().add("section-title");

        table = buildRoomsTable();
        refreshTable();

        view.getChildren().addAll(title, formCard, messageLabel, tableTitle, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return view;
    }

    // -------------------------------------------------------
    // Add Room Form
    // -------------------------------------------------------
    private VBox buildAddRoomForm() {
        VBox card = new VBox(14);
        card.getStyleClass().add("form-card");

        Label formTitle = new Label("Add New Room");
        formTitle.getStyleClass().add("section-title");

        TextField roomNumberField = new TextField();
        roomNumberField.setPromptText("Room number e.g. 104");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("SINGLE", "DOUBLE", "SUITE");
        typeCombo.setPromptText("Room type");
        typeCombo.setMaxWidth(Double.MAX_VALUE);

        TextField priceField = new TextField();
        priceField.setPromptText("Price per night e.g. 2500");

        HBox row1 = new HBox(12,
                labeledField("Room Number", roomNumberField),
                labeledField("Room Type", typeCombo),
                labeledField("Price / Night (₹)", priceField));
        row1.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(roomNumberField, Priority.ALWAYS);
        HBox.setHgrow(typeCombo,       Priority.ALWAYS);
        HBox.setHgrow(priceField,      Priority.ALWAYS);

        Button addBtn = new Button("+ Add Room");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> handleAddRoom(roomNumberField, typeCombo, priceField));

        card.getChildren().addAll(formTitle, row1, addBtn);
        return card;
    }

    // -------------------------------------------------------
    // Handle Add Room
    // -------------------------------------------------------
    private void handleAddRoom(TextField roomNumberField, ComboBox<String> typeCombo, TextField priceField) {
        String roomNumber = roomNumberField.getText().trim();
        String type       = typeCombo.getValue();
        String priceText  = priceField.getText().trim();

        if (roomNumber.isEmpty() || type == null || priceText.isEmpty()) {
            showMessage("Please fill in all fields.", false);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException ex) {
            showMessage("Price must be a valid number.", false);
            return;
        }

        boolean success = roomService.addRoom(roomNumber, type, price);
        if (success) {
            showMessage("Room " + roomNumber + " added successfully!", true);
            roomNumberField.clear();
            typeCombo.setValue(null);
            priceField.clear();
            refreshTable();
        } else {
            showMessage("Failed to add room. Check the details.", false);
        }
    }

    // -------------------------------------------------------
    // Rooms Table
    // -------------------------------------------------------
    @SuppressWarnings("unchecked")
    private TableView<Room> buildRoomsTable() {
        TableView<Room> table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Room, Number> colId     = new TableColumn<>("ID");
        TableColumn<Room, String> colNumber = new TableColumn<>("Room No.");
        TableColumn<Room, String> colType   = new TableColumn<>("Type");
        TableColumn<Room, String> colPrice  = new TableColumn<>("Price / Night");
        TableColumn<Room, String> colStatus = new TableColumn<>("Status");
        TableColumn<Room, Void>   colAction = new TableColumn<>("Action");

        colId    .setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()));
        colNumber.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoomNumber()));
        colType  .setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType().name()));
        colPrice .setCellValueFactory(data -> new SimpleStringProperty("₹" + String.format("%.0f", data.getValue().getPricePerNight())));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().isAvailable() ? "Available" : "Booked"
        ));

        // Color code status
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(item.equals("Available")
                        ? "-fx-text-fill: #22c55e; -fx-font-weight: bold;"
                        : "-fx-text-fill: #ef4444; -fx-font-weight: bold;");
            }
        });

        // Delete button column
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            {
                deleteBtn.getStyleClass().add("btn-danger");
                deleteBtn.setOnAction(e -> {
                    Room room = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Delete Room " + room.getRoomNumber() + "?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            roomService.deleteRoom(room.getId());
                            showMessage("Room " + room.getRoomNumber() + " deleted.", true);
                            refreshTable();
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        colId.setMaxWidth(50);
        table.getColumns().addAll(colId, colNumber, colType, colPrice, colStatus, colAction);
        table.setPlaceholder(new Text("No rooms found."));
        return table;
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------
    private void refreshTable() {
        List<Room> rooms = roomService.getAllRooms();
        table.setItems(FXCollections.observableArrayList(rooms));
    }

    private void showMessage(String msg, boolean success) {
        messageLabel.setText(msg);
        messageLabel.getStyleClass().removeAll("msg-success", "msg-error");
        messageLabel.getStyleClass().add(success ? "msg-success" : "msg-error");
    }

    private VBox labeledField(String labelText, Control field) {
        VBox box = new VBox(5);
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("form-label");
        field.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);
        box.getChildren().addAll(lbl, field);
        return box;
    }
}