package com.example.robotsim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RobotApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ApplicationMenu.fxml"));

        // Set up the scene
        Scene scene = new Scene(loader.load(), 1920, 1080);

        // Configure stage
        primaryStage.setTitle("Robot Simulation Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
