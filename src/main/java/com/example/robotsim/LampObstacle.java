package com.example.robotsim;

public class LampObstacle extends Obstacle {

    public LampObstacle(double x, double y, double size) {
        super(x, y, "Obstacle1.png", size);
        this.setType("Lamp");
    }

    @Override
    public void handleCollision(Robot robot) {
        // Lamp-specific collision behavior: Slows down the robot
        robot.setSpeed(robot.getSpeed() * 0.5); // Halve the robot's speed
    }
}
