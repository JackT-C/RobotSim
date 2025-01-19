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

/**
 * The ArenaController class manages the arena in which robots and obstacles are placed.
 * It provides functionality to add robots, control their animations, and manage obstacles.
 */
public class ArenaController {

    // List to hold the Timelines for all robots
    private final List<Timeline> robotAnimations = new ArrayList<>();
    private boolean isPlaying = false;  // Flag to check if robots are currently playing
    private int robotCount = 0;  // Counter for the number of robots
    private final List<Robot> robots = new ArrayList<>();  // List to hold all robots
    private final List<Obstacle> obstacles = new ArrayList<>();  // List to hold all obstacles
    private int obstacleCount = 0;  // Counter for the number of obstacles

    @FXML
    private TextArea robotInfoArea;  // Text area to display information about robots

    @FXML
    private Pane arenaPane;  // Pane where robots and obstacles are displayed

    /**
     * Initializes the arena with default robots and obstacles.
     * This method is called automatically when the controller is initialized.
     */
    @FXML
    public void initialize() {
        // Create and add one robot of each type to the arena
        Robot defaultRobot = new DefaultRobot("Default Robot", 1000, 100, 90);
        Robot sensorRobot = new SensorRobot("Sensor Robot", 1000, 300, 80);
        Robot userControlledRobot = new UserControlledRobot("User Controlled", 1000, 500, 100);
        Robot predatorRobot = new PredatorRobot("Predator Robot", 1000, 700, 70, this);
        Robot whiskerRobot = new WhiskerRobot("Whisker Robot", 400, 500, 75);

        addRobotToArena(defaultRobot);
        addRobotToArena(sensorRobot);
        addRobotToArena(userControlledRobot);
        addRobotToArena(predatorRobot);
        addRobotToArena(whiskerRobot);

        // Create and add one obstacle of each type to the arena
        Obstacle lamp = new LampObstacle(550, 650, 75);
        Obstacle rock = new RockObstacle(250, 250, 75);
        Obstacle lake = new LakeObstacle(750, 150, 75);

        addObstacleToArena(lamp);
        addObstacleToArena(rock);
        addObstacleToArena(lake);

        // Update the robot info area with the current robot details
        updateRobotInfo();
    }

    /**
     * Helper method to add a robot to the arena and the robot list.
     * @param robot The robot to add.
     */
    private void addRobotToArena(Robot robot) {
        arenaPane.getChildren().add(robot);  // Add the robot to the display
        robots.add(robot);  // Add the robot to the list
        robotCount++;  // Increment the robot count
    }

    /**
     * Helper method to add an obstacle to the arena and the obstacle list.
     * @param obstacle The obstacle to add.
     */
    private void addObstacleToArena(Obstacle obstacle) {
        arenaPane.getChildren().add(obstacle);  // Add the obstacle to the display
        obstacles.add(obstacle);  // Add the obstacle to the list
    }

    /**
     * Displays a dialog to allow the user to add a robot to the arena.
     * The user can customize the robot's name, type, and size.
     * @param event The event triggered when the user attempts to add a robot.
     */
    @FXML
    private void addRobot(ActionEvent event) {
        // List of robot types to choose from
        List<String> robotOptions = Arrays.asList("Default Robot", "Sensor Robot", "User Controlled", "Predator Robot", "Whisker Robot");

        // Create a dialog to customize the robot
        Dialog<Pair<String, Double>> dialog = new Dialog<>();
        dialog.setTitle("Add Robot");
        dialog.setHeaderText("Customize your robot:");

        // Create and configure fields for the robot's name, type, and size
        Label nameLabel = new Label("Robot Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter robot name");

        Label typeLabel = new Label("Robot Type:");
        ChoiceBox<String> typeChoiceBox = new ChoiceBox<>();
        typeChoiceBox.getItems().addAll(robotOptions);
        typeChoiceBox.setValue(robotOptions.get(0)); // Default to the first option

        Label sizeLabel = new Label("Robot Size:");
        Slider sizeSlider = new Slider(30, 150, 50); // Min size: 30, Max size: 150, Default: 50
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setMajorTickUnit(20);
        sizeSlider.setBlockIncrement(5);

        // Layout for the dialog's content
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

        // Add OK and Cancel buttons to the dialog
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Convert the result to a pair containing the robot's name, type, and size
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    name = "Robot " + (robotCount + 1);  // Default name if left blank
                }
                String type = typeChoiceBox.getValue();
                Double size = sizeSlider.getValue();
                return new Pair<>(name + "|" + type, size);  // Return the customized robot data
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

            // Create the specific robot based on the selected type
            Robot robot = switch (selectedType) {
                case "Default Robot" -> new DefaultRobot(name, 100 + robotCount * 50, 100 + robotCount * 50, size);
                case "Sensor Robot" -> new SensorRobot(name, 100 + robotCount * 50, 100 + robotCount * 50, size);
                case "Predator Robot" -> new PredatorRobot(name, 100 + robotCount * 50, 100 + robotCount * 50, size, this);
                case "User Controlled" -> new UserControlledRobot(name, 100 + robotCount * 50, 100 + robotCount * 50, size);
                case "Whisker Robot" -> new WhiskerRobot(name, 100 + robotCount * 50, 100 + robotCount * 50, size);
                default -> null;
            };

            if (robot != null) {
                addRobotToArena(robot);  // Add the newly created robot to the arena
                robotCount++;  // Increment robot count
                updateRobotInfo();  // Update robot info display
            }
        });
    }

    /**
     * Starts the animations for all robots in the arena.
     * This method is called when the play button is clicked.
     */
    public void play() {
        if (!isPlaying) {
            for (Robot robot : robots) {
                animateRobot(robot);  // Start each robot's animation
            }
            isPlaying = true;  // Set the playing flag to true
        }
    }

    /**
     * Pauses the animations for all robots in the arena.
     * This method is called when the pause button is clicked.
     */
    public void pause() {
        if (isPlaying) {
            for (Timeline animation : robotAnimations) {
                animation.pause();  // Pause each robot's animation
            }
            isPlaying = false;  // Set the playing flag to false
        }
    }

    /**
     * Animates a robot in the arena, handling its movement and interactions with boundaries, obstacles, and other elements.
     * The robot's position is updated periodically, and its collisions with walls, the text area, and obstacles are handled.
     *
     * @param robot the robot to animate
     */
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

    /**
     * Creates a new arena by clearing all current robots and obstacles, and resets necessary counters.
     * Displays an information alert to notify the user that a new arena has been created.
     */
    public void NewArena() {
        // Display an information alert to notify the user about the new arena creation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("New Arena Created :)");
        alert.show();

        // Clear all children (robots, obstacles, etc.) from the arena
        arenaPane.getChildren().clear();

        // reset any relevant counters
        robotCount = 0;
        obstacleCount = 0;
        robots.clear();
        obstacles.clear();
        // Ensure the robotInfoArea remains visible and in the top-right corner
        if (!arenaPane.getChildren().contains(robotInfoArea)) {
            arenaPane.getChildren().add(robotInfoArea); // Re-add the TextView if it's not already added
        }

        robotInfoArea.toFront(); // Bring the robotInfoArea to the front

        // Update the robot information to reflect the new arena state
        updateRobotInfo();
    }


    /**
     * Detects collisions between a robot and obstacles in the arena.
     * SensorRobot and WhiskerRobot are excluded from obstacle collision handling.
     *
     * @param robot the robot to check for obstacle collisions
     */
    private void detectObstacleCollisions(Robot robot) {
        // Exclude SensorRobot and WhiskerRobot from obstacle interaction handling
        if (robot instanceof SensorRobot || robot instanceof WhiskerRobot) {
            return; // Skip handling for these specific robot types
        }

        // Continue with the normal obstacle interaction for other robot types
        handleNormalRobotObstacleInteraction(robot);
    }

    /**
     * Handles the interaction of a robot with obstacles in the arena.
     * This is used for normal robots (not SensorRobot or WhiskerRobot).
     *
     * @param robot the robot to check for obstacle interactions
     */
    private void handleNormalRobotObstacleInteraction(Robot robot) {
        for (var node : arenaPane.getChildren()) {
            if (node instanceof Obstacle obstacle) {
                if (isCollidingWithObstacle(robot, obstacle)) {
                    obstacle.handleCollision(robot); // Polymorphic behavior handles specific logic
                }
            }
        }
    }

    /**
     * Checks if a robot is colliding with a given obstacle.
     *
     * @param robot the robot to check
     * @param obstacle the obstacle to check for collision with
     * @return true if the robot is colliding with the obstacle, false otherwise
     */
    private boolean isCollidingWithObstacle(Robot robot, Obstacle obstacle) {
        Bounds robotBounds = robot.getBoundsInParent();
        Bounds obstacleBounds = obstacle.getBoundsInParent();
        return robotBounds.intersects(obstacleBounds);
    }

    /**
     * Checks if a robot is colliding with the text area that displays robot information.
     *
     * @param robot the robot to check for collision with the text area
     * @return true if the robot is colliding with the text area, false otherwise
     */
    private boolean isCollidingWithTextArea(Robot robot) {
        Bounds textAreaBounds = robotInfoArea.getBoundsInParent();
        Bounds robotBounds = robot.getBoundsInParent();
        return robotBounds.intersects(textAreaBounds);
    }

    /**
     * Handles the behavior when a robot collides with the text area, making it bounce off the text area.
     * Adjusts the robot's position slightly after collision.
     *
     * @param robot the robot to adjust after collision with the text area
     */
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

    /**
     * Updates the robot information displayed in the robotInfoArea TextArea.
     * This includes the name, X and Y positions, and the robot's size.
     */
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

    /**
     * Handles the action to return to the main menu.
     * Loads the ApplicationMenu.fxml scene and sets it as the current scene.
     *
     * @param event the action event that triggers this method
     */
    public void goBackToMenu(ActionEvent event) {
        try {
            Parent menuRoot = FXMLLoader.load(getClass().getResource("ApplicationMenu.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(menuRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes an obstacle from the arena.
     * Displays a prompt for the user to select which obstacle to remove by name or number.
     * If the input is invalid, an error message is shown.
     *
     * @param event the action event that triggers this method
     */
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
    /**
     * Removes a robot from the arena and the list of robots.
     * If no robots are available, displays an informational alert.
     * Prompts the user to select a robot by either index or name.
     * If the robot is found, removes it from the arena and updates the robot info.
     *
     * @param event The action event that triggered the removal.
     */
    public void removeRobot(ActionEvent event) {
        if (robots.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Robots Available");
            alert.setHeaderText(null);
            alert.setContentText("There are no robots to remove.");
            alert.showAndWait();
            return;
        }

        StringBuilder robotList = new StringBuilder("Current robots:\n");
        for (int i = 0; i < robots.size(); i++) {
            robotList.append(i + 1).append(". ").append(robots.get(i).getName()).append("\n");
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Select Robot to Remove");
        dialog.setHeaderText(null);
        dialog.setContentText(robotList.toString() + "\nEnter the number or name of the robot to remove:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String userInput = result.get();
            Robot robotToRemove = null;

            try {
                int robotIndex = Integer.parseInt(userInput.trim()) - 1;
                if (robotIndex >= 0 && robotIndex < robots.size()) {
                    robotToRemove = robots.get(robotIndex);
                }
            } catch (NumberFormatException e) {
                for (Robot robot : robots) {
                    if (robot.getName().equalsIgnoreCase(userInput.trim())) {
                        robotToRemove = robot;
                        break;
                    }
                }
            }

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

    /**
     * Adds a new obstacle to the arena based on user input.
     * Prompts the user to choose the obstacle type and size.
     * Depending on the selection, creates a new obstacle and adds it to the arena.
     *
     * @param event The action event that triggered the addition.
     */
    public void addObstacle(ActionEvent event) {
        List<String> obstacleOptions = Arrays.asList("Lamp", "Rock", "Lake");

        Dialog<Pair<String, Double>> dialog = new Dialog<>();
        dialog.setTitle("Add Obstacle");
        dialog.setHeaderText("Choose an obstacle type and size:");

        Label typeLabel = new Label("Obstacle Type:");
        ChoiceBox<String> typeChoiceBox = new ChoiceBox<>();
        typeChoiceBox.getItems().addAll(obstacleOptions);
        typeChoiceBox.setValue(obstacleOptions.get(0));

        Label sizeLabel = new Label("Obstacle Size:");
        Slider sizeSlider = new Slider(50, 200, 100);
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

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Pair<>(typeChoiceBox.getValue(), sizeSlider.getValue());
            }
            return null;
        });

        Optional<Pair<String, Double>> result = dialog.showAndWait();
        result.ifPresent(obstacleData -> {
            String selectedType = obstacleData.getKey();
            double size = obstacleData.getValue();

            Obstacle obstacle = switch (selectedType) {
                case "Lamp" -> new LampObstacle(100 + obstacleCount * 50, 100 + obstacleCount * 50, size);
                case "Rock" -> new RockObstacle(100 + obstacleCount * 50, 100 + obstacleCount * 50, size);
                case "Lake" -> new LakeObstacle(100 + obstacleCount * 50, 100 + obstacleCount * 50, size);
                default -> null;
            };

            if (obstacle != null) {
                arenaPane.getChildren().add(obstacle);
                addObstacleToArena(obstacle);
                obstacleCount++;
            }
        });
    }

    /**
     * Displays an alert with options to save or load the arena state.
     * Depending on the user's choice, either saves or loads the arena's robots and obstacles.
     *
     * @param event The action event that triggered the file alert.
     */
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
                    showInfoDialog("The arena has been successfully saved.");
                } catch (IOException e) {
                    showErrorDialog("Failed to save the arena: " + e.getMessage());
                }
            } else if (result.get() == loadButton) {
                try {
                    NewArena();
                    fileHandler.loadArena();
                    if (!arenaPane.getChildren().contains(robotInfoArea)) {
                        arenaPane.getChildren().add(robotInfoArea); // Re-add the TextView if it's not already added
                    }
                    updateRobotInfo();
                    showInfoDialog("The arena has been successfully loaded.");
                } catch (IOException e) {
                    showErrorDialog("Failed to load the arena: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Displays an error dialog with the specified title and message.
     *
     * @param message The error message to display.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an informational dialog with the specified title and message.
     *
     * @param message The informational message to display.
     */
    private void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Gets the list of robots in the arena.
     *
     * @return A list of robots in the arena.
     */
    public List<Robot> getRobots() {
        return robots;
    }

    /**
     * Gets the list of obstacles in the arena.
     *
     * @return A list of obstacles in the arena.
     */
    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    /**
     * Gets the pane representing the arena.
     *
     * @return The pane representing the arena.
     */
    public Pane getArenaPane() {
        return arenaPane;
    }

    /**
     * Removes an object (either robot or obstacle) from both the arena and the respective list.
     *
     * @param object The object to remove (either a robot or an obstacle).
     */
    public void removeObject(Node object) {
        if (object instanceof Robot) {
            removeRobot((Robot) object);
        } else if (object instanceof Obstacle) {
            removeObstacle((Obstacle) object);
        }
    }

    /**
     * Removes a robot from the arena and the list of robots.
     *
     * @param robot The robot to remove.
     */
    public void removeRobot(Robot robot) {
        arenaPane.getChildren().remove(robot);
        robots.remove(robot);
    }

    /**
     * Removes an obstacle from the arena and the list of obstacles.
     *
     * @param obstacle The obstacle to remove.
     */
    public void removeObstacle(Obstacle obstacle) {
        arenaPane.getChildren().remove(obstacle);
        obstacles.remove(obstacle);
    }
}