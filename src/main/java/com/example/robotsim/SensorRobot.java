package com.example.robotsim;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.io.Serializable;

public class SensorRobot extends Robot implements Serializable {
    private transient Polygon beamSensor;  // The beam sensor represented as a Polygon
    private double angle;  // Angle in degrees to determine the robot's direction
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

        // Add a beam sensor represented by a polygon (this is the beam the robot uses for detection)
        beamSensor = new Polygon(
                size / 2, 0,       // Apex of the beam pointing outward (at the front of the robot)
                size, size / 4,    // Bottom-right corner of the beam
                size, -size / 4    // Bottom-left corner of the beam
        );
        beamSensor.setFill(Color.GREEN);  // Initial color when no obstacle is detected
        getChildren().add(beamSensor);
    }

    @Override
    public void updatePosition() {
        // Move in the direction of the current beam angle
        double radians = Math.toRadians(angle);
        double dx = Math.cos(radians) * getSpeed();  // Movement in the x direction
        double dy = Math.sin(radians) * getSpeed();  // Movement in the y direction
        setLayoutX(getLayoutX() + dx);  // Update robot's x position
        setLayoutY(getLayoutY() + dy);  // Update robot's y position

        // Update rotation of the robot and beam sensor to match the angle
        setRotate(angle);
        beamSensor.setRotate(angle);
    }

    // Detect if the beam sensor intersects with any obstacles or robots
    public void detectObject(Object obj) {
        if (obj instanceof Obstacle) {
            Obstacle obstacle = (Obstacle) obj;
            if (beamSensor.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                if (!recentlyDetectedObstacle) {
                    beamSensor.setFill(Color.RED); // Obstacle detected
                    avoidObstacle();  // Call method to avoid obstacle
                    recentlyDetectedObstacle = true;

                    // Reset the detection flag after a short delay
                    new Thread(() -> {
                        try {
                            Thread.sleep(500); // Delay to prevent constant detection
                            recentlyDetectedObstacle = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } else {
                beamSensor.setFill(Color.GREEN); // No obstacle detected
            }
        }

        // Detect other robots and steer away
        if (obj instanceof Robot) {
            Robot otherRobot = (Robot) obj;
            if (this != otherRobot && beamSensor.getBoundsInParent().intersects(otherRobot.getBoundsInParent())) {
                beamSensor.setFill(Color.YELLOW); // Robot detected
                avoidRobot();  // Call method to avoid another robot
            }
        }
    }

    private void avoidObstacle() {
        // Obstacle avoidance logic: Turn 90 degrees to the left to avoid the obstacle
        angle += 90;
        normalizeAngle();  // Normalize the angle to ensure it remains within [0, 360)
    }

    private void avoidRobot() {
        // Robot avoidance logic: Turn 45 degrees to the left to avoid another robot
        angle -= 45;
        normalizeAngle();  // Normalize the angle to ensure it remains within [0, 360)
    }

    @Override
    public void bounceHorizontally() {
        angle = 180 - angle;  // Reverse horizontal direction
        normalizeAngle();
        beamSensor.setFill(Color.PURPLE);  // Indicate wall collision
    }

    @Override
    public void bounceVertically() {
        angle = -angle;  // Reverse vertical direction
        normalizeAngle();
        beamSensor.setFill(Color.PURPLE);  // Indicate wall collision
    }

    private void normalizeAngle() {
        // Keep the angle within [0, 360) degrees
        angle = (angle + 360) % 360;
    }
}
