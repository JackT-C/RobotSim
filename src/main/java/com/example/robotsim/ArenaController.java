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
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ArenaController {

    private int robotCount = 0;
    private final List<Robot> robots = new ArrayList<>();
    private final List<Obstacle> obstacles = new ArrayList<Obstacle>();
    private int obstacleCount = 0;

    @FXML
    private TextArea robotInfoArea;

    @FXML
    private Pane arenaPane;

    @FXML
    private Button addRobotButton;

    @FXML
    public void initialize() {
        // Add one robot of each type
        Robot defaultRobot = new DefaultRobot("Default Robot", 1000, 100, 90);
        Robot sensorRobot = new SensorRobot("Sensor Robot", 1000, 300, 80);
        Robot userControlledRobot = new UserControlledRobot("User Controlled", 1000, 500, 100);
        Robot predatorRobot = new PredatorRobot("Predator Robot", 1000, 700, 70);
        Robot WhiskerRobot = new WhiskerRobot("Whisker Robot", 1000, 500, 75);

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
        animateRobot(robot);
        robotCount++;
    }

    // Helper method to add an obstacle to the arena
    private void addObstacleToArena(Obstacle obstacle) {
        arenaPane.getChildren().add(obstacle);
        obstacles.add(obstacle); // Ensure obstacles are added to the list
        obstacleCount++;
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
                case "Whisker Robot":
                    robot =  new WhiskerRobot(name, 100 + robotCount * 50, 100 + robotCount * 50, size);
                    break;
            }

            if (robot != null) {
                // Add robot to the arena and the list of robots
                addRobotToArena(robot);
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
        if (robot instanceof SensorRobot sensorRobot) {
            // Sensor Robot (Beam) Obstacle Handling
            for (Obstacle obstacle : obstacles) {
                // Use getBoundsInParent to detect global bounds intersection
                Bounds beamBounds = sensorRobot.getBeam().getBoundsInParent();
                Bounds obstacleBounds = obstacle.getBoundsInParent();
                if (beamBounds.intersects(obstacleBounds)) {
                    System.out.println("SensorRobot detected an obstacle: " + obstacle);
                    sensorRobot.avoidObstacle(obstacle); // Trigger avoid logic
                    return; // Exit after detecting one obstacle
                }
            }
        } else if (robot instanceof WhiskerRobot whiskerRobot) {
            // Whisker Robot (Whiskers) Obstacle Handling
            for (Obstacle obstacle : obstacles) {
                Bounds frontWhiskerBounds = whiskerRobot.getFrontWhisker().getBoundsInParent();
                Bounds leftWhiskerBounds = whiskerRobot.getLeftWhisker().getBoundsInParent();
                Bounds rightWhiskerBounds = whiskerRobot.getRightWhisker().getBoundsInParent();
                Bounds obstacleBounds = obstacle.getBoundsInParent();

                // Check if any of the whiskers intersect with the obstacle
                if (frontWhiskerBounds.intersects(obstacleBounds)) {
                    System.out.println("WhiskerRobot detected an obstacle with front whisker: " + obstacle);
                    whiskerRobot.detectObstacle(obstacle);
                    return;
                } else if (leftWhiskerBounds.intersects(obstacleBounds)) {
                    System.out.println("WhiskerRobot detected an obstacle with left whisker: " + obstacle);
                    whiskerRobot.detectObstacle(obstacle);
                    return;
                } else if (rightWhiskerBounds.intersects(obstacleBounds)) {
                    System.out.println("WhiskerRobot detected an obstacle with right whisker: " + obstacle);
                    whiskerRobot.detectObstacle(obstacle);
                    return;
                }
            }
        } else {
            // Default handling for other robot types
            handleNormalRobotObstacleInteraction(robot);
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
                addObstacleToArena(obstacle);
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
                try {
                    saveArena("arena_config.ser");
                    showInfoDialog("Success", "The arena has been successfully saved.");
                } catch (IOException e) {
                    showErrorDialog("Error", "Failed to save the arena: " + e.getMessage());
                }
            } else if (result.get() == loadButton) {
                try {
                    loadArena("arena_config.ser");
                    showInfoDialog("Success", "The arena has been successfully loaded.");
                } catch (IOException e) {
                    showErrorDialog("Error", "Failed to load the arena: " + e.getMessage());
                }
            }
        }
    }


    private Robot createRobotFromType(String type, String name, double x, double y) {
        switch (type) {
            case "SensorRobot":
                return new SensorRobot(name, x, y, 100);
            case "DefaultRobot":
                return new DefaultRobot(name, x, y, 100);
            case "PredatorRobot":
                return new PredatorRobot(name, x, y, 100);
            case "UserControlledRobot":
                return new UserControlledRobot(name, x, y, 100);
            case "WhiskerRobot":  // Add support for WhiskerRobot
                return new WhiskerRobot(name, x, y, 100);
            default:
                throw new IllegalArgumentException("Unknown robot type: " + type);
        }
    }


    private Obstacle createObstacleFromType(String type, double x, double y, double size) {
        switch (type) {
            case "LampObstacle":
                return new LampObstacle(x, y, size);
            case "RockObstacle":
                return new RockObstacle(x, y, size);
            case "LakeObstacle":
                return new LakeObstacle(x, y, size);
            default:
                throw new IllegalArgumentException("Unknown obstacle type: " + type);
        }
    }




    private void saveArena(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Save robots
            writer.write("Robots\n");
            for (Robot robot : robots) {
                writer.write(robot.getClass().getSimpleName() + "," + robot.getName() + "," +
                        robot.getLayoutX() + "," + robot.getLayoutY() + "\n");
            }

            // Save obstacles
            writer.write("Obstacles\n");
            for (Obstacle obstacle : obstacles) {
                writer.write(obstacle.getClass().getSimpleName() + "," + obstacle.getLayoutX() + "," +
                        obstacle.getLayoutY() + "," + obstacle.getSize() + "\n");
            }
        }
    }
    private void loadArena(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean loadingRobots = false;
            boolean loadingObstacles = false;

            arenaPane.getChildren().clear();
            robots.clear();
            obstacles.clear();

            while ((line = reader.readLine()) != null) {
                if (line.equals("Robots")) {
                    loadingRobots = true;
                    loadingObstacles = false;
                } else if (line.equals("Obstacles")) {
                    loadingObstacles = true;
                    loadingRobots = false;
                } else if (loadingRobots) {
                    String[] parts = line.split(",");
                    String type = parts[0];
                    String name = parts[1];
                    double x = Double.parseDouble(parts[2]);
                    double y = Double.parseDouble(parts[3]);
                    Robot robot = createRobotFromType(type, name, x, y);
                    addRobotToArena(robot);
                } else if (loadingObstacles) {
                    String[] parts = line.split(",");
                    String type = parts[0];
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double size = Double.parseDouble(parts[3]);
                    Obstacle obstacle = createObstacleFromType(type, x, y, size);
                    addObstacleToArena(obstacle);
                }
            }

            updateRobotInfo(); // Update UI info about robots
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





}
