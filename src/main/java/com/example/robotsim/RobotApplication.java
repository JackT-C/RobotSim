package com.example.robotsim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main entry point for the Robot Simulation application.
 * This class extends {@link Application} and sets up the primary stage for the application.
 */
public class RobotApplication extends Application {

    /**
     * The start method is called when the application launches.
     * It sets up the primary stage with the main menu scene.
     *
     * @param primaryStage The primary stage for this application, provided by the JavaFX runtime.
     * @throws Exception If there is an error during the loading of the FXML file or other setup processes.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the main menu FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ApplicationMenu.fxml"));

        // Set up the scene with the specified dimensions
        Scene scene = new Scene(loader.load(), 1920, 1080);

        // Configure the primary stage
        primaryStage.setTitle("Robot Simulation Application"); // Set the window title
        primaryStage.setScene(scene); // Attach the scene to the stage
        primaryStage.show(); // Display the stage
    }

    /**
     * The main method serves as the entry point for the Java application.
     * It launches the JavaFX application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
