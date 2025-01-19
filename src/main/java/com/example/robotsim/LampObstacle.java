package com.example.robotsim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * The LampObstacle class represents a lamp that, when collided with, falls over and reverts to its original position after a set time.
 */
public class LampObstacle extends Obstacle {

    /**
     * Constructs a LampObstacle at the specified position (x, y) with the given size.
     * It initializes the obstacle with an image (Obstacle1.png) and sets its type to "Lamp".
     *
     * @param x The X-coordinate of the obstacle's position.
     * @param y The Y-coordinate of the obstacle's position.
     * @param size The size (width and height) of the obstacle.
     */
    public LampObstacle(double x, double y, double size) {
        super(x, y, "Obstacle1.png", size);  // Load lamp image
        this.setType("Lamp");  // Set the obstacle type to "Lamp"
    }

    /**
     * Handles the collision between the robot and the lamp obstacle.
     * Upon collision, the lamp "falls over" by rotating 90 degrees to show it's fallen.
     * After 5 seconds, the lamp reverts to its original position.
     *
     * @param robot The robot that collided with the lamp.
     */
    @Override
    public void handleCollision(Robot robot) {
        // Rotate the lamp to simulate it falling over
        this.setRotate(90);

        // After 5 seconds, restore the lamp's rotation to its original state (0 degrees)
        new Timeline(new KeyFrame(Duration.seconds(5), e -> this.setRotate(0))).play();
    }
}
