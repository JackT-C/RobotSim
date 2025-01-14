package com.example.robotsim;
//inherits behavior from Obstacle Class
public class LampObstacle extends Obstacle{


    //Constructor same params as obstacle but with specific image
    public LampObstacle(double x, double y, double size) {
        super(x, y, "Obstacle1.png", size);
        this.setType("Lamp");
    }

    //overrides collision method, so it will be specific for Lamp interactions
    @Override
    public void handleCollision() {
        // Specific collision behavior for lamps
    }
}