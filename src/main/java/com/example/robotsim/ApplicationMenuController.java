package com.example.robotsim;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;

public class ApplicationMenuController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private void loadConfiguration(ActionEvent event) {
        System.out.println("Loading configuration...");
        showAlert("Load Configuration", "Feature to load configuration is under development.");
    }

    @FXML
    private void saveConfiguration(ActionEvent event) {
        System.out.println("Saving configuration...");
        showAlert("Save Configuration", "Feature to save configuration is under development.");
    }

    @FXML
    private void exitApplication(ActionEvent event) {
        System.out.println("Exiting application...");
        System.exit(0);
    }

    @FXML
    private void startSimulation(ActionEvent event) {
        System.out.println("Starting simulation...");
    }

    @FXML
    private void pauseSimulation(ActionEvent event) {
        System.out.println("Pausing simulation...");
    }

    @FXML
    private void resetSimulation(ActionEvent event) {
        System.out.println("Resetting simulation...");
    }

    @FXML
    private void showAbout(ActionEvent event) {
        showAlert("About", "Robot Simulation Application\nVersion 1.0");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
