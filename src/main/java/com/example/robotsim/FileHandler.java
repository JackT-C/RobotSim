package com.example.robotsim;

import javafx.stage.FileChooser;
import javafx.scene.layout.Pane;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    public static void saveArena(Pane arenaPane, List<Robot> robots, int robotCount, int obstacleCount) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Arena");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arena Files", "*.arena"));

        File file = fileChooser.showSaveDialog(arenaPane.getScene().getWindow());
        if (file != null) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                // Save robots
                oos.writeInt(robotCount);
                oos.writeObject(new ArrayList<>(robots));

                // Save obstacles
                oos.writeInt(obstacleCount);
                List<Obstacle> obstacles = new ArrayList<>();
                for (var node : arenaPane.getChildren()) {
                    if (node instanceof Obstacle) {
                        obstacles.add((Obstacle) node);
                    }
                }
                oos.writeObject(obstacles);

                // Save arena size
                oos.writeDouble(arenaPane.getWidth());
                oos.writeDouble(arenaPane.getHeight());

                showAlert("Save Successful", "Arena has been saved successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Save Failed", "An error occurred while saving the arena.");
            }
        }
    }

    public static void loadArena(Pane arenaPane, List<Robot> robots) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Arena");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arena Files", "*.arena"));

        File file = fileChooser.showOpenDialog(arenaPane.getScene().getWindow());
        if (file != null) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                // Clear existing arena
                arenaPane.getChildren().clear();
                robots.clear();

                // Load robots
                int robotCount = ois.readInt();
                List<Robot> loadedRobots = (List<Robot>) ois.readObject();
                robots.addAll(loadedRobots);
                for (Robot robot : loadedRobots) {
                    arenaPane.getChildren().add(robot);
                }

                // Load obstacles
                int obstacleCount = ois.readInt();
                List<Obstacle> loadedObstacles = (List<Obstacle>) ois.readObject();
                for (Obstacle obstacle : loadedObstacles) {
                    arenaPane.getChildren().add(obstacle);
                }

                // Load arena size
                double width = ois.readDouble();
                double height = ois.readDouble();
                arenaPane.setPrefSize(width, height);

                showAlert("Load Successful", "Arena has been loaded successfully!");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                showAlert("Load Failed", "An error occurred while loading the arena.");
            }
        }
    }

    private static void showAlert(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
