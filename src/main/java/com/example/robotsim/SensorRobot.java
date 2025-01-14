package com.example.robotsim;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class SensorRobot extends Robot{
    private Polygon frontSensor;

    public SensorRobot(String name, double x, double y, double size) {
        super(name, x, y, size);
        // Remove the default image from the parent class and add a new one
        getChildren().clear();
        ImageView imageView = new ImageView(new Image(SensorRobot.class.getResourceAsStream("/Images/sensorRobot.png")));
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        getChildren().add(imageView);

        // Add a triangular front sensor
        frontSensor = new Polygon(
                size / 2, -10, // Apex of the triangle
                -5, size / 2,  // Bottom-left corner
                size + 5, size / 2 // Bottom-right corner
        );
        frontSensor.setFill(Color.GREEN);
        getChildren().add(frontSensor);
    }



    @Override
    public void updatePosition() {
        super.updatePosition();

        // Update sensor position
        frontSensor.setTranslateX(getTranslateX());
        frontSensor.setTranslateY(getTranslateY());
    }

    public void detectObstacle(Obstacle obstacle) {
        if (frontSensor.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
            frontSensor.setFill(Color.RED); // Obstacle detected
        } else {
            frontSensor.setFill(Color.TRANSPARENT); // No obstacle detected
        }
    }

    public void detectRobot(Robot otherRobot) {
        if (this != otherRobot && frontSensor.getBoundsInParent().intersects(otherRobot.getBoundsInParent())) {
            frontSensor.setFill(Color.YELLOW); // Robot detected
        }
    }

    @Override
    public void bounceHorizontally() {
        super.bounceHorizontally();
        frontSensor.setFill(Color.PURPLE); // Indicate wall collision
        try {
            Thread.sleep(100);
            frontSensor.setFill(Color.GREEN);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void bounceVertically() {
        super.bounceVertically();
        frontSensor.setFill(Color.PURPLE); // Indicate wall collision
        try {
            Thread.sleep(100);
            frontSensor.setFill(Color.GREEN);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}