package com.hotel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.hotel.ui.MainLayout;

/**
 * Main — Entry point for the JavaFX application.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainLayout mainLayout = new MainLayout(primaryStage);
        Scene scene = new Scene(mainLayout.getRoot(), 1100, 680);

        // Load CSS styling
        scene.getStylesheets().add(
                getClass().getResource("/styles/Main.css").toExternalForm()
        );

        primaryStage.setTitle("Hotel Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}