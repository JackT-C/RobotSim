package com.example.robotsim;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class ApplicationMenuController {

    @FXML
    private AnchorPane mainPane;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private javafx.scene.control.Button FileButton, QuestionButton, startsimbutton, stopsimButton;

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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("File Options");
        alert.setHeaderText("Choose an option");
        alert.setContentText("Do you want to select save folder or load the configuration?");

        ButtonType selectButton = new ButtonType("Select");
        ButtonType loadButton = new ButtonType("Load");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(selectButton, loadButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == selectButton) {
                selectConfiguration();
            } else if (result.get() == loadButton) {
                loadConfiguration();
            }
        }
    }

    private void selectConfiguration() {

    }

    private void loadConfiguration() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Configuration");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Configuration Files", "*.config"));
        File file = fileChooser.showOpenDialog(mainPane.getScene().getWindow());
        if (file != null) {
            // Implement load logic here
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
