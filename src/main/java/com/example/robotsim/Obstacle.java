package com.example.robotsim;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Obstacle extends ImageView {
    private String type;
    private double size;
    private ImageView imageView;
    private double mouseX;
    private double mouseY;
    private String imagePath;

    public Obstacle(double x, double y, String imagePath, double size) {
        super(new Image(Obstacle.class.getResourceAsStream("/Images/" + imagePath))); // Load image
        this.imagePath = imagePath; // Save image path
        this.size = size;

        setX(x);
        setY(y);
        setFitWidth(size);
        setFitHeight(size);
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



    public double getSize(){
        return size;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName(){return type;}

    // Placeholder for handling collisions (to be overridden by subclasses)
    public abstract void handleCollision(Robot robot);
}
