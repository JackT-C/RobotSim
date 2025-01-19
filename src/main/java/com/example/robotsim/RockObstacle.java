package com.example.robotsim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * The RockObstacle class represents a specific type of obstacle: a rock.
 * This obstacle stops the robot for 3 seconds when a collision occurs,
 * and then resumes its movement.
 */
public class RockObstacle extends Obstacle {

    /**
     * Constructs a RockObstacle at the specified position (x, y) with the given size.
     * It initializes the obstacle with an image (Obstacle2.png) and sets its type to "Rock".
     *
     * @param x The X-coordinate of the obstacle's position.
     * @param y The Y-coordinate of the obstacle's position.
     * @param size The size (width and height) of the obstacle.
     */
    public RockObstacle(double x, double y, double size) {
        super(x, y, "Obstacle2.png", size);  // Load rock image
        this.setType("Rock");  // Set the obstacle type to "Rock"
    }

    /**
     * Handles the collision between the robot and the rock obstacle.
     * When a robot collides with this rock, it stops for 3 seconds and then resumes movement.
     *
     * @param robot The robot that collided with the rock.
     */
    @Override
    public void handleCollision(Robot robot) {
        // Rock-specific collision behavior: Stops the robot
        robot.setSpeed(0);  // Set the robot's speed to 0 (stop it)

        // After 3 seconds, resume the robot's movement by setting speed to 5
        new Timeline(new KeyFrame(Duration.seconds(3), e -> robot.setSpeed(5))).play();
    }
}
