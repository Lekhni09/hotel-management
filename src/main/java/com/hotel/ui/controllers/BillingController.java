package com.hotel.ui.controllers;

import com.hotel.model.Booking;
import com.hotel.service.BookingService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

/**
 * BillingController — Generate and view invoice for any booking.
 * SOLID: S — only handles billing UI.
 */
public class BillingController {

    private final BookingService bookingService = new BookingService();
    private VBox   invoiceBox;
    private Label  messageLabel;

    public Node getView() {
        VBox view = new VBox(20);
        view.getStyleClass().add("screen");
        view.setPadding(new Insets(28));

        Label title = new Label("Billing");
        title.getStyleClass().add("screen-title");

        messageLabel = new Label("");

        VBox searchCard = buildSearchCard();
        invoiceBox      = new VBox(0);

        view.getChildren().addAll(title, searchCard, messageLabel, invoiceBox);
        return view;
    }

    // -------------------------------------------------------
    // Search by Booking ID
    // -------------------------------------------------------
    private VBox buildSearchCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("form-card");

        Label formTitle = new Label("Generate Invoice");
        formTitle.getStyleClass().add("section-title");

        TextField bookingIdField = new TextField();
        bookingIdField.setPromptText("Enter Booking ID e.g. 1");
        bookingIdField.setMaxWidth(Double.MAX_VALUE);

        Button generateBtn = new Button("📄 Generate Invoice");
        generateBtn.getStyleClass().add("btn-primary");
        generateBtn.setOnAction(e -> handleGenerateInvoice(bookingIdField));

        HBox row = new HBox(10, bookingIdField, generateBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(bookingIdField, Priority.ALWAYS);

        card.getChildren().addAll(formTitle, row);
        return card;
    }

    // -------------------------------------------------------
    // Generate Invoice
    // -------------------------------------------------------
    private void handleGenerateInvoice(TextField bookingIdField) {
        String idText = bookingIdField.getText().trim();

        if (idText.isEmpty()) {
            showMessage("Please enter a Booking ID.", false);
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            showMessage("Booking ID must be a number.", false);
            return;
        }

        Booking booking = bookingService.getBookingById(bookingId);
        if (booking == null) {
            showMessage("No booking found with ID: " + bookingId, false);
            invoiceBox.getChildren().clear();
            return;
        }

        messageLabel.setText("");
        invoiceBox.getChildren().setAll(buildInvoice(booking));
    }

    // -------------------------------------------------------
    // Build Invoice Card
    // -------------------------------------------------------
    private VBox buildInvoice(Booking booking) {
        VBox invoice = new VBox(0);
        invoice.getStyleClass().add("form-card");
        invoice.setMaxWidth(520);

        // Header
        VBox header = new VBox(4);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-padding: 0 0 16 0;");

        Label hotelName = new Label("🏨 HOTEL MANAGEMENT SYSTEM");
        hotelName.setStyle("-fx-text-fill: #f1f5f9; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label invoiceTitle = new Label("INVOICE");
        invoiceTitle.setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 13px; -fx-font-weight: bold; -fx-letter-spacing: 2;");

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #334155; -fx-padding: 8 0 8 0;");

        header.getChildren().addAll(hotelName, invoiceTitle, sep1);

        // Booking details
        VBox details = new VBox(10);
        details.setStyle("-fx-padding: 16 0 16 0;");

        details.getChildren().addAll(
                invoiceRow("Booking ID",    "#" + booking.getId()),
                invoiceRow("Customer",      booking.getCustomerName()),
                invoiceRow("Room",          booking.getRoomNumber() + " (" + booking.getRoomType() + ")"),
                invoiceRow("Check-in",      booking.getCheckIn().toString()),
                invoiceRow("Check-out",     booking.getCheckOut().toString()),
                invoiceRow("Nights",        calculateNights(booking) + " night(s)"),
                invoiceRow("Status",        booking.getStatus().name())
        );

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: #334155;");

        // Total
        HBox totalRow = new HBox();
        totalRow.setStyle("-fx-padding: 16 0 0 0;");
        totalRow.setAlignment(Pos.CENTER_LEFT);

        Label totalLabel = new Label("TOTAL AMOUNT");
        totalLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-font-size: 13px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label totalValue = new Label("₹" + String.format("%.0f", booking.getTotalAmount()));
        totalValue.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold; -fx-font-size: 20px;");

        totalRow.getChildren().addAll(totalLabel, spacer, totalValue);

        // Footer
        Label footer = new Label("Thank you for staying with us!");
        footer.setStyle("-fx-text-fill: #475569; -fx-font-size: 11px; -fx-padding: 16 0 0 0;");
        footer.setMaxWidth(Double.MAX_VALUE);
        footer.setAlignment(Pos.CENTER);

        invoice.getChildren().addAll(header, details, sep2, totalRow, footer);
        return invoice;
    }

    // -------------------------------------------------------
    // Single invoice row (label + value)
    // -------------------------------------------------------
    private HBox invoiceRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        lbl.setMinWidth(130);

        Label val = new Label(value);
        val.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 12px; -fx-font-weight: bold;");

        row.getChildren().addAll(lbl, val);
        return row;
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------
    private long calculateNights(Booking booking) {
        return java.time.temporal.ChronoUnit.DAYS.between(
                booking.getCheckIn(), booking.getCheckOut()
        );
    }

    private void showMessage(String msg, boolean success) {
        messageLabel.setText(msg);
        messageLabel.getStyleClass().removeAll("msg-success", "msg-error");
        messageLabel.getStyleClass().add(success ? "msg-success" : "msg-error");
    }
}