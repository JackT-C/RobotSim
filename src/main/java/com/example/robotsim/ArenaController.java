package com.example.robotsim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

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
    private void addRobot(ActionEvent event) {
        // List of robot types
        List<String> robotOptions = Arrays.asList("Default Robot", "Sensor Robot", "User Controlled", "Predator Robot");

        // Create a dialog for adding a robot
        Dialog<Pair<String, Double>> dialog = new Dialog<>();
        dialog.setTitle("Add Robot");
        dialog.setHeaderText("Customize your robot:");

        // Robot Name
        Label nameLabel = new Label("Robot Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter robot name");

        // Robot Type
        Label typeLabel = new Label("Robot Type:");
        ChoiceBox<String> typeChoiceBox = new ChoiceBox<>();
        typeChoiceBox.getItems().addAll(robotOptions);
        typeChoiceBox.setValue(robotOptions.get(0)); // Default selection

        // Robot Size
        Label sizeLabel = new Label("Robot Size:");
        Slider sizeSlider = new Slider(30, 150, 50); // Min size: 30, Max size: 150, Default: 50
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setMajorTickUnit(20);
        sizeSlider.setBlockIncrement(5);

        // Layout for the dialog content
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameField, 1, 0);
        gridPane.add(typeLabel, 0, 1);
        gridPane.add(typeChoiceBox, 1, 1);
        gridPane.add(sizeLabel, 0, 2);
        gridPane.add(sizeSlider, 1, 2);

        dialog.getDialogPane().setContent(gridPane);

        // Add OK and Cancel buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Convert the result to a pair of robot type and size, including name
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    name = "Robot " + (robotCount + 1); // Default name if user leaves it blank
                }
                String type = typeChoiceBox.getValue();
                Double size = sizeSlider.getValue();
                return new Pair<>(name + "|" + type, size);
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<Pair<String, Double>> result = dialog.showAndWait();
        result.ifPresent(robotData -> {
            String[] parts = robotData.getKey().split("\\|");
            String name = parts[0];
            String selectedType = parts[1];
            double size = robotData.getValue();

            Robot robot = null;

            // Create the specific robot based on the selected type
            switch (selectedType) {
                case "Default Robot":
                    robot = new DefaultRobot(name, 100 + robotCount * 50, 100 + robotCount * 50, size);
                    break;
                case "Sensor Robot":
                    robot = new SensorRobot(name, 100 + robotCount * 50, 100 + robotCount * 50, size);
                    break;
                case "Predator Robot":
                    robot = new PredatorRobot(name, 100 + robotCount * 50, 100 + robotCount * 50, size);
                    break;
                case "User Controlled":
                    robot = new UserControlledRobot(name, 100 + robotCount * 50, 100 + robotCount * 50, size);
                    break;

            }

            if (robot != null) {
                // Add robot to the arena and the list of robots
                arenaPane.getChildren().add(robot);
                robots.add(robot);

                animateRobot(robot);

                robotCount++;
                updateRobotInfo(); // Update TextArea with the new robot
            }
        });
    }



    private void animateRobot(Robot robot) {
        Timeline animation = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            robot.updatePosition();

            // Handle wall collisions
            if (robot.getX() <= 0 || robot.getX() + robot.getRobotWidth() >= arenaPane.getWidth()) {
                robot.bounceHorizontally();
            }
            if (robot.getY() <= 0 || robot.getY() + robot.getRobotHeight() >= arenaPane.getHeight()) {
                robot.bounceVertically();
            }

            // Handle text area collisions
            if (isCollidingWithTextArea(robot)) {
                bounceOffTextArea(robot);
            }

            // Detect collisions with obstacles
            detectObstacleCollisions(robot);

            // Update robot details in the text area
            updateRobotInfo();
        }));

        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }


    private void detectObstacleCollisions(Robot robot) {
        if (robot instanceof SensorRobot) {
            avoidObstacles((SensorRobot) robot);
        } else {
            handleNormalRobotObstacleInteraction(robot);
        }
    }

    private void avoidObstacles(SensorRobot sensorRobot) {
        for (var node : arenaPane.getChildren()) {
            if (node instanceof Obstacle) {
                Obstacle obstacle = (Obstacle) node;
                if (isCollidingWithObstacle(sensorRobot, obstacle)) {
                    sensorRobot.avoidObstacle(obstacle);
                }
            }
        }
    }

    private void handleNormalRobotObstacleInteraction(Robot robot) {
        for (var node : arenaPane.getChildren()) {
            if (node instanceof Obstacle) {
                Obstacle obstacle = (Obstacle) node;
                if (isCollidingWithObstacle(robot, obstacle)) {
                    obstacle.handleCollision(robot); // Polymorphic behavior handles specific logic
                }
            }
        }
    }


    private boolean isCollidingWithObstacle(Robot robot, Obstacle obstacle) {
        Bounds robotBounds = robot.getBoundsInParent();
        Bounds obstacleBounds = obstacle.getBoundsInParent();
        return robotBounds.intersects(obstacleBounds);
    }

    private void handleObstacleCollision(Robot robot, Obstacle obstacle) {
        Bounds obstacleBounds = obstacle.getBoundsInParent();
        Bounds robotBounds = robot.getBoundsInParent();

        // Bounce robot in the opposite direction
        if (robotBounds.getMaxX() > obstacleBounds.getMinX() && robotBounds.getMinX() < obstacleBounds.getMinX()) {
            robot.bounceHorizontally();
            robot.setLayoutX(robot.getLayoutX() - 5); // Push back slightly
        } else if (robotBounds.getMinX() < obstacleBounds.getMaxX() && robotBounds.getMaxX() > obstacleBounds.getMaxX()) {
            robot.bounceHorizontally();
            robot.setLayoutX(robot.getLayoutX() + 5); // Push forward slightly
        }

        if (robotBounds.getMaxY() > obstacleBounds.getMinY() && robotBounds.getMinY() < obstacleBounds.getMinY()) {
            robot.bounceVertically();
            robot.setLayoutY(robot.getLayoutY() - 5); // Push back slightly
        } else if (robotBounds.getMinY() < obstacleBounds.getMaxY() && robotBounds.getMaxY() > obstacleBounds.getMaxY()) {
            robot.bounceVertically();
            robot.setLayoutY(robot.getLayoutY() + 5); // Push forward slightly
        }
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


    private void updateRobotInfo() {
        robotInfoArea.clear();
        for (Robot robot : robots) {
            String info = String.format("Name: %s, X: %.2f, Y: %.2f, Size: %.2f%n",
                    robot.getName(),
                    robot.getLayoutX(),
                    robot.getLayoutY(),
                    robot.getRobotWidth());
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
        // List of obstacle types
        List<String> obstacleOptions = Arrays.asList("Lamp", "Rock", "Lake");

        // Create a dialog for adding an obstacle
        Dialog<Pair<String, Double>> dialog = new Dialog<>();
        dialog.setTitle("Add Obstacle");
        dialog.setHeaderText("Choose an obstacle type and size:");

        // Set the dialog content
        Label typeLabel = new Label("Obstacle Type:");
        ChoiceBox<String> typeChoiceBox = new ChoiceBox<>();
        typeChoiceBox.getItems().addAll(obstacleOptions);
        typeChoiceBox.setValue(obstacleOptions.get(0)); // Default selection

        Label sizeLabel = new Label("Obstacle Size:");
        Slider sizeSlider = new Slider(50, 200, 100); // Min size: 50, Max size: 200, Default: 100
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setMajorTickUnit(20);
        sizeSlider.setBlockIncrement(5);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(typeLabel, 0, 0);
        gridPane.add(typeChoiceBox, 1, 0);
        gridPane.add(sizeLabel, 0, 1);
        gridPane.add(sizeSlider, 1, 1);

        dialog.getDialogPane().setContent(gridPane);

        // Add OK and Cancel buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Convert the result to a pair of obstacle type and size
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Pair<>(typeChoiceBox.getValue(), sizeSlider.getValue());
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<Pair<String, Double>> result = dialog.showAndWait();
        result.ifPresent(obstacleData -> {
            String selectedType = obstacleData.getKey();
            double size = obstacleData.getValue();

            Obstacle obstacle = null;

            // Create the specific obstacle based on the selected type
            switch (selectedType) {
                case "Lamp":
                    obstacle = new LampObstacle(100 + obstacleCount * 50, 100 + obstacleCount * 50, size);
                    break;
                case "Rock":
                    obstacle = new RockObstacle(100 + obstacleCount * 50, 100 + obstacleCount * 50, size);
                    break;
                case "Lake":
                    obstacle = new LakeObstacle(100 + obstacleCount * 50, 100 + obstacleCount * 50, size);
                    break;
            }

            if (obstacle != null) {
                arenaPane.getChildren().add(obstacle);
                obstacleCount++;
            }
        });
    }




    @FXML
    public void fileAlert(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("File Operation");
        alert.setHeaderText("Would you like to save or load the arena?");
        alert.setContentText("Choose an option:");

        ButtonType saveButton = new ButtonType("Save");
        ButtonType loadButton = new ButtonType("Load");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(saveButton, loadButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == saveButton) {
                FileHandler.saveArena(arenaPane, robots, robotCount, obstacleCount);
            } else if (result.get() == loadButton) {
                FileHandler.loadArena(arenaPane, robots);
            }
        }
    }

}
