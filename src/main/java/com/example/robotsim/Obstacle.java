package com.example.robotsim;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public abstract class Obstacle extends ImageView {
    private String type;
    private double size;
    private double x, y;
    private ImageView imageView; // Make non-serializable fields transient
    private double mouseX;
    private double mouseY;
    private String imagePath; // Stores the image path for reconstruction

    public Obstacle(double x, double y, String imagePath, double size) {
        super(new Image(Obstacle.class.getResourceAsStream("/Images/" + imagePath))); // Load image
        this.imagePath = imagePath; // Save image path
        this.x = x;
        this.y = y;
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

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();

        // Reinitialize image after deserialization
        Image obstacleImage = new Image(Obstacle.class.getResourceAsStream("/Images/" + imagePath));
        setImage(obstacleImage);
        setFitWidth(size);
        setFitHeight(size);
        setX(x);
        setY(y);

        // Re-enable drag functionality
        enableDrag();
    }



    public String getType() {
        return type;
    }

    public double getSize(){
        return size;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Ensure this method is accessible
    public String getImagePath() {
        return imagePath;
    }

    // Placeholder for handling collisions (to be overridden by subclasses)
    public abstract void handleCollision(Robot robot);
}
