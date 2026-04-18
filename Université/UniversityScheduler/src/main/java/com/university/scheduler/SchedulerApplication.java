package com.university.scheduler;

import com.university.scheduler.dao.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SchedulerApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerApplication.class);
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Starting University Scheduler Application");

            // Initialize database
            DatabaseManager.getInstance();
            logger.info("Database initialized");

            // Load login scene
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);

            // Set stylesheet (optional)
            try {
                String css = getClass().getClassLoader().getResource("css/styles.css").toExternalForm();
                if (css != null) {
                    scene.getStylesheets().add(css);
                }
            } catch (Exception e) {
                logger.warn("Could not load application stylesheet");
            }

            primaryStage.setScene(scene);
            primaryStage.setTitle("University Scheduler");
            primaryStage.setWidth(WINDOW_WIDTH);
            primaryStage.setHeight(WINDOW_HEIGHT);
            primaryStage.centerOnScreen();
            primaryStage.setOnCloseRequest(e -> onExit());

            // Try to set application icon
            try {
                primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("icon/app-icon.png")));
            } catch (Exception e) {
                logger.warn("Could not load application icon");
            }

            primaryStage.show();
            logger.info("Application started successfully");

        } catch (IOException e) {
            logger.error("Error starting application", e);
            System.exit(1);
        }
    }

    @Override
    public void stop() throws Exception {
        logger.info("Application stopping");
        super.stop();
        DatabaseManager.getInstance().closeConnection();
    }

    private void onExit() {
        logger.info("Application exiting");
        DatabaseManager.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
