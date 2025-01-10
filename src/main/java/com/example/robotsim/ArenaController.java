package com.example.robotsim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
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
            double x = ThreadLocalRandom.current().nextDouble(50, arenaPane.getWidth() - 50 - Robot.DEFAULT_SIZE);
            double y = ThreadLocalRandom.current().nextDouble(50, arenaPane.getHeight() - 50 - Robot.DEFAULT_SIZE);

            Robot robot = new Robot(name, x, y);
            robot.setOnMouseClicked(event -> robot.changeDirection());

            arenaPane.getChildren().add(robot);
            animateRobot(robot);
            robots.add(robot);
            updateRobotInfo(); // Update robot details initially
        });
    }

    private void animateRobot(Robot robot) {
        Timeline animation = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            robot.updatePosition();

            if (robot.getX() < 0 || robot.getX() + Robot.DEFAULT_SIZE > arenaPane.getWidth()) {
                robot.bounceHorizontally();
            }
            if (robot.getY() < 0 || robot.getY() + Robot.DEFAULT_SIZE > arenaPane.getHeight()) {
                robot.bounceVertically();
            }

            if (isCollidingWithTextArea(robot)) {
                bounceOffTextArea(robot);
            }

            detectCollisions(robot);
            updateRobotInfo(); // Update robot details during movement
        }));

        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    private boolean isCollidingWithTextArea(Robot robot) {
        Bounds textAreaBounds = robotInfoArea.getBoundsInParent();
        Bounds robotBounds = robot.getBoundsInParent();
        return robotBounds.intersects(textAreaBounds);
    }

    private void bounceOffTextArea(Robot robot) {
        Bounds textAreaBounds = robotInfoArea.getBoundsInParent();
        Bounds robotBounds = robot.getBoundsInParent();

        if (robotBounds.getMaxX() > textAreaBounds.getMinX() && robotBounds.getMinX() < textAreaBounds.getMinX()) {
            robot.bounceHorizontally();
            robot.setLayoutX(robot.getLayoutX() - 5);
        } else if (robotBounds.getMinX() < textAreaBounds.getMaxX() && robotBounds.getMaxX() > textAreaBounds.getMaxX()) {
            robot.bounceHorizontally();
            robot.setLayoutX(robot.getLayoutX() + 5);
        } else if (robotBounds.getMaxY() > textAreaBounds.getMinY() && robotBounds.getMinY() < textAreaBounds.getMinY()) {
            robot.bounceVertically();
            robot.setLayoutY(robot.getLayoutY() - 5);
        } else if (robotBounds.getMinY() < textAreaBounds.getMaxY() && robotBounds.getMaxY() > textAreaBounds.getMaxY()) {
            robot.bounceVertically();
            robot.setLayoutY(robot.getLayoutY() + 5);
        }
    }

    private void detectCollisions(Robot currentRobot) {
        for (Robot otherRobot : robots) {
            if (currentRobot == otherRobot) continue;

            Bounds currentBounds = currentRobot.getBoundsInParent();
            Bounds otherBounds = otherRobot.getBoundsInParent();

            if (currentBounds.intersects(otherBounds)) {
                currentRobot.changeDirection();
                otherRobot.changeDirection();

                currentRobot.setLayoutX(currentRobot.getLayoutX() + 5);
                currentRobot.setLayoutY(currentRobot.getLayoutY() + 5);

                otherRobot.setLayoutX(otherRobot.getLayoutX() - 5);
                otherRobot.setLayoutY(otherRobot.getLayoutY() - 5);
            }
        }
    }

    private void updateRobotInfo() {
        robotInfoArea.clear();
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
            Parent menuRoot = FXMLLoader.load(getClass().getResource("ApplicationMenu.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(menuRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeObstacle(ActionEvent event) {
        for (int i = arenaPane.getChildren().size() - 1; i >= 0; i--) {
            if (arenaPane.getChildren().get(i) instanceof Obstacle) {
                arenaPane.getChildren().remove(i);
                obstacleCount--;
                break;
            }
        }
    }

    public void removeRobot(ActionEvent event) {
        for (int i = arenaPane.getChildren().size() - 1; i >= 0; i--) {
            if (arenaPane.getChildren().get(i) instanceof Robot) {
                Robot robotToRemove = (Robot) arenaPane.getChildren().get(i);
                arenaPane.getChildren().remove(robotToRemove);
                robots.remove(robotToRemove);
                robotCount--;
                updateRobotInfo();
                break;
            }
        }
    }

    public void addObstacle(ActionEvent event) {
        List<String> obstacleOptions = Arrays.asList("Obstacle1.png", "Obstacle2.png", "Obstacle3.png");
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
        // Placeholder for file alert logic
    }
}
