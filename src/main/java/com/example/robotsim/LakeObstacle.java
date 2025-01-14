package com.example.robotsim;
//inherits behavior from Obstacle Class
public class LakeObstacle extends Obstacle{


    //Constructor same params as obstacle but with specific image
    public LakeObstacle(double x, double y, double size) {
        super(x, y, "Obstacle3.png", size);
        this.setType("Lake");
    }

    //overrides collision method, so it will be specific for Lamp interactions
    @Override
    public void handleCollision() {
        // Specific collision behavior for lakes
    }
}