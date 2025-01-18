package com.example.robotsim;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

public class WhiskerRobot extends Robot {
    private Polygon frontWhisker;
    private Polygon leftWhisker;
    private Polygon rightWhisker;
    private boolean recentlyDetectedObstacle = false;

    public WhiskerRobot(String name, double x, double y, double size) {
        super(name, x, y, size);

        // Remove the default image from the parent class and add a new one
        getChildren().clear();
        ImageView imageView = new ImageView(new Image(WhiskerRobot.class.getResource("/Images/whiskerRobot.png").toExternalForm()));
        imageView.setFitWidth(getRobotWidth());
        imageView.setFitHeight(getRobotHeight());
        getChildren().add(imageView);

        // Initialize whiskers
        initializeWhiskers(size);
    }

    private void initializeWhiskers(double size) {
        double whiskerLength = size * 1.5;

        // Front whisker
        frontWhisker = new Polygon(0.0, 0.0, -whiskerLength / 4, -whiskerLength, whiskerLength / 4, -whiskerLength);
        frontWhisker.setFill(Color.LIGHTBLUE);
        frontWhisker.setOpacity(0.7);

        // Left whisker
        leftWhisker = new Polygon(0.0, 0.0, -whiskerLength, -whiskerLength / 4, -whiskerLength, whiskerLength / 4);
        leftWhisker.setFill(Color.GREEN);
        leftWhisker.setOpacity(0.7);

        // Right whisker
        rightWhisker = new Polygon(0.0, 0.0, whiskerLength, -whiskerLength / 4, whiskerLength, whiskerLength / 4);
        rightWhisker.setFill(Color.GREEN);
        rightWhisker.setOpacity(0.7);

        // Add whiskers to the robot
        getChildren().addAll(frontWhisker, leftWhisker, rightWhisker);

        // Align whiskers with the robot's direction
        updateWhiskerPositions();
    }

    @Override
    public void updatePosition() {
        super.updatePosition();

        // Update whisker positions
        updateWhiskerPositions();

        // Check for collisions in the scene
        Pane parent = (Pane) getParent();
        if (parent != null) {
            List<Node> childrenCopy = new ArrayList<>(parent.getChildren());

            for (Node child : childrenCopy) {
                if (child != this && child != frontWhisker && child != leftWhisker && child != rightWhisker && isInteractable(child)) {
                    if (getBoundsInParent().intersects(child.getBoundsInParent())) {
                        // Object detected, steer away based on whisker intersection
                        detectAndAvoidObstacle(child);
                        break;
                    }
                }
            }
        }
    }

    private void updateWhiskerPositions() {
        // Convert robot's direction to radians
        double radians = Math.toRadians(getDirection());

        // Offset whiskers relative to robot's position
        double offsetX = Math.cos(radians) * (getRobotHeight() * 0.5);
        double offsetY = Math.sin(radians) * (getRobotHeight() * 0.5);

        // Align whiskers with the robot's current direction
        frontWhisker.setTranslateX(offsetX);
        frontWhisker.setTranslateY(offsetY);
        frontWhisker.setRotate(getDirection() - 90);

        leftWhisker.setTranslateX(offsetX);
        leftWhisker.setTranslateY(offsetY);
        leftWhisker.setRotate(getDirection() - 135);

        rightWhisker.setTranslateX(offsetX);
        rightWhisker.setTranslateY(offsetY);
        rightWhisker.setRotate(getDirection() - 45);
    }

    private boolean isInteractable(Node node) {
        // Define criteria for interactable objects (can be customized)
        return node != null && !(node instanceof WhiskerRobot); // Example: ignore other WhiskerRobots
    }

    private void detectAndAvoidObstacle(Node obstacle) {
        if (!recentlyDetectedObstacle) {
            // Determine which whisker detects the obstacle
            if (frontWhisker.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                avoidObstacle("front");
            } else if (leftWhisker.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                avoidObstacle("left");
            } else if (rightWhisker.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                avoidObstacle("right");
            }
        }
    }

    private void avoidObstacle(String direction) {
        if (!recentlyDetectedObstacle) {
            switch (direction) {
                case "front" -> {
                    frontWhisker.setFill(Color.RED);
                    setDirection(getDirection() + 90); // Turn right
                }
                case "left" -> {
                    leftWhisker.setFill(Color.RED);
                    setDirection(getDirection() + 45); // Turn slightly right
                }
                case "right" -> {
                    rightWhisker.setFill(Color.RED);
                    setDirection(getDirection() - 45); // Turn slightly left
                }
            }

            normalizeAngle();
            recentlyDetectedObstacle = true;

            // Reset whisker colors and detection flag after a delay
            resetDetectionAfterDelay();
        }
    }

    private void resetDetectionAfterDelay() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                frontWhisker.setFill(Color.LIGHTBLUE);
                leftWhisker.setFill(Color.GREEN);
                rightWhisker.setFill(Color.GREEN);
                recentlyDetectedObstacle = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Normalize the angle to stay within the [0, 360) range
    private void normalizeAngle() {
        setDirection((getDirection() + 360) % 360);
    }
}
