package com.example.robotsim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * The LakeObstacle class represents a specific type of obstacle: a lake.
 * When a robot collides with the lake, it bounces off in both horizontal and vertical directions,
 * and its speed is reduced temporarily.
 */
public class LakeObstacle extends Obstacle {

    /**
     * Constructs a LakeObstacle at the specified position (x, y) with the given size.
     * It initializes the obstacle with an image (Obstacle3.png) and sets its type to "Lake".
     *
     * @param x The X-coordinate of the obstacle's position.
     * @param y The Y-coordinate of the obstacle's position.
     * @param size The size (width and height) of the obstacle.
     */
    public LakeObstacle(double x, double y, double size) {
        super(x, y, "Obstacle3.png", size);  // Load lake image
        this.setType("Lake");  // Set the obstacle type to "Lake"
    }

    /**
     * Handles the collision between the robot and the lake obstacle.
     * Upon collision, the robot bounces off in both horizontal and vertical directions,
     * and its speed is temporarily reduced by 50%.
     * After 2 seconds, the speed is reverted back to a normal value.
     *
     * @param robot The robot that collided with the lake.
     */
    @Override
    public void handleCollision(Robot robot) {
        // Bounce the robot off the lake in both horizontal and vertical directions
        robot.bounceHorizontally();
        robot.bounceVertically();

        // Reduce the robot's speed by 50% upon collision with the lake
        robot.setSpeed(robot.getSpeed() * 0.5);

        // After 2 seconds, restore the robot's speed to its normal value (3)
        new Timeline(new KeyFrame(Duration.seconds(2), e -> robot.setSpeed(3))).play();
    }
}
