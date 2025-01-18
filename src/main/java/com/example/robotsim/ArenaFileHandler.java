package com.example.robotsim;

import javafx.scene.layout.Pane;
import javafx.scene.control.Alert;

import java.io.*;
import java.util.List;

public class ArenaFileHandler {

    private final String filePath = "arena_config.txt"; // Predefined file path
    private List<Robot> robots;
    private List<Obstacle> obstacles;
    private Pane arenaPane;

    public ArenaFileHandler(List<Robot> robots, List<Obstacle> obstacles, Pane arenaPane) {
        this.robots = robots;
        this.obstacles = obstacles;
        this.arenaPane = arenaPane;
    }

    public void saveArena() throws IOException {
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

    public void loadArena() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("The predefined file does not exist: " + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean loadingRobots = false;
            boolean loadingObstacles = false;

            arenaPane.getChildren().clear(); // Clear all existing elements in the pane
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
                    robots.add(robot);
                    arenaPane.getChildren().add(robot); // Add robot to the pane
                } else if (loadingObstacles) {
                    String[] parts = line.split(",");
                    String type = parts[0];
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double size = Double.parseDouble(parts[3]);
                    Obstacle obstacle = createObstacleFromType(type, x, y, size);
                    obstacles.add(obstacle);
                    arenaPane.getChildren().add(obstacle); // Add obstacle to the pane
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
}
