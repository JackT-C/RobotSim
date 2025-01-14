package com.example.robotsim;

public class RockObstacle extends Obstacle {

    public RockObstacle(double x, double y, double size) {
        super(x, y, "Obstacle2.png", size);
        this.setType("Rock");
    }

    @Override
    public void handleCollision() {
        // Specific collision behavior for rocks
        System.out.println("Collision with a rock obstacle! The robot slows down.");
    }
}
