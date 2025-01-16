package com.example.robotsim;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UserControlledRobot extends Robot{
    public UserControlledRobot(String name, double x, double y, double size) {
        super(name, x, y, size);
        // Remove the default image from the parent class and add a new one
        getChildren().clear();
        ImageView imageView = new ImageView(new Image(SensorRobot.class.getResourceAsStream("/Images/userControlledRobot.png")));
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        getChildren().add(imageView);
    }
}
