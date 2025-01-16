package com.example.robotsim;

public class RockObstacle extends Obstacle {

    public RockObstacle(double x, double y, double size) {
        super(x, y, "Obstacle2.png", size);
        this.setType("Rock");
    }

    @Override
    public void handleCollision(Robot robot) {
        // Rock-specific collision behavior: Stops the robot
        robot.setSpeed(0); // Set robot's speed to 0
    }
}
