package com.example.robotsim;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;


public class ApplicationMenuController {

    @FXML
    private AnchorPane mainPane;

    private boolean isFullscreen = false;

    @FXML
    private void minimiseWindow() {
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void toggleFullscreen() {
        Stage stage = (Stage) mainPane.getScene().getWindow();
        isFullscreen = !isFullscreen;
        stage.setFullScreen(isFullscreen);
    }

    @FXML
    private void closeApplication() {
        Platform.exit();
    }

    @FXML
    private void showFileAlert() {
        try {
            // Ensure that ArenaController is available before interacting with it
            SceneSwitcher.switchToArenaScene(mainPane.getScene());  // Switch to Arena scene
            ArenaController arenaController = SceneSwitcher.getArenaController();  // Retrieve the ArenaController

            if (arenaController != null) {
                // Access ArenaController methods like getRobots(), getObstacles(), etc.
                ArenaFileHandler fileHandler = new ArenaFileHandler(arenaController.getRobots(), arenaController.getObstacles(), arenaController.getArenaPane(),arenaController );

                // Load the arena configuration
                fileHandler.loadArena();
            } else {
                throw new NullPointerException("ArenaController is not initialized.");
            }

        } catch (IOException e) {
            // Show an error dialog if loading fails
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Load Error");
            alert.setHeaderText("Failed to Load Arena");
            alert.setContentText("An error occurred while loading the arena: " + e.getMessage());
            alert.showAndWait();
        }
    }



    @FXML
    private void showHelp() {
        // Switch scene to Help Page
        SceneSwitcher.switchToHelpScene(mainPane.getScene());
    }

    @FXML
    private void startSimulation() {
        // Switch scene to Sim Page
        SceneSwitcher.switchToArenaScene(mainPane.getScene());
    }
}
