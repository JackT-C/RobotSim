package com.example.robotsim;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.io.Serializable;

public class WhiskerRobot extends Robot implements Serializable {

    private transient Polygon frontWhisker;
    private transient Polygon leftWhisker;
    private transient Polygon rightWhisker;
    private double angle; // Angle for direction control
    private boolean recentlyDetectedObstacle = false;

    public WhiskerRobot(String name, double x, double y, double size) {
        super(name, x, y, size);
        this.angle = 0; // Initial facing angle (right)

        // Initialize whiskers
        initializeWhiskers(size);
    }

    private void initializeWhiskers(double size) {
        double whiskerLength = size * 1.5; // Whisker length relative to robot size

        // Front whisker (pointing forward)
        frontWhisker = new Polygon(
                0, 0,
                -whiskerLength / 4, -whiskerLength,
                whiskerLength / 4, -whiskerLength
        );

        // Left whisker (pointing left-forward)
        leftWhisker = new Polygon(
                0, 0,
                -whiskerLength, -whiskerLength / 4,
                -whiskerLength, whiskerLength / 4
        );

        // Right whisker (pointing right-forward)
        rightWhisker = new Polygon(
                0, 0,
                whiskerLength, -whiskerLength / 4,
                whiskerLength, whiskerLength / 4
        );

        // Add whiskers to the robot
        getChildren().addAll(frontWhisker, leftWhisker, rightWhisker);

        // Default whisker colors
        frontWhisker.setFill(Color.LIGHTBLUE);
        leftWhisker.setFill(Color.GREEN);
        rightWhisker.setFill(Color.GREEN);
    }

    @Override
    public void updatePosition() {
        // Update robot position
        double radians = Math.toRadians(angle);
        double dx = Math.cos(radians) * getSpeed();
        double dy = Math.sin(radians) * getSpeed();
        setLayoutX(getLayoutX() + dx);
        setLayoutY(getLayoutY() + dy);

        // Rotate the robot and adjust whisker positions
        setRotate(angle);
        updateWhiskerPositions();
    }

    private void updateWhiskerPositions() {
        // Update whisker angles based on the robot's current angle
        frontWhisker.setRotate(angle);
        leftWhisker.setRotate(angle - 45); // 45 degrees offset for left whisker
        rightWhisker.setRotate(angle + 45); // 45 degrees offset for right whisker
    }

    public void detectObstacle(Obstacle obstacle) {
        Bounds obstacleBounds = obstacle.getBoundsInParent();

        if (frontWhisker.getBoundsInParent().intersects(obstacleBounds)) {
            avoidObstacle("front");
        } else if (leftWhisker.getBoundsInParent().intersects(obstacleBounds)) {
            avoidObstacle("left");
        } else if (rightWhisker.getBoundsInParent().intersects(obstacleBounds)) {
            avoidObstacle("right");
        }
    }

    private void avoidObstacle(String direction) {
        if (!recentlyDetectedObstacle) {
            switch (direction) {
                case "front" -> {
                    frontWhisker.setFill(Color.RED);
                    angle += 30; // Turn right to avoid the obstacle
                }
                case "left" -> {
                    leftWhisker.setFill(Color.RED);
                    angle += 15; // Turn slightly right
                }
                case "right" -> {
                    rightWhisker.setFill(Color.RED);
                    angle -= 15; // Turn slightly left
                }
            }

            normalizeAngle();
            recentlyDetectedObstacle = true;

            // Reset detection after a short delay
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    resetWhiskerColors();
                    recentlyDetectedObstacle = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void resetWhiskerColors() {
        frontWhisker.setFill(Color.LIGHTBLUE);
        leftWhisker.setFill(Color.GREEN);
        rightWhisker.setFill(Color.GREEN);
    }

    // Normalize the angle to stay within the [0, 360) range
    private void normalizeAngle() {
        angle = (angle + 360) % 360;
    }

    // Bounce methods for handling wall collisions
    @Override
    public void bounceHorizontally() {
        angle = 180 - angle; // Reverse horizontal direction
        normalizeAngle();
        resetWhiskerColors();
    }

    @Override
    public void bounceVertically() {
        angle = -angle; // Reverse vertical direction
        normalizeAngle();
        resetWhiskerColors();
    }

    // Getter methods for the whiskers
    public Node getFrontWhisker() {
        return frontWhisker;
    }

    public Node getLeftWhisker() {
        return leftWhisker;
    }

    public Node getRightWhisker() {
        return rightWhisker;
    }



}
