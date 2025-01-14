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
    private void addRobot() {
        // Create a custom dialog
        Dialog<Pair<String, Pair<String, Double>>> dialog = new Dialog<>();
        dialog.setTitle("Create Robot");
        dialog.setHeaderText("Enter a name, choose a robot type and size for your robot:");

        // Set up the dialog's content
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label typeLabel = new Label("Robot Type:");

        // Robot Type dropdown
        ChoiceBox<String> typeChoiceBox = new ChoiceBox<>();
        typeChoiceBox.getItems().addAll("Default Robot", "Sensor Robot");
        typeChoiceBox.setValue("Default Robot"); // Default selection

        Label sizeLabel = new Label("Size:");
        Slider sizeSlider = new Slider(30, 150, 50); // Slider for size selection
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setShowTickMarks(true);

        // Display current size value
        Label sizeValueLabel = new Label("50");
        sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            sizeValueLabel.setText(String.format("%.0f", newValue));
        });

        // Layout for the dialog content
        VBox content = new VBox(10);
        content.getChildren().addAll(nameLabel, nameField, typeLabel, typeChoiceBox, sizeLabel, sizeSlider, sizeValueLabel);

        dialog.getDialogPane().setContent(content);

        // Add OK and Cancel buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Convert the result to a name, robot type, and size when the OK button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                String name = nameField.getText();
                String robotType = typeChoiceBox.getValue();
                double size = sizeSlider.getValue();
                return new Pair<>(name, new Pair<>(robotType, size));
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<Pair<String, Pair<String, Double>>> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            String name = pair.getKey();
            String robotType = pair.getValue().getKey();
            double size = pair.getValue().getValue();

            // Ensure robot is placed within valid bounds
            double x = ThreadLocalRandom.current().nextDouble(50, arenaPane.getWidth() - size);
            double y = ThreadLocalRandom.current().nextDouble(50, arenaPane.getHeight() - size);

            // Create the robot based on the selected type
            Robot robot;
            if (robotType.equals("Sensor Robot")) {
                robot = new SensorRobot(name, x, y, size);
            } else {
                robot = new Robot(name, x, y, size);
            }

            // Set behavior for interaction (can be customized further)
            robot.setOnMouseClicked(event -> robot.changeDirection());

            // Add to arenaPane
            arenaPane.getChildren().add(robot);

            // Start animation
            animateRobot(robot);
            robots.add(robot);
            updateRobotInfo();
        });
    }



    private void animateRobot(Robot robot) {
        Timeline animation = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            robot.updatePosition();

            if (robot.getX() < 0 || robot.getX() + robot.getRobotWidth() >= arenaPane.getWidth()) {
                robot.bounceHorizontally();
            }
            if (robot.getY() < 0 || robot.getY() + robot.getRobotHeight() >= arenaPane.getHeight()) {
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




    public void fileAlert(ActionEvent event) {
        // Placeholder for file alert logic
    }
}
