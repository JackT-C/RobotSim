package com.example.robotsim;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Robot extends ImageView {
    private double speed;
    private String name;

    public Robot(double x, double y) {
        setImage(new Image(getClass().getResourceAsStream("/Images/robot.png")));
        this.setX(x);
        this.setY(y);
        this.setFitWidth(50); // Set appropriate size
        this.setFitHeight(50);
        this.speed = 1.0; // Default speed
        this.name = "Robot";
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}