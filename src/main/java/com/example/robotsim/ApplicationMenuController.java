package com.example.robotsim;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * The ApplicationMenuController class handles the logic for the application menu,
 * such as minimizing the window, toggling fullscreen mode, closing the application,
 * and switching between scenes.
 */
public class ApplicationMenuController {

    @FXML
    private AnchorPane mainPane;  // The main pane of the application, used for layout and scene transitions

    private boolean isFullscreen = false;  // Flag to track the fullscreen state

    /**
     * Minimizes the application window when called.
     */
    @FXML
    private void minimiseWindow() {
        // Get the current stage (window) and minimize it
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Toggles fullscreen mode for the application window.
     * If the window is in fullscreen mode, it will exit fullscreen.
     * If it is not in fullscreen, it will enter fullscreen.
     */
    @FXML
    private void toggleFullscreen() {
        // Get the current stage (window) and toggle fullscreen mode
        Stage stage = (Stage) mainPane.getScene().getWindow();
        isFullscreen = !isFullscreen;  // Toggle the fullscreen flag
        stage.setFullScreen(isFullscreen);  // Apply the fullscreen state to the stage
    }

    /**
     * Closes the application by calling Platform.exit(), which safely exits the application.
     */
    @FXML
    private void closeApplication() {
        // Exit the application
        Platform.exit();
    }

    /**
     * Shows an alert for loading a file, retrieves the ArenaController, and loads arena configuration.
     * In case of an error, an error alert is shown.
     */
    @FXML
    private void showFileAlert() {
        try {
            // Switch to the Arena scene
            SceneSwitcher.switchToArenaScene(mainPane.getScene());

            // Retrieve the ArenaController to access the arena configuration and elements
            ArenaController arenaController = SceneSwitcher.getArenaController();

            if (arenaController != null) {
                // Initialize the ArenaFileHandler with robots, obstacles, and arena pane
                ArenaFileHandler fileHandler = new ArenaFileHandler(arenaController.getRobots(), arenaController.getObstacles(), arenaController.getArenaPane(), arenaController);

                // Load the arena configuration from the file
                fileHandler.loadArena();
            } else {
                // Throw an error if ArenaController is not initialized
                throw new NullPointerException("ArenaController is not initialized.");
            }

        } catch (IOException e) {
            // Show an error dialog if loading the arena fails
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Load Error");
            alert.setHeaderText("Failed to Load Arena");
            alert.setContentText("An error occurred while loading the arena: " + e.getMessage());
            alert.showAndWait();  // Show the error alert
        }
    }

    /**
     * Switches to the Help scene when called.
     * Displays instructions or other help-related information for the user.
     */
    @FXML
    private void showHelp() {
        // Switch to the Help scene
        SceneSwitcher.switchToHelpScene(mainPane.getScene());
    }

    /**
     * Starts the simulation by switching to the arena scene.
     * This begins the simulation of the robots and the arena.
     */
    @FXML
    private void startSimulation() {
        // Switch to the Arena scene to start the simulation
        SceneSwitcher.switchToArenaScene(mainPane.getScene());
    }
}
