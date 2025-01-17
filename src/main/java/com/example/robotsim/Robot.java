package com.example.robotsim;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Random;

public abstract class Robot extends Group implements Serializable {

    private transient Text nameText;
    private static final long serialVersionUID = 1L; // Make sure this matches across all classes that are serialized/deserialized.
    private transient ImageView imageView;

    private String name; // Add a serializable field for the name
    private transient double direction; // Angle in degrees
    private transient double speed;
    private final Random random = new Random();
    private double robotWidth = 50;
    private double robotHeight = 50;
    private double x, y;

    //Constructor with parameters

    public Robot(String name, double x, double y, double size) {
        // Load robot image
        Image robotImage = new Image(getClass().getResource("/Images/robot.png").toExternalForm());
        imageView = new ImageView(robotImage);
        robotWidth = size;
        robotHeight = size;

        //set size to size given by user
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        // Set position
        setLayoutX(x);
        setLayoutY(y);

        // Add name display
        nameText = new Text(name);
        nameText.setStyle("-fx-font-size: 14; -fx-fill: black;");
        nameText.setX(0); // Center align name
        nameText.setY(-10); // Above the robot


        // Combine image and name
        getChildren().addAll(imageView, nameText);

        // Initialize movement parameters (random angle between 0 and 360 degrees)
        direction = random.nextDouble() * 360; // Random direction
        speed = 2 + random.nextDouble() * 2; // Random speed between 2 and 4

        // Enable drag-and-drop
        enableDrag();
    }



    // Add getters for position, name and size
    public double getX() {
        return getLayoutX();
    }
    public double getY() {
        return getLayoutY();
    }
    public String getName() {
        return nameText.getText();
    }
    public double getRobotWidth(){
        return robotWidth;
    }
    public double getRobotHeight(){
        return robotHeight;
    }
    public double getSpeed() {return speed;}
    public void setSpeed(double speed) {
        this.speed = Math.max(0, speed); // Ensure speed is non-negative
    }
    public void setDirection(double direction) {
        this.direction = direction % 360; // Ensure direction stays within 0-360 degrees
    }




    public void updatePosition() {
        // Convert direction to radians
        double radians = Math.toRadians(direction);

        // Calculate deltas
        double dx = Math.cos(radians) * speed;
        double dy = Math.sin(radians) * speed;

        // Update position
        setLayoutX(getLayoutX() + dx);
        setLayoutY(getLayoutY() + dy);
    }

    public void changeDirection() {
        // Adjust direction randomly within ±45 degrees
        direction += random.nextDouble() * 90 - 45;

        // Keep direction within [0, 360]
        if (direction < 0) direction += 360;
        if (direction >= 360) direction -= 360;
    }

    public void bounceHorizontally() {
        // Reverse the horizontal component of the direction
        direction = 180 - direction;

        // Keep direction within [0, 360]
        if (direction < 0) direction += 360;
    }

    public void bounceVertically() {
        // Reverse the vertical component of the direction
        direction = 360 - direction;

        // Keep direction within [0, 360]
        if (direction < 0) direction += 360;
    }

    private void enableDrag() {
        setOnMousePressed(event -> {
            event.setDragDetect(true);
            toFront(); // Bring the robot to the front during drag
        });

        setOnMouseDragged(event -> {
            setLayoutX(event.getSceneX() - imageView.getFitWidth() / 2);
            setLayoutY(event.getSceneY() - imageView.getFitHeight() / 2);
        });
    }

    //FOR SAVE LOAD FUNCTIONALITY
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // Default deserialization
        ois.defaultReadObject();

        // Reinitialize transient fields
        Image robotImage = new Image(getClass().getResource("/Images/robot.png").toExternalForm());
        imageView = new ImageView(robotImage);
        imageView.setFitWidth(robotWidth);
        imageView.setFitHeight(robotHeight);

        // Restore nameText
        nameText = new Text(name);
        nameText.setStyle("-fx-font-size: 14; -fx-fill: black;");
        nameText.setX(0);
        nameText.setY(-10);

        // Combine image and name
        getChildren().clear();
        getChildren().addAll(imageView, nameText);

        // Restore random properties (like direction and speed) if needed
        if (direction == 0) {
            direction = new Random().nextDouble() * 360;
        }
        if (speed == 0) {
            speed = 2 + new Random().nextDouble() * 2;
        }

        // Re-enable drag functionality
        enableDrag();
    }



}
