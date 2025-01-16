package com.example.robotsim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class LakeObstacle extends Obstacle {

    public LakeObstacle(double x, double y, double size) {
        super(x, y, "Obstacle3.png", size);
        this.setType("Lake");
    }

    @Override
    public void handleCollision(Robot robot) {
        robot.bounceHorizontally();
        robot.bounceVertically();
        robot.setSpeed(robot.getSpeed() * 0.5); // Reduce speed by 50%
        new Timeline(new KeyFrame(Duration.seconds(2), e -> robot.setSpeed(3))).play(); // Revert after 2 seconds
    }
}
