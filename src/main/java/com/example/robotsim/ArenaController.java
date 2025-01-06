package com.example.robotsim;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ArenaController {

    private int robotCount = 0;
    private int obstacleCount = 0;

    @FXML
    private Pane arenaPane;

    @FXML
    private Button addRobotButton;

    @FXML
    public void initialize() {
        // Initialization logic for the arena
    }

    @FXML
    private void addRobot() {
        Robot robot = new Robot(200, 200); // Position can be dynamic
        arenaPane.getChildren().add(robot);
        robotCount++;
        animateRobot(robot);
    }

    private void animateRobot(Robot robot) {
        // Add animation logic for the robot
    }

    public void goBackToMenu(ActionEvent event) {
        try {
            // Load the main menu FXML file
            Parent menuRoot = FXMLLoader.load(getClass().getResource("ApplicationMenu.fxml"));

            // Get the current stage from the event's source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene with the main menu
            stage.setScene(new Scene(menuRoot));
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., show an error dialog)
        }
    }

    public void removeObstacle(ActionEvent event) {
        // Remove the last obstacle added to the arena, if any
        for (int i = arenaPane.getChildren().size() - 1; i >= 0; i--) {
            if (arenaPane.getChildren().get(i) instanceof Obstacle) {
                arenaPane.getChildren().remove(i);
                obstacleCount--;
                break;
            }
        }
    }

    public void removeRobot(ActionEvent event) {
        // Remove the last robot added to the arena, if any
        for (int i = arenaPane.getChildren().size() - 1; i >= 0; i--) {
            if (arenaPane.getChildren().get(i) instanceof Robot) {
                arenaPane.getChildren().remove(i);
                robotCount--;
                break;
            }
        }
    }

    public void addObstacle(ActionEvent event) {
        // List of obstacle image names
        List<String> obstacleOptions = Arrays.asList(
                "Obstacle1.png",
                "Obstacle2.png",
                "Obstacle3.png"
        );

        // Show a dialog to select an obstacle image
        ChoiceDialog<String> dialog = new ChoiceDialog<>(obstacleOptions.get(0), obstacleOptions);
        dialog.setTitle("Select Obstacle");
        dialog.setHeaderText("Choose an obstacle to add to the arena:");
        dialog.setContentText("Obstacles:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(obstacleImage -> {
            Obstacle obstacle = new Obstacle(100 + obstacleCount * 50, 100 + obstacleCount * 50, obstacleImage);
            arenaPane.getChildren().add(obstacle);
            obstacleCount++;
        });
    }

    public void fileAlert(ActionEvent event) {
    }
}
