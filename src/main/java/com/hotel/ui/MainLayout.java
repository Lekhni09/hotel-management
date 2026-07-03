package com.hotel.ui;

import com.hotel.ui.controllers.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * MainLayout — builds the sidebar + content area.
 * Handles navigation between screens.
 *
 * SOLID: S — only responsible for layout and navigation.
 */
public class MainLayout {

    private final BorderPane root;
    private final StackPane  contentArea;
    private Button           activeButton;

    public MainLayout(Stage stage) {
        root        = new BorderPane();
        contentArea = new StackPane();

        root.setLeft(buildSidebar());
        root.setCenter(contentArea);

        // Show dashboard by default
        showDashboard();
    }

    // -------------------------------------------------------
    // Build left sidebar
    // -------------------------------------------------------
    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(200);

        // App title
        Label title = new Label("🏨 Hotel MS");
        title.getStyleClass().add("sidebar-title");
        VBox.setMargin(title, new Insets(24, 16, 24, 16));

        // Nav buttons
        Button btnDashboard = navButton("📊  Dashboard");
        Button btnRooms     = navButton("🚪  Rooms");
        Button btnBookings  = navButton("📅  Bookings");
        Button btnCustomers = navButton("👥  Customers");
        Button btnBilling   = navButton("🧾  Billing");

        // Click handlers
        btnDashboard.setOnAction(e -> { setActive(btnDashboard); showDashboard(); });
        btnRooms    .setOnAction(e -> { setActive(btnRooms);     showRooms(); });
        btnBookings .setOnAction(e -> { setActive(btnBookings);  showBookings(); });
        btnCustomers.setOnAction(e -> { setActive(btnCustomers); showCustomers(); });
        btnBilling  .setOnAction(e -> { setActive(btnBilling);   showBilling(); });

        // Set dashboard active by default
        setActive(btnDashboard);

        sidebar.getChildren().addAll(title, btnDashboard, btnRooms, btnBookings, btnCustomers, btnBilling);
        return sidebar;
    }

    // -------------------------------------------------------
    // Navigation helpers
    // -------------------------------------------------------
    private void showScreen(Node screen) {
        contentArea.getChildren().setAll(screen);
    }

    private void showDashboard() {
        showScreen(new DashboardController().getView());
    }

    private void showRooms() {
        showScreen(new RoomsController().getView());
    }

    private void showBookings() {
        showScreen(new BookingsController().getView());
    }

    private void showCustomers() {
        showScreen(new CustomersController().getView());
    }

    private void showBilling() {
        showScreen(new BillingController().getView());
    }

    // -------------------------------------------------------
    // Creates a styled sidebar nav button
    // -------------------------------------------------------
    private Button navButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(btn, new Insets(2, 8, 2, 8));
        return btn;
    }

    // -------------------------------------------------------
    // Sets the active (highlighted) nav button
    // -------------------------------------------------------
    private void setActive(Button btn) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("nav-button-active");
        }
        activeButton = btn;
        btn.getStyleClass().add("nav-button-active");
    }

    public BorderPane getRoot() {
        return root;
    }
}