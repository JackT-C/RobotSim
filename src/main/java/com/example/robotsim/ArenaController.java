package com.example.robotsim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.*;
import java.util.*;


public class ArenaController {

    // List to hold the Timelines for all robots
    private final List<Timeline> robotAnimations = new ArrayList<>();
    private boolean isPlaying = false;
    private int robotCount = 0;
    private final List<Robot> robots = new ArrayList<>();
    private final List<Obstacle> obstacles = new ArrayList<>();
    private int obstacleCount = 0;


    @FXML
    private TextArea robotInfoArea;

    @FXML
    private Pane arenaPane;


    @FXML
    public void initialize() {
        // Add one robot of each type
        Robot defaultRobot = new DefaultRobot("Default Robot", 1000, 100, 90);
        Robot sensorRobot = new SensorRobot("Sensor Robot", 1000, 300, 80);
        Robot userControlledRobot = new UserControlledRobot("User Controlled", 1000, 500, 100);
        Robot predatorRobot = new PredatorRobot("Predator Robot", 1000, 700, 70, this);
        Robot WhiskerRobot = new WhiskerRobot("Whisker Robot", 400, 500, 75);

        addRobotToArena(defaultRobot);
        addRobotToArena(sensorRobot);
        addRobotToArena(userControlledRobot);
        addRobotToArena(predatorRobot);
        addRobotToArena(WhiskerRobot);

        // Add one obstacle of each type
        Obstacle lamp = new LampObstacle(550, 650, 75);
        Obstacle rock = new RockObstacle(250, 250, 75);
        Obstacle lake = new LakeObstacle(750, 150, 75);

        addObstacleToArena(lamp);
        addObstacleToArena(rock);
        addObstacleToArena(lake);


        // Update robot info area
        updateRobotInfo();
    }

    // Helper method to add a robot to the arena and the list
    private void addRobotToArena(Robot robot) {
        arenaPane.getChildren().add(robot);
        robots.add(robot);
        robotCount++;
    }

    private void addObstacleToArena(Obstacle obstacle) {
        arenaPane.getChildren().add(obstacle); // Add to the UI
        obstacles.add(obstacle); // Add to the list
    }



    @FXML
    private void addRobot(ActionEvent event) {
        // List of robot types
        List<String> robotOptions = Arrays.asList("Default Robot", "Sensor Robot", "User Controlled", "Predator Robot", "Whisker Robot");

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

            Robot robot = switch (selectedType) {
                case "Default Robot" -> new DefaultRobot(name, 100 + robotCount * 50, 100 + robotCount * 50, size);
                case "Sensor Robot" -> new SensorRobot(name, 100 + robotCount * 50, 100 + robotCount * 50, size);
                case "Predator Robot" -> new PredatorRobot(name, 100 + robotCount * 50, 100 + robotCount * 50, size, this);
                case "User Controlled" -> new UserControlledRobot(name, 100 + robotCount * 50, 100 + robotCount * 50, size);
                case "Whisker Robot" -> new WhiskerRobot(name, 100 + robotCount * 50, 100 + robotCount * 50, size);
                default -> null;

                // Create the specific robot based on the selected type
            };

            if (robot != null) {
                // Add robot to the arena and the list of robots
                addRobotToArena(robot);
                robotCount++;
                updateRobotInfo(); // Update TextArea with the new robot
            }
        });
    }

    // Play method to start all robot animations
    public void play() {
        if (!isPlaying) {
            for (Robot robot : robots) {
                animateRobot(robot); // Start each robot's animation
            }
            isPlaying = true;
        }
    }

    // Pause method to stop all robot animations
    public void pause() {
        if (isPlaying) {
            for (Timeline animation : robotAnimations) {
                animation.pause(); // Pause each robot's animation
            }
            isPlaying = false;
        }
    }

    // Modify animateRobot method to handle each robot's individual animation
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

        animation.setCycleCount(Timeline.INDEFINITE); // Infinite loop
        animation.play(); // Start the animation

        robotAnimations.add(animation); // Add this animation to the list of all animations
    }

    public void NewArena() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("New Arena Created :)");
        // Clear all children (robots, obstacles, etc.) from the arena
        arenaPane.getChildren().clear();

        // Optionally, reset any relevant counters
        robotCount = 0;
        obstacleCount = 0;
        robotInfoArea.toFront();

        // Update or refresh the UI as necessary
        updateRobotInfo();
    }


    private void detectObstacleCollisions(Robot robot) {
        // Exclude SensorRobot and WhiskerRobot from obstacle interaction handling
        if (robot instanceof SensorRobot || robot instanceof WhiskerRobot) {
            return; // Skip handling for these specific robot types
        }

        // Continue with the normal obstacle interaction for other robot types
        handleNormalRobotObstacleInteraction(robot);
    }

    private void handleNormalRobotObstacleInteraction(Robot robot) {
        for (var node : arenaPane.getChildren()) {
            if (node instanceof Obstacle obstacle) {
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
        // Create a list of obstacles currently in the arena
        StringBuilder obstacleList = new StringBuilder("Current obstacles:\n");
        int index = 1;  // Obstacle numbering starts from 1 for user-friendliness
        for (int i = 0; i < arenaPane.getChildren().size(); i++) {
            if (arenaPane.getChildren().get(i) instanceof Obstacle obstacle) {
                obstacleList.append(index).append(". ").append(obstacle.getName()).append("\n");
                index++;
            }
        }

        // Check if there are any obstacles to remove
        if (index == 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Obstacles Available");
            alert.setHeaderText(null);
            alert.setContentText("There are no obstacles to remove.");
            alert.showAndWait();
            return;
        }

        // Prompt the user to select an obstacle by number or name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Select Obstacle to Remove");
        dialog.setHeaderText(null);
        dialog.setContentText(obstacleList.toString() + "\nEnter the name of the obstacle to remove:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String userInput = result.get();
            Obstacle obstacleToRemove = null;

            try {
                // Try parsing the input as an obstacle index (1-based)
                int obstacleIndex = Integer.parseInt(userInput.trim()) - 1;
                if (obstacleIndex >= 0 && obstacleIndex < arenaPane.getChildren().size()) {
                    if (arenaPane.getChildren().get(obstacleIndex) instanceof Obstacle selectedObstacle) {
                        obstacleToRemove = selectedObstacle;
                    }
                }
            } catch (NumberFormatException e) {
                // If the input is not a number, try to find the obstacle by name
                for (int i = 0; i < arenaPane.getChildren().size(); i++) {
                    if (arenaPane.getChildren().get(i) instanceof Obstacle obstacle) {
                        if (obstacle.getName().equalsIgnoreCase(userInput.trim())) {
                            obstacleToRemove = obstacle;
                            break;
                        }
                    }
                }
            }

            // If an obstacle was found, remove it
            if (obstacleToRemove != null) {
                arenaPane.getChildren().remove(obstacleToRemove);
                obstacleCount--;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Obstacle Not Found");
                alert.setHeaderText(null);
                alert.setContentText("No obstacle found with the name or number '" + userInput + "'.");
                alert.showAndWait();
            }
        }
    }


    public void removeRobot(ActionEvent event) {
        // Check if there are any robots in the list
        if (robots.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Robots Available");
            alert.setHeaderText(null);
            alert.setContentText("There are no robots to remove.");
            alert.showAndWait();
            return;
        }

        // Create a dialog to display the list of robots
        StringBuilder robotList = new StringBuilder("Current robots:\n");
        for (int i = 0; i < robots.size(); i++) {
            robotList.append(i + 1).append(". ").append(robots.get(i).getName()).append("\n");
        }

        // Prompt the user to enter the number or name of the robot to remove
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Select Robot to Remove");
        dialog.setHeaderText(null);
        dialog.setContentText(robotList.toString() + "\nEnter the number or name of the robot to remove:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String userInput = result.get();
            Robot robotToRemove = null;

            try {
                // Try parsing the input as a robot index (1-based)
                int robotIndex = Integer.parseInt(userInput.trim()) - 1;
                if (robotIndex >= 0 && robotIndex < robots.size()) {
                    robotToRemove = robots.get(robotIndex);
                }
            } catch (NumberFormatException e) {
                // If the input is not a number, try to find the robot by name
                for (Robot robot : robots) {
                    if (robot.getName().equalsIgnoreCase(userInput.trim())) {
                        robotToRemove = robot;
                        break;
                    }
                }
            }

            // If a robot was found, remove it
            if (robotToRemove != null) {
                arenaPane.getChildren().remove(robotToRemove);
                robots.remove(robotToRemove);
                robotCount--;
                updateRobotInfo();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Robot Not Found");
                alert.setHeaderText(null);
                alert.setContentText("No robot found with the name or number '" + userInput + "'.");
                alert.showAndWait();
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

            Obstacle obstacle = switch (selectedType) {
                case "Lamp" -> new LampObstacle(100 + obstacleCount * 50, 100 + obstacleCount * 50, size);
                case "Rock" -> new RockObstacle(100 + obstacleCount * 50, 100 + obstacleCount * 50, size);
                case "Lake" -> new LakeObstacle(100 + obstacleCount * 50, 100 + obstacleCount * 50, size);
                default -> null;

                // Create the specific obstacle based on the selected type
            };

            if (obstacle != null) {
                arenaPane.getChildren().add(obstacle);
                addObstacleToArena(obstacle);
                obstacleCount++;
            }
        });
    }

    @FXML
    private void fileAlert(ActionEvent event) {
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
            ArenaFileHandler fileHandler = new ArenaFileHandler(robots, obstacles, arenaPane, this);
            if (result.get() == saveButton) {
                try {
                    fileHandler.saveArena();
                    showInfoDialog("Success", "The arena has been successfully saved.");
                } catch (IOException e) {
                    showErrorDialog("Error", "Failed to save the arena: " + e.getMessage());
                }
            } else if (result.get() == loadButton) {
                try {
                    // Reset Arena
                    NewArena();
                    fileHandler.loadArena();
                    updateRobotInfo(); // Refresh robot information if needed
                    showInfoDialog("Success", "The arena has been successfully loaded.");
                } catch (IOException e) {
                    showErrorDialog("Error", "Failed to load the arena: " + e.getMessage());
                }
            }
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public List<Robot> getRobots() {
        return robots;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public Pane getArenaPane() {
        return arenaPane;
    }

    // Example method to remove an object from both arena and lists
    public void removeObject(Node object) {
        if (object instanceof Robot) {
            removeRobot((Robot) object);
        } else if (object instanceof Obstacle) {
            removeObstacle((Obstacle) object);
        }
    }
    // Remove a robot from the arena and list
    public void removeRobot(Robot robot) {
        arenaPane.getChildren().remove(robot);
        robots.remove(robot);
    }

    // Remove an obstacle from the arena and list
    public void removeObstacle(Obstacle obstacle) {
        arenaPane.getChildren().remove(obstacle);
        obstacles.remove(obstacle);
    }



}