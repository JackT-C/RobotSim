package com.example.robotsim;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class SensorRobot extends Robot {
    private transient Polygon frontSensor;
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

        // Add a triangular front sensor pointing inward
        frontSensor = new Polygon(
                size / 2, size / 2,   // Apex of the triangle at the center
                size / 4, size,       // Bottom-left corner
                size * 3 / 4, size    // Bottom-right corner
        );
        frontSensor.setFill(Color.GREEN);
        getChildren().add(frontSensor);
    }

    @Override
    public void updatePosition() {
        // Move in the direction of the current angle
        double radians = Math.toRadians(angle);
        double dx = Math.cos(radians) * getSpeed();
        double dy = Math.sin(radians) * getSpeed();
        setLayoutX(getLayoutX() + dx);
        setLayoutY(getLayoutY() + dy);

        // Update rotation of the robot and sensor to match the angle
        setRotate(angle);
        frontSensor.setRotate(angle);
    }

    public void avoidObstacle(Obstacle obstacle) {
        if (frontSensor.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
            if (!recentlyDetectedObstacle) {
                frontSensor.setFill(Color.RED); // Obstacle detected
                angle += 90; // Turn 90 degrees to avoid the obstacle
                normalizeAngle();
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
            frontSensor.setFill(Color.GREEN); // No obstacle detected
        }
    }

    public void detectRobot(Robot otherRobot) {
        if (this != otherRobot && frontSensor.getBoundsInParent().intersects(otherRobot.getBoundsInParent())) {
            frontSensor.setFill(Color.YELLOW); // Robot detected
            angle += 45; // Turn slightly to avoid the other robot
            normalizeAngle();
        }
    }

    @Override
    public void bounceHorizontally() {
        angle = 180 - angle; // Reverse horizontal direction
        normalizeAngle();
        frontSensor.setFill(Color.PURPLE); // Indicate wall collision
    }

    @Override
    public void bounceVertically() {
        angle = -angle; // Reverse vertical direction
        normalizeAngle();
        frontSensor.setFill(Color.PURPLE); // Indicate wall collision
    }

    private void normalizeAngle() {
        // Keep the angle within [0, 360) degrees
        angle = (angle + 360) % 360;
    }
}
