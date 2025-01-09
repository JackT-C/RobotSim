package com.example.robotsim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaController {

    private int robotCount = 0;
    // List to track all robots in the arena
    private final List<Robot> robots = new ArrayList<>();
    private int obstacleCount = 0;

    @FXML
    private TextArea robotInfoArea;

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
        // Prompt user to enter robot name
        TextInputDialog dialog = new TextInputDialog("Robot");
        dialog.setTitle("Name Your Robot");
        dialog.setHeaderText("Enter a name for the new robot:");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            // Ensure robot is placed within valid bounds
            double x = ThreadLocalRandom.current().nextDouble(50, arenaPane.getWidth() - 50 - Robot.DEFAULT_SIZE);
            double y = ThreadLocalRandom.current().nextDouble(50, arenaPane.getHeight() - 50 - Robot.DEFAULT_SIZE);

            // Create the robot
            Robot robot = new Robot(name, x, y);

            // Set behavior for interaction
            robot.setOnMouseClicked(event -> robot.changeDirection()); // Change direction on click

            // Add to arenaPane
            arenaPane.getChildren().add(robot);

            // Start animation
            animateRobot(robot);
            robots.add(robot);
            updateRobotInfo(); // Update robot details after adding
        });
    }


    private void animateRobot(Robot robot) {
        Timeline animation = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            // Update robot's position
            robot.updatePosition();

            // Check for collisions with arena boundaries
            if (robot.getLayoutX() < 0 || robot.getLayoutX() + Robot.DEFAULT_SIZE > arenaPane.getWidth()) {
                robot.bounceHorizontally();
            }
            if (robot.getLayoutY() < 0 || robot.getLayoutY() + Robot.DEFAULT_SIZE > arenaPane.getHeight()) {
                robot.bounceVertically();
            }

            // Update robot information
            updateRobotInfo();
        }));

        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    private void updateRobotInfo() {
        // Clear the current text
        robotInfoArea.clear();

        // Add details for each robot
        for (Robot robot : robots) {
            String info = String.format("Name: %s, X: %.2f, Y: %.2f\n",
                    robot.getName(),
                    robot.getLayoutX(),
                    robot.getLayoutY());
            robotInfoArea.appendText(info);
        }
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
