package com.example.robotsim;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the Help section of the application.
 * Handles user interactions such as navigating back to the main menu.
 */
public class HelpController {

    /**
     * Handles the action when the "Back to Menu" button is clicked.
     * It loads the main menu FXML and sets it as the current scene.
     *
     * @param event The ActionEvent triggered by the button click.
     */
    public void goBackToMenu(ActionEvent event) {
        try {
            // Load the main menu FXML file
            Parent menuRoot = FXMLLoader.load(getClass().getResource("ApplicationMenu.fxml"));

            // Get the current stage from the event's source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene with the main menu
            stage.setScene(new Scene(menuRoot));
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., show an error dialog)
        }
    }
}
