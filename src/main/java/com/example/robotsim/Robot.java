package com.example.robotsim;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.util.Random;

/**
 * Abstract class representing a robot in the arena. Robots have movement capabilities, random attributes (direction and speed),
 * and a visual representation. This class serves as a base for all robot types, allowing them to be displayed, moved, and interacted with.
 */
public abstract class Robot extends Group {

    private Text nameText; // Text element to display the robot's name
    private ImageView imageView; // ImageView to display the robot's image

    private String name; // Name of the robot
    private double direction; // Angle in degrees (0 to 360) representing the robot's direction
    private double speed; // Speed of the robot (distance moved per update)
    private final Random random = new Random(); // Random number generator for random speed and direction
    private double robotWidth = 50; // Default width of the robot (modifiable)
    private double robotHeight = 50; // Default height of the robot (modifiable)

    /**
     * Constructs a new Robot with the specified name, position (x, y), and size.
     * Initializes the robot's appearance, position, speed, and direction.
     *
     * @param name The name of the robot.
     * @param x The X-coordinate of the robot's position.
     * @param y The Y-coordinate of the robot's position.
     * @param size The size of the robot (both width and height).
     */
    public Robot(String name, double x, double y, double size) {
        // Load robot image
        Image robotImage = new Image(getClass().getResource("/Images/robot.png").toExternalForm());
        imageView = new ImageView(robotImage);
        robotWidth = size;
        robotHeight = size;

        // Set size to size provided by user
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);

        // Set initial position of the robot
        setLayoutX(x);
        setLayoutY(y);

        // Initialize name display
        nameText = new Text(name);
        nameText.setStyle("-fx-font-size: 14; -fx-fill: black;");
        nameText.setX(0); // Center the name horizontally above the robot
        nameText.setY(-10); // Position name above the robot

        // Add the image and name to the robot's visual representation
        getChildren().addAll(imageView, nameText);

        // Initialize movement parameters with random values
        direction = random.nextDouble() * 360; // Random direction between 0 and 360 degrees
        speed = 2 + random.nextDouble() * 2; // Random speed between 2 and 4 units

        // Enable drag-and-drop functionality for the robot
        enableDrag();
    }

    // Getters for position, name, and size

    /**
     * Returns the X-coordinate of the robot's position.
     *
     * @return The X-coordinate.
     */
    public double getX() {
        return getLayoutX();
    }

    /**
     * Returns the Y-coordinate of the robot's position.
     *
     * @return The Y-coordinate.
     */
    public double getY() {
        return getLayoutY();
    }

    /**
     * Returns the name of the robot.
     *
     * @return The name of the robot.
     */
    public String getName() {
        return nameText.getText();
    }

    /**
     * Returns the width of the robot.
     *
     * @return The width of the robot.
     */
    public double getRobotWidth() {
        return robotWidth;
    }

    /**
     * Returns the height of the robot.
     *
     * @return The height of the robot.
     */
    public double getRobotHeight() {
        return robotHeight;
    }

    /**
     * Returns the speed of the robot.
     *
     * @return The speed of the robot.
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Returns the current direction of the robot in degrees.
     *
     * @return The direction of the robot.
     */
    public double getDirection() {
        return direction;
    }

    /**
     * Sets the direction of the robot.
     *
     * @param direction The new direction of the robot in degrees.
     */
    public void setDirection(double direction) {
        this.direction = direction;
    }

    /**
     * Sets the speed of the robot. Ensures that the speed is non-negative.
     *
     * @param speed The new speed of the robot.
     */
    public void setSpeed(double speed) {
        this.speed = Math.max(0, speed); // Ensure speed is non-negative
    }

    /**
     * Updates the robot's position based on its direction and speed.
     * The robot moves in the direction specified by its current heading (direction).
     */
    public void updatePosition() {
        // Convert the direction from degrees to radians
        double radians = Math.toRadians(direction);

        // Calculate the change in position based on speed and direction
        double dx = Math.cos(radians) * speed;
        double dy = Math.sin(radians) * speed;

        // Update the robot's position
        setLayoutX(getLayoutX() + dx);
        setLayoutY(getLayoutY() + dy);
    }

    /**
     * Makes the robot bounce horizontally by reversing its horizontal direction.
     */
    public void bounceHorizontally() {
        // Reverse the horizontal component of the direction
        direction = 180 - direction;

        // Ensure the direction remains within the range [0, 360]
        if (direction < 0) direction += 360;
    }

    /**
     * Makes the robot bounce vertically by reversing its vertical direction.
     */
    public void bounceVertically() {
        // Reverse the vertical component of the direction
        direction = 360 - direction;

        // Ensure the direction remains within the range [0, 360]
        if (direction < 0) direction += 360;
    }

    /**
     * Enables drag-and-drop functionality for the robot.
     * Allows the robot to be dragged with the mouse and brought to the front during dragging.
     */
    private void enableDrag() {
        setOnMousePressed(event -> {
            event.setDragDetect(true);
            toFront(); // Bring the robot to the front during dragging
        });

        setOnMouseDragged(event -> {
            // Update the robot's position during dragging
            setLayoutX(event.getSceneX() - imageView.getFitWidth() / 2);
            setLayoutY(event.getSceneY() - imageView.getFitHeight() / 2);
        });
    }
}
