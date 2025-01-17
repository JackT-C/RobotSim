package com.example.robotsim;

import java.io.*;
import java.util.List;

public class FileHandler {

    private static final String DEFAULT_FILE_PATH = "simulation_config.ser";

    public static void saveConfiguration(SimulationConfiguration config, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(config);
        }
    }

    public static SimulationConfiguration loadConfiguration(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (SimulationConfiguration) ois.readObject();
        }
    }

    public static SimulationConfiguration loadDefaultConfiguration() {
        SimulationConfiguration defaultConfig = new SimulationConfiguration();

        // Default Robots
        List<Robot> defaultRobots = List.of(
                new DefaultRobot("Default Robot", 100, 100, 50),
                new SensorRobot("Sensor Robot", 200, 200, 50),
                new UserControlledRobot("User Controlled", 300, 300, 50),
                new PredatorRobot("Predator Robot", 400, 400, 50)
        );
        defaultConfig.setRobots(defaultRobots);

        // Default Obstacles
        List<Obstacle> defaultObstacles = List.of(
                new LampObstacle(150, 150, 75),
                new RockObstacle(250, 250, 75),
                new LakeObstacle(350, 350, 75)
        );
        defaultConfig.setObstacles(defaultObstacles);

        return defaultConfig;
    }
}
