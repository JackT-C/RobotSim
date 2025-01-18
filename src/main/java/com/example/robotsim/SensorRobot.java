package com.example.robotsim;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

public class SensorRobot extends Robot {
    private transient Polygon coneBeam; // Sensor cone
    private boolean recentlyDetectedObstacle = false;

    public SensorRobot(String name, double x, double y, double size) {
        super(name, x, y, size);

        // Remove the default image from the parent class and add a new one
        getChildren().clear();
        ImageView imageView = new ImageView(new Image(SensorRobot.class.getResource("/Images/sensorRobot.png").toExternalForm()));
        imageView.setFitWidth(getRobotWidth());
        imageView.setFitHeight(getRobotHeight());
        getChildren().add(imageView);

        // Create the cone beam
        coneBeam = new Polygon();
        coneBeam.setFill(Color.LIGHTBLUE);
        coneBeam.setOpacity(0.7);

        // Set cone shape
        double coneWidth = getRobotWidth() * 0.6;
        double coneHeight = getRobotHeight() * 1.2;
        coneBeam.getPoints().addAll(
                0.0, 0.0, // Tip of the cone
                -coneWidth / 2, coneHeight, // Bottom-left point
                coneWidth / 2, coneHeight  // Bottom-right point
        );

        // Add the cone to the robot
        getChildren().add(coneBeam);

        // Align the cone with the robot's position and direction
        updateConePosition();
    }

    @Override
    public void updatePosition() {
        super.updatePosition();

        // Update cone position
        updateConePosition();

        // Check for collisions in the scene
        Pane parent = (Pane) getParent();
        if (parent != null) {
            List<Node> childrenCopy = new ArrayList<>(parent.getChildren());

            for (Node child : childrenCopy) {
                if (child != this && child != coneBeam && isInteractable(child)) {
                    if (getBoundsInParent().intersects(child.getBoundsInParent())) {
                        // Object detected, steer away
                        avoidObstacle();
                        break; // Avoid processing multiple objects simultaneously
                    }
                }
            }
        }
    }

    private void updateConePosition() {
        // Convert the robot's direction to radians
        double radians = Math.toRadians(getDirection());

        // Position the cone at the front of the robot
        double offsetX = Math.cos(radians) * getRobotHeight();
        double offsetY = Math.sin(radians) * getRobotHeight();

        // Set the cone's position relative to the robot
        coneBeam.setTranslateX(offsetX);
        coneBeam.setTranslateY(offsetY);

        // Rotate the cone to match the robot's direction
        coneBeam.setRotate(getDirection());
    }

    private boolean isInteractable(Node node) {
        // Define criteria for interactable objects (can be customized)
        return node != null && !(node instanceof SensorRobot); // Example: ignore other SensorRobots
    }

    public void avoidObstacle() {
        if (!recentlyDetectedObstacle) {
            coneBeam.setFill(Color.RED); // Visual indication

            // Adjust direction to steer away from the obstacle
            setDirection(getDirection() + Math.random() * 90 - 45); // Randomized adjustment within ±45°

            recentlyDetectedObstacle = true;

            // Reset obstacle detection after a delay
            resetDetectionAfterDelay();
        }
    }

    private void resetDetectionAfterDelay() {
        recentlyDetectedObstacle = false;
        coneBeam.setFill(Color.LIGHTBLUE); // Reset cone color
    }
}