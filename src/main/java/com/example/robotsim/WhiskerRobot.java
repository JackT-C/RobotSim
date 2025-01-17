package com.example.robotsim;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.io.Serializable;

public class WhiskerRobot extends Robot implements Serializable {

    private Polygon frontWhisker;
    private Polygon leftWhisker;
    private Polygon rightWhisker;
    private double angle; // Angle for direction control
    private boolean recentlyDetectedObstacle = false;

    public WhiskerRobot(String name, double x, double y, double size) {
        super(name, x, y, size);
        this.angle = 0; // Initial facing angle (right)

        // Initialize whiskers (beams) as polygons
        initializeWhiskers(size);
    }

    // Initialize whiskers as polygons (triangular shapes for sensing)
    // Initialize whiskers as polygons (triangular shapes for sensing)
    private void initializeWhiskers(double size) {
        // Create the polygons (front, left, right whiskers)
        frontWhisker = new Polygon(0, -size / 2, size / 2, 0, 0, size / 2); // Front-facing whisker
        leftWhisker = new Polygon(0, 0, -size / 2, -size / 2, -size / 2, size / 2); // Left whisker
        rightWhisker = new Polygon(0, 0, size / 2, -size / 2, size / 2, size / 2); // Right whisker

        // Position them relative to the robot
        frontWhisker.setTranslateX(getRobotWidth() / 2);
        frontWhisker.setTranslateY(-size);
        leftWhisker.setTranslateX(-size / 2);
        leftWhisker.setTranslateY(0);
        rightWhisker.setTranslateX(size / 2);
        rightWhisker.setTranslateY(0);

        // Add whiskers to the robot group
        getChildren().addAll(frontWhisker, leftWhisker, rightWhisker);

        // Set the whisker colors to indicate sensor states
        frontWhisker.setFill(Color.LIGHTBLUE);
        leftWhisker.setFill(Color.GREEN);
        rightWhisker.setFill(Color.GREEN);
    }


    // Method to update the robot's position
    @Override
    public void updatePosition() {
        // Move robot based on current angle
        double radians = Math.toRadians(angle);
        double dx = Math.cos(radians) * getSpeed();
        double dy = Math.sin(radians) * getSpeed();
        setLayoutX(getLayoutX() + dx);
        setLayoutY(getLayoutY() + dy);

        // Update the rotation of the robot and whiskers
        setRotate(angle);
        frontWhisker.setRotate(angle);
        leftWhisker.setRotate(angle - 30);  // Left whisker offset
        rightWhisker.setRotate(angle + 30); // Right whisker offset
    }


    // Avoid the obstacle by steering the robot (this example just turns a little)
    public void avoidObstacle(Obstacle obstacle) {
        if (!recentlyDetectedObstacle) { // Only steer if a new obstacle is detected
            frontWhisker.setFill(Color.RED); // Indicate obstacle detection
            angle += 15; // Turn slightly to avoid the obstacle
            normalizeAngle();
            recentlyDetectedObstacle = true;

            // Reset the detection flag after a short delay (similar to SensorRobot)
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Delay to simulate avoiding obstacle
                    recentlyDetectedObstacle = false;
                    frontWhisker.setFill(Color.LIGHTBLUE); // Reset whisker color
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    // Normalize the angle to stay within the [0, 360) range
    private void normalizeAngle() {
        angle = (angle + 360) % 360;
    }

    public Node getFrontWhisker(){
        return frontWhisker;
    }

    public Node getLeftWhisker(){
        return leftWhisker;
    }

    public Node getRightWhisker(){
        return rightWhisker;
    }

    // Handle wall collisions (bouncing)
    @Override
    public void bounceHorizontally() {
        angle = 180 - angle; // Reverse horizontal direction
        normalizeAngle();
        frontWhisker.setFill(Color.PURPLE); // Indicate collision
    }

    @Override
    public void bounceVertically() {
        angle = -angle; // Reverse vertical direction
        normalizeAngle();
        frontWhisker.setFill(Color.PURPLE); // Indicate collision
    }
}
