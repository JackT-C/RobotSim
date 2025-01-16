package com.example.robotsim;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.Serializable;

public abstract class Obstacle extends ImageView implements Serializable {
    private String type;
    private double size;
    private transient double x, y;
    private double mouseX;
    private double mouseY;

    public Obstacle(double x, double y, String imagePath, double size) {
        super(new Image(Obstacle.class.getResourceAsStream("/Images/" + imagePath))); // Adjusted to load from resources
        this.setX(x);
        this.setY(y);
        this.setFitWidth(size);
        this.setFitHeight(size);
        this.type = "Static";

        // Enable dragging
        enableDrag();
    }

    private void enableDrag() {
        this.setOnMousePressed(event -> {
            mouseX = event.getSceneX() - getX();
            mouseY = event.getSceneY() - getY();
        });

        this.setOnMouseDragged(event -> {
            setX(event.getSceneX() - mouseX);
            setY(event.getSceneY() - mouseY);
        });
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    // Placeholder for handling collisions (to be overridden by subclasses)
    public abstract void handleCollision(Robot robot);
}
