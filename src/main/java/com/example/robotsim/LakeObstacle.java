package com.example.robotsim;

public class LakeObstacle extends Obstacle {

    public LakeObstacle(double x, double y, double size) {
        super(x, y, "Obstacle3.png", size);
        this.setType("Lake");
    }

    @Override
    public void handleCollision(Robot robot) {
        // Lake-specific collision behavior: Change robot's direction randomly
        robot.setDirection(Math.random() * 360); // Randomize direction
    }
}
