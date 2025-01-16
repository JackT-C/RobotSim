package com.example.robotsim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class RockObstacle extends Obstacle {

    public RockObstacle(double x, double y, double size) {
        super(x, y, "Obstacle2.png", size);
        this.setType("Rock");
    }

    @Override
    public void handleCollision(Robot robot) {
        // Rock-specific collision behavior: Stops the robot
        robot.setSpeed(0); // Stop the robot
        new Timeline(new KeyFrame(Duration.seconds(3), e -> robot.setSpeed(5))).play(); // Resume after 3 seconds
    }
}
