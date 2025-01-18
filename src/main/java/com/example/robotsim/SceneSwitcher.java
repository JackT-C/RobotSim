package com.example.robotsim;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {

    private static ArenaController arenaController = null;  // Static reference to ArenaController

    public static void switchToHelpScene(Scene currentScene) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("help.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void switchToArenaScene(Scene currentScene) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("Arena.fxml"));
            Parent root = loader.load();

            // Ensure that ArenaController is set after loading the scene
            arenaController = loader.getController();

            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to get ArenaController reference
    public static ArenaController getArenaController() {
        return arenaController;  // Return the reference to ArenaController
    }
}
