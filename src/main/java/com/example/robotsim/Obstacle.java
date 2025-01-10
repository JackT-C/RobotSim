package com.example.robotsim;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Obstacle extends ImageView {
    private String type;

    public Obstacle(double x, double y, String imagePath) {
        // Resolve the image resource before calling the superclass constructor
        super(resolveImage(imagePath));
        this.setX(x);
        this.setY(y);
        this.setFitWidth(50); // Set appropriate size
        this.setFitHeight(50);
        this.type = "Static"; // Default type
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Helper method to resolve the image
    private static Image resolveImage(String imagePath) {
        return new Image(Obstacle.class.getResource(imagePath).toExternalForm());
    }
}
