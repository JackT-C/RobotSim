package com.example.robotsim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class LampObstacle extends Obstacle {

    public LampObstacle(double x, double y, double size) {
        super(x, y, "Obstacle1.png", size);
        this.setType("Lamp");
    }

    @Override
    public void handleCollision(Robot robot) {
        // Logic to make the lamp "fall over"
        this.setRotate(90); // Example: Rotate the lamp to show it's fallen
        new Timeline(new KeyFrame(Duration.seconds(5), e -> this.setRotate(0))).play(); // Revert after 5 seconds
    }

}
