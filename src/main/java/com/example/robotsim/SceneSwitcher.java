package com.example.robotsim;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utility class for managing scene switching in the Robot Simulation application.
 * Provides methods to switch between different scenes, such as the help scene and arena scene.
 */
public class SceneSwitcher {

    /**
     * Static reference to the {@link ArenaController} instance.
     * This ensures a single shared reference across the application.
     */
    private static ArenaController arenaController = null;

    /**
     * Switches the current scene to the help scene.
     *
     * @param currentScene The current {@link Scene} from which the switch is initiated.
     */
    public static void switchToHelpScene(Scene currentScene) {
        try {
            // Load the help scene FXML file
            FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("help.fxml"));
            Parent root = loader.load();

            // Set the new scene on the current stage
            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            // Print stack trace in case of errors during loading
            e.printStackTrace();
        }
    }

    /**
     * Switches the current scene to the arena scene and retrieves its controller.
     *
     * @param currentScene The current {@link Scene} from which the switch is initiated.
     */
    public static void switchToArenaScene(Scene currentScene) {
        try {
            // Load the arena scene FXML file
            FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("Arena.fxml"));
            Parent root = loader.load();

            // Set the static reference to ArenaController
            arenaController = loader.getController();

            // Set the new scene on the current stage
            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            // Print stack trace in case of errors during loading
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the static reference to the {@link ArenaController}.
     * This method ensures a shared controller instance for managing arena-related operations.
     *
     * @return The {@link ArenaController} instance, or {@code null} if not yet initialized.
     */
    public static ArenaController getArenaController() {
        return arenaController;
    }
}
