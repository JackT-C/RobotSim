package com.example.robotsim;

import javafx.scene.layout.Pane;

import java.io.*;
import java.util.List;

/**
 * This class handles the saving and loading of arena configuration to and from a file.
 * It interacts with the arena controller, robots, and obstacles to persist the state of the arena.
 */
public class ArenaFileHandler {

    private final String filePath = "arena_config.txt"; // Predefined file path
    private ArenaController arenaController;  // Reference to ArenaController to access arena functionality
    private List<Robot> robots;  // List of robots in the arena
    private List<Obstacle> obstacles;  // List of obstacles in the arena
    private Pane arenaPane;  // Pane representing the arena where robots and obstacles are displayed

    /**
     * Constructs an ArenaFileHandler instance with the specified robots, obstacles, and arena pane.
     *
     * @param robots The list of robots in the arena.
     * @param obstacles The list of obstacles in the arena.
     * @param arenaPane The pane representing the arena.
     * @param arenaController The ArenaController instance.
     */
    public ArenaFileHandler(List<Robot> robots, List<Obstacle> obstacles, Pane arenaPane, ArenaController arenaController) {
        this.robots = robots;
        this.obstacles = obstacles;
        this.arenaPane = arenaPane;
        this.arenaController = arenaController;
    }

    /**
     * Saves the current state of robots and obstacles in the arena to a file.
     * The file format is a text file with a predefined structure.
     *
     * @throws IOException If an I/O error occurs while saving the arena configuration.
     */
    public void saveArena() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Save robots section
            writer.write("Robots\n");
            for (Robot robot : robots) {
                writer.write(robot.getClass().getSimpleName() + "," + robot.getName() + "," +
                        robot.getLayoutX() + "," + robot.getLayoutY() + "\n");
            }

            // Save obstacles section
            writer.write("Obstacles\n");
            for (Obstacle obstacle : obstacles) {
                writer.write(obstacle.getClass().getSimpleName() + "," +
                        obstacle.getX() + "," +
                        obstacle.getY() + "," +
                        obstacle.getSize() + "\n");
            }
        }
    }

    /**
     * Loads the arena configuration from a file and reconstructs the robots and obstacles.
     * The file format is expected to have a predefined structure for robots and obstacles.
     *
     * @throws IOException If an I/O error occurs while loading the arena configuration.
     * @throws FileNotFoundException If the arena configuration file does not exist.
     */
    public void loadArena() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("The predefined file does not exist: " + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean loadingRobots = false;
            boolean loadingObstacles = false;

            // Clear the current arena state before loading new data
            arenaPane.getChildren().clear();
            robots.clear();
            obstacles.clear();

            // Read the file and parse robots and obstacles
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
                    arenaPane.getChildren().add(obstacle);
                }
            }
        }
    }

    /**
     * Creates a robot instance based on the type provided in the configuration.
     *
     * @param type The type of the robot (e.g., SensorRobot, DefaultRobot, etc.).
     * @param name The name of the robot.
     * @param x The X coordinate of the robot.
     * @param y The Y coordinate of the robot.
     * @return The created robot instance.
     * @throws IllegalArgumentException If the robot type is unknown.
     */
    private Robot createRobotFromType(String type, String name, double x, double y) {
        return switch (type) {
            case "SensorRobot" -> new SensorRobot(name, x, y, 100);
            case "DefaultRobot" -> new DefaultRobot(name, x, y, 100);
            case "PredatorRobot" -> new PredatorRobot(name, x, y, 100, arenaController);
            case "UserControlledRobot" -> new UserControlledRobot(name, x, y, 100);
            case "WhiskerRobot" -> new WhiskerRobot(name, x, y, 100);
            default -> throw new IllegalArgumentException("Unknown robot type: " + type);
        };
    }

    /**
     * Creates an obstacle instance based on the type provided in the configuration.
     *
     * @param type The type of the obstacle (e.g., LampObstacle, RockObstacle, etc.).
     * @param x The X coordinate of the obstacle.
     * @param y The Y coordinate of the obstacle.
     * @param size The size of the obstacle.
     * @return The created obstacle instance.
     * @throws IllegalArgumentException If the obstacle type is unknown.
     */
    private Obstacle createObstacleFromType(String type, double x, double y, double size) {
        return switch (type) {
            case "LampObstacle" -> new LampObstacle(x, y, size);
            case "RockObstacle" -> new RockObstacle(x, y, size);
            case "LakeObstacle" -> new LakeObstacle(x, y, size);
            default -> throw new IllegalArgumentException("Unknown obstacle type: " + type);
        };
    }

    /**
     * Returns the list of robots in the arena.
     *
     * @return A list of robots in the arena.
     */
    public List<Robot> getRobots() {
        return robots;
    }

    /**
     * Returns the list of obstacles in the arena.
     *
     * @return A list of obstacles in the arena.
     */
    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    /**
     * Returns the pane representing the arena.
     *
     * @return The pane representing the arena.
     */
    public Pane getArenaPane() {
        return arenaPane;
    }
}
