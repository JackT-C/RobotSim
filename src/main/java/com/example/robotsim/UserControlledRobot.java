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

/**
 * The UserControlledRobot class extends the Robot class to represent a robot that can be controlled by the user.
 * It allows the user to move the robot around using the W, A, S, D keys. The robot is also clickable, showing an
 * alert with instructions on how to control the robot.
 */
public class UserControlledRobot extends Robot {

    /**
     * Constructs a UserControlledRobot with the specified name, position (x, y), and size.
     * It sets up the robot's visual appearance and adds event listeners for mouse clicks and key presses.
     *
     * @param name The name of the robot.
     * @param x The X-coordinate of the robot's position.
     * @param y The Y-coordinate of the robot's position.
     * @param size The size of the robot (both width and height).
     */
    public UserControlledRobot(String name, double x, double y, double size) {
        super(name, x, y, size);

        // Clear the default image from the parent class and add a custom image for the UserControlledRobot
        getChildren().clear();
        ImageView imageView = new ImageView(new Image(SensorRobot.class.getResourceAsStream("/Images/userControlledRobot.png")));
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        getChildren().add(imageView);

        // Set mouse click event to show an alert and request focus for key press events
        setOnMouseClicked(event -> {
            showAlert();
            requestFocus(); // Ensure the robot has focus to listen for key events
        });

        // Listen for key press events to move the robot with W, A, S, D keys
        setOnKeyPressed(this::handleKeyPress);
    }

    /**
     * Shows an alert when the robot is clicked, providing instructions for controlling the robot.
     */
    private void showAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Robot Control");
        alert.setHeaderText("You can now move the robot!");
        alert.setContentText("Use W, A, S, D to move the robot around.");
        alert.showAndWait();
    }

    /**
     * Handles key press events to move the robot.
     * The robot moves in response to the following keys:
     * - W: Move up
     * - A: Move left
     * - S: Move down
     * - D: Move right
     *
     * @param event The key event that triggered the movement.
     */
    private void handleKeyPress(KeyEvent event) {
        double speed = getSpeed(); // Get the current speed of the robot
        enableKeyboardFocus();

        // Move robot based on the key pressed
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

    /**
     * Ensures the robot has focus when clicked, so it can receive keyboard input for movement.
     */
    public void enableKeyboardFocus() {
        setOnMouseClicked(event -> {
            showAlert();
            requestFocus(); // Automatically request focus when the robot is clicked
        });
    }
}