package com.example.robotsim;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class SensorRobot extends Robot implements Serializable {
    private transient Rectangle beam; // Light beam represented as a narrow rectangle
    private double angle; // Angle in degrees to determine the robot's direction
    private boolean recentlyDetectedObstacle = false; // Flag to prevent repeated obstacle reactions

    public SensorRobot(String name, double x, double y, double size) {
        super(name, x, y, size);
        this.angle = 0; // Default angle facing right

        // Remove the default image from the parent class and add a new one
        getChildren().clear();
        ImageView imageView = new ImageView(new Image(SensorRobot.class.getResourceAsStream("/Images/sensorRobot.png")));
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        getChildren().add(imageView);

        // Create the beam (light sensor) as a narrow rectangle
        beam = new Rectangle(size / 4, size * 2); // Beam width = 1/4 of robot, height = 2x robot size
        beam.setFill(Color.LIGHTBLUE);
        beam.setOpacity(0.5); // Slight transparency for visualization

        // Position the beam directly on top of the robot
        beam.setTranslateX((size / 2) - (beam.getWidth() / 2)); // Center the beam horizontally
        beam.setTranslateY(-beam.getHeight()); // Position the beam above the robot

        getChildren().add(beam);

        // Ensure the robot starts with the correct rotation
        setRotate(angle);
    }

    @Override
    public void updatePosition() {
        // Move in the direction of the current angle
        double radians = Math.toRadians(angle);
        double dx = Math.cos(radians) * getSpeed();
        double dy = Math.sin(radians) * getSpeed();
        setLayoutX(getLayoutX() + dx);
        setLayoutY(getLayoutY() + dy);

        // Update the rotation of the robot and the beam
        setRotate(angle);
        beam.setRotate(angle);
    }

    public void avoidObstacle(Obstacle obstacle) {
        if (!recentlyDetectedObstacle) { // Only steer if a new obstacle is detected
            beam.setFill(Color.RED); // Indicate obstacle detection
            angle += 15; // Turn slightly to avoid the obstacle
            normalizeAngle();
            recentlyDetectedObstacle = true;

            // Reset the detection flag after a short delay
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Delay to prevent constant detection
                    recentlyDetectedObstacle = false;
                    beam.setFill(Color.LIGHTBLUE); // Reset beam color
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void detectRobot(Robot otherRobot) {
        // Detect if another robot intersects the beam
        if (this != otherRobot && beam.getBoundsInParent().intersects(otherRobot.getBoundsInParent())) {
            beam.setFill(Color.YELLOW); // Indicate robot detection
            angle += 10; // Turn slightly to avoid the other robot
            normalizeAngle();
        }
    }

    @Override
    public void bounceHorizontally() {
        angle = 180 - angle; // Reverse horizontal direction
        normalizeAngle();
        beam.setFill(Color.PURPLE); // Indicate wall collision
    }

    @Override
    public void bounceVertically() {
        angle = -angle; // Reverse vertical direction
        normalizeAngle();
        beam.setFill(Color.PURPLE); // Indicate wall collision
    }

    private void normalizeAngle() {
        // Keep the angle within [0, 360) degrees
        angle = (angle + 360) % 360;
    }

    public Node getBeam() {
        return beam;
    }
}