package com.example.robotsim;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

public class UserControlledRobot extends Robot {

    public UserControlledRobot(String name, double x, double y, double size) {
        super(name, x, y, size);

        // Remove the default image from the parent class and add a new one
        getChildren().clear();
        ImageView imageView = new ImageView(new Image(SensorRobot.class.getResourceAsStream("/Images/userControlledRobot.png")));
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        getChildren().add(imageView);

        // Set mouse click event to show an alert
        setOnMouseClicked(event -> {
            showAlert();
            requestFocus(); // Ensure the robot has focus to listen for key events
        });

        // Listen for key press events (W, A, S, D)
        setOnKeyPressed(this::handleKeyPress);
    }

    // Method to show alert when the robot is clicked
    private void showAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Robot Control");
        alert.setHeaderText("You can now move the robot!");
        alert.setContentText("Use W, A, S, D to move the robot around.");
        alert.showAndWait();
    }

    // Handle key press events to move the robot
    private void handleKeyPress(KeyEvent event) {
        double speed = getSpeed(); // Get the current speed of the robot

        if (event.getCode() == KeyCode.W) {
            setLayoutY(getLayoutY() - speed); // Move up (W)
        } else if (event.getCode() == KeyCode.A) {
            setLayoutX(getLayoutX() - speed); // Move left (A)
        } else if (event.getCode() == KeyCode.S) {
            setLayoutY(getLayoutY() + speed); // Move down (S)
        } else if (event.getCode() == KeyCode.D) {
            setLayoutX(getLayoutX() + speed); // Move right (D)
        }
    }

    // Ensure the robot has focus to receive keyboard events
    public void enableKeyboardFocus() {
        setOnMouseClicked(event -> {
            showAlert();
            requestFocus(); // Automatically request focus when clicked
        });
    }
}
