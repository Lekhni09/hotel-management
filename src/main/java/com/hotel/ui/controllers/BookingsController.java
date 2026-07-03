package com.hotel.ui.controllers;

import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.service.BookingService;
import com.hotel.service.RoomService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.List;

/**
 * BookingsController — Create bookings, view all bookings, check-out.
 * SOLID: S — only handles bookings UI.
 */
public class BookingsController {

    private final BookingService bookingService = new BookingService();
    private final RoomService    roomService    = new RoomService();

    private TableView<Booking> table;
    private Label              messageLabel;

    // Form fields — kept as instance variables so we can read them in handler
    private TextField    nameField;
    private TextField    phoneField;
    private TextField    emailField;
    private TextField    idProofField;
    private ComboBox<Room> roomCombo;
    private DatePicker   checkInPicker;
    private DatePicker   checkOutPicker;
    private Label        totalAmountLabel;

    public Node getView() {
        VBox view = new VBox(20);
        view.getStyleClass().add("screen");
        view.setPadding(new Insets(28));

        Label title = new Label("Bookings");
        title.getStyleClass().add("screen-title");

        messageLabel = new Label("");

        VBox formCard    = buildBookingForm();
        Label tableTitle = new Label("All Bookings");
        tableTitle.getStyleClass().add("section-title");

        table = buildBookingsTable();
        refreshTable();

        view.getChildren().addAll(title, formCard, messageLabel, tableTitle, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return view;
    }

    // -------------------------------------------------------
    // Booking Form
    // -------------------------------------------------------
    private VBox buildBookingForm() {
        VBox card = new VBox(14);
        card.getStyleClass().add("form-card");

        Label formTitle = new Label("New Booking");
        formTitle.getStyleClass().add("section-title");

        // Row 1 — Customer details
        nameField    = new TextField();  nameField.setPromptText("Full name");
        phoneField   = new TextField();  phoneField.setPromptText("Phone number");
        emailField   = new TextField();  emailField.setPromptText("Email (optional)");
        idProofField = new TextField();  idProofField.setPromptText("ID proof e.g. Aadhar");

        HBox row1 = new HBox(12,
                labeledField("Customer Name",  nameField),
                labeledField("Phone",          phoneField),
                labeledField("Email",          emailField),
                labeledField("ID Proof",       idProofField));

        // Row 2 — Room + Dates
        roomCombo = new ComboBox<>();
        roomCombo.setMaxWidth(Double.MAX_VALUE);
        roomCombo.setPromptText("Select available room");
        loadAvailableRooms();

        checkInPicker  = new DatePicker();
        checkInPicker.setPromptText("Check-in date");
        checkInPicker.setMaxWidth(Double.MAX_VALUE);

        checkOutPicker = new DatePicker();
        checkOutPicker.setPromptText("Check-out date");
        checkOutPicker.setMaxWidth(Double.MAX_VALUE);

        // Auto calculate total when dates or room changes
        checkInPicker .setOnAction(e -> updateTotalAmount());
        checkOutPicker.setOnAction(e -> updateTotalAmount());
        roomCombo     .setOnAction(e -> updateTotalAmount());

        totalAmountLabel = new Label("Total: ₹0");
        totalAmountLabel.setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-font-size: 14px;");

        HBox row2 = new HBox(12,
                labeledField("Available Room", roomCombo),
                labeledField("Check-in Date",  checkInPicker),
                labeledField("Check-out Date", checkOutPicker));

        // Buttons row
        Button confirmBtn = new Button("✔ Confirm Booking");
        confirmBtn.getStyleClass().add("btn-primary");
        confirmBtn.setOnAction(e -> handleCreateBooking());

        Button clearBtn = new Button("Clear");
        clearBtn.getStyleClass().add("btn-secondary");
        clearBtn.setOnAction(e -> clearForm());

        HBox btnRow = new HBox(10, confirmBtn, clearBtn, totalAmountLabel);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(formTitle, row1, row2, btnRow);
        return card;
    }

    // -------------------------------------------------------
    // Handle Create Booking
    // -------------------------------------------------------
    private void handleCreateBooking() {
        String    name     = nameField.getText().trim();
        String    phone    = phoneField.getText().trim();
        String    email    = emailField.getText().trim();
        String    idProof  = idProofField.getText().trim();
        Room      room     = roomCombo.getValue();
        LocalDate checkIn  = checkInPicker.getValue();
        LocalDate checkOut = checkOutPicker.getValue();

        // Basic UI validation
        if (name.isEmpty() || phone.isEmpty() || room == null
                || checkIn == null || checkOut == null) {
            showMessage("Please fill in all required fields.", false);
            return;
        }

        // Call service — all business logic is there
        String result = bookingService.createBooking(
                name, phone, email, idProof,
                room.getId(), checkIn, checkOut
        );

        if (result.startsWith("SUCCESS")) {
            showMessage(result, true);
            clearForm();
            refreshTable();
        } else {
            showMessage(result, false);
        }
    }

    // -------------------------------------------------------
    // Bookings Table
    // -------------------------------------------------------
    @SuppressWarnings("unchecked")
    private TableView<Booking> buildBookingsTable() {
        TableView<Booking> table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Booking, Number> colId       = new TableColumn<>("ID");
        TableColumn<Booking, String> colCustomer = new TableColumn<>("Customer");
        TableColumn<Booking, String> colRoom     = new TableColumn<>("Room");
        TableColumn<Booking, String> colCheckIn  = new TableColumn<>("Check-in");
        TableColumn<Booking, String> colCheckOut = new TableColumn<>("Check-out");
        TableColumn<Booking, String> colAmount   = new TableColumn<>("Amount");
        TableColumn<Booking, String> colStatus   = new TableColumn<>("Status");
        TableColumn<Booking, Void>   colAction   = new TableColumn<>("Action");

        colId      .setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()));
        colCustomer.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomerName()));
        colRoom    .setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getRoomNumber() + " (" + data.getValue().getRoomType() + ")"));
        colCheckIn .setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckIn().toString()));
        colCheckOut.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckOut().toString()));
        colAmount  .setCellValueFactory(data -> new SimpleStringProperty(
                "₹" + String.format("%.0f", data.getValue().getTotalAmount())));
        colStatus  .setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().name()));

        // Color code status
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

        // Action column — Check-out / Cancel buttons
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button checkOutBtn = new Button("Check-out");
            private final Button cancelBtn   = new Button("Cancel");
            private final HBox   box         = new HBox(6, checkOutBtn, cancelBtn);

            {
                checkOutBtn.getStyleClass().add("btn-primary");
                cancelBtn  .getStyleClass().add("btn-danger");

                checkOutBtn.setOnAction(e -> {
                    Booking b = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Check out Booking #" + b.getId() + "?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(r -> {
                        if (r == ButtonType.YES) {
                            String result = bookingService.checkOut(b.getId());
                            showMessage(result, result.startsWith("SUCCESS"));
                            refreshTable();
                            loadAvailableRooms();
                        }
                    });
                });

                cancelBtn.setOnAction(e -> {
                    Booking b = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Cancel Booking #" + b.getId() + "?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(r -> {
                        if (r == ButtonType.YES) {
                            String result = bookingService.cancelBooking(b.getId());
                            showMessage(result, result.startsWith("SUCCESS"));
                            refreshTable();
                            loadAvailableRooms();
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Booking b = getTableView().getItems().get(getIndex());
                // Only show buttons for ACTIVE bookings
                setGraphic(b.getStatus() == Booking.Status.ACTIVE ? box : null);
            }
        });

        colId.setMaxWidth(45);
        table.getColumns().addAll(colId, colCustomer, colRoom, colCheckIn, colCheckOut, colAmount, colStatus, colAction);
        table.setPlaceholder(new Text("No bookings yet. Create one above!"));
        return table;
    }

    // -------------------------------------------------------
    // Auto-calculate total amount when room/dates change
    // -------------------------------------------------------
    private void updateTotalAmount() {
        Room      room     = roomCombo.getValue();
        LocalDate checkIn  = checkInPicker.getValue();
        LocalDate checkOut = checkOutPicker.getValue();

        if (room != null && checkIn != null && checkOut != null && checkOut.isAfter(checkIn)) {
            double total = bookingService.calculateTotalAmount(room.getId(), checkIn, checkOut);
            totalAmountLabel.setText("Total: ₹" + String.format("%.0f", total));
        } else {
            totalAmountLabel.setText("Total: ₹0");
        }
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------
    private void loadAvailableRooms() {
        List<Room> available = roomService.getAvailableRooms();
        roomCombo.setItems(FXCollections.observableArrayList(available));
    }

    private void refreshTable() {
        table.setItems(FXCollections.observableArrayList(bookingService.getAllBookings()));
    }

    private void clearForm() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        idProofField.clear();
        roomCombo.setValue(null);
        checkInPicker.setValue(null);
        checkOutPicker.setValue(null);
        totalAmountLabel.setText("Total: ₹0");
        loadAvailableRooms();
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