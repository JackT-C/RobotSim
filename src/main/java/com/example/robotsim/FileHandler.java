package com.example.robotsim;

import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;

public class FileHandler {

    private Stage stage;

    public FileHandler(Stage stage) {
        this.stage = stage;
    }

    public void saveArena(List<Robot> robots, List<Obstacle> obstacles) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arena Files", "*.ser"));
        fileChooser.setInitialFileName("arena_config.ser");

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
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
    }

    public void loadArena(List<Robot> robots, List<Obstacle> obstacles, AnchorPane arenaPane) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arena Files", "*.ser"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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
                        addRobotToArena(robot, arenaPane, robots);
                    } else if (loadingObstacles) {
                        String[] parts = line.split(",");
                        String type = parts[0];
                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);
                        double size = Double.parseDouble(parts[3]);
                        Obstacle obstacle = createObstacleFromType(type, x, y, size);
                        addObstacleToArena(obstacle, arenaPane, obstacles);
                    }
                }
            }
        }
    }

    private Robot createRobotFromType(String type, String name, double x, double y) {
        return switch (type) {
            case "SensorRobot" -> new SensorRobot(name, x, y, 100);
            case "DefaultRobot" -> new DefaultRobot(name, x, y, 100);
            case "PredatorRobot" -> new PredatorRobot(name, x, y, 100);
            case "UserControlledRobot" -> new UserControlledRobot(name, x, y, 100);
            case "WhiskerRobot" -> new WhiskerRobot(name, x, y, 100);
            default -> throw new IllegalArgumentException("Unknown robot type: " + type);
        };
    }

    private Obstacle createObstacleFromType(String type, double x, double y, double size) {
        return switch (type) {
            case "LampObstacle" -> new LampObstacle(x, y, size);
            case "RockObstacle" -> new RockObstacle(x, y, size);
            case "LakeObstacle" -> new LakeObstacle(x, y, size);
            default -> throw new IllegalArgumentException("Unknown obstacle type: " + type);
        };
    }

    private void addRobotToArena(Robot robot, AnchorPane arenaPane, List<Robot> robots) {
        arenaPane.getChildren().add(robot);
        robots.add(robot);
    }

    private void addObstacleToArena(Obstacle obstacle, AnchorPane arenaPane, List<Obstacle> obstacles) {
        arenaPane.getChildren().add(obstacle);
        obstacles.add(obstacle);
    }
}
