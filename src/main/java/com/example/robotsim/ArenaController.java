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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class ArenaController {

    private Random random = new Random(); // For generating random positions
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
        // Generate random positions within the bounds of the arenaPane
        double x = random.nextDouble() * (arenaPane.getWidth());
        double y = random.nextDouble() * (arenaPane.getHeight());

        Robot robot = new Robot(x, y); // Robot positioned randomly
        arenaPane.getChildren().add(robot);
        robotCount++;
        animateRobot(robot);
    }

    private void animateRobot(Robot robot) {
        Timeline animation = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            // Move the robot in a random direction
            double dx = random.nextDouble() * 80 - 40; // Random delta x (-2 to 2)
            double dy = random.nextDouble() * 80 - 40; // Random delta y (-2 to 2)

            double newX = robot.getX() + dx;
            double newY = robot.getY() + dy;

            // Check for collisions with arena boundaries
            if (newX < 0 || newX + Robot.DEFAULT_SIZE > arenaPane.getWidth()) {
                dx = -dx;
            }
            if (newY < 0 || newY + Robot.DEFAULT_SIZE > arenaPane.getHeight()) {
                dy = -dy;
            }

            // Update position
            robot.setX(robot.getX() + dx);
            robot.setY(robot.getY() + dy);

            // Check for collisions with other objects
            detectCollisions(robot);
        }));

        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    private void detectCollisions(Robot robot) {
        for (var node : arenaPane.getChildren()) {
            if (node instanceof Obstacle || node instanceof Robot && node != robot) {
                if (robot.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    // Handle collision: bounce the robot back
                    double dx = random.nextDouble() * 40 - 20;
                    double dy = random.nextDouble() * 40 - 20;
                    robot.setX(robot.getX() - dx);
                    robot.setY(robot.getY() - dy);
                }
            }
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
