package com.example.robotsim;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

public class WhiskerRobot extends Robot {
    private transient Line frontLine;
    private transient Line leftLine;
    private transient Line rightLine;
    private boolean recentlyDetectedObstacle = false;
    private double offset = 35;  // Offset to move lines to the right

    public WhiskerRobot(String name, double x, double y, double size) {
        super(name, x, y, size);

        // Remove the default image from the parent class and add a new one
        getChildren().clear();
        ImageView imageView = new ImageView(new Image(WhiskerRobot.class.getResource("/Images/whiskerRobot.png").toExternalForm()));
        imageView.setFitWidth(getRobotWidth());
        imageView.setFitHeight(getRobotHeight());
        getChildren().add(imageView); // Add robot image first

        // Create the three fixed sensor lines (whiskers)
        createSensorLines();

        // Add lines to the robot, after the robot image to ensure they are on top
        getChildren().addAll(frontLine, leftLine, rightLine);
    }

    private void createSensorLines() {
        double whiskerLength = getRobotHeight() * 1.0;  // Shortened length of the sensor lines

        // Front line (facing the robot's direction)
        frontLine = new Line(0, 0, 0, -whiskerLength);
        frontLine.setStroke(Color.BLACK);
        frontLine.setStrokeWidth(2);

        // Left line (facing 90 degrees left of the robot's direction)
        leftLine = new Line(0, 0, 0, -whiskerLength);
        leftLine.setStroke(Color.BLACK);
        leftLine.setStrokeWidth(2);

        // Right line (facing 90 degrees right of the robot's direction)
        rightLine = new Line(0, 0, 0, -whiskerLength);
        rightLine.setStroke(Color.BLACK);
        rightLine.setStrokeWidth(2);

        // Position the lines initially with the rightward offset
        updateSensorPositions();
    }

    @Override
    public void updatePosition() {
        super.updatePosition();

        // Update the whisker positions to ensure they remain attached
        updateSensorPositions();

        // Check for collisions in the scene using the sensor lines
        Pane parent = (Pane) getParent();
        if (parent != null) {
            List<Node> childrenCopy = new ArrayList<>(parent.getChildren());

            for (Node child : childrenCopy) {
                if (child != this && child != frontLine && child != leftLine && child != rightLine && isInteractable(child)) {
                    if (getBoundsInParent().intersects(child.getBoundsInParent())) {
                        // Object detected by any of the sensor lines
                        avoidObstacle();
                        break; // Avoid processing multiple objects at once
                    }
                }
            }
        }
    }

    private void updateSensorPositions() {
        // Update the positions of the lines relative to the robot's center, with an offset to the right
        double robotWidth = getRobotWidth();
        double robotHeight = getRobotHeight();

        // Apply offset to move lines to the right
        double offsetX = offset; // Rightward offset for all lines

        // Set the positions and rotations for the lines
        frontLine.setStartX(offsetX);  // Add offset to the X coordinate
        frontLine.setStartY(0);
        frontLine.setEndX(offsetX);    // Keep X constant
        frontLine.setEndY(-robotHeight); // Extend line in front of robot

        leftLine.setStartX(offsetX);  // Add offset to the X coordinate
        leftLine.setStartY(0);
        leftLine.setEndX(offsetX - robotWidth); // Left line extending to the left
        leftLine.setEndY(0); // Y remains the same

        rightLine.setStartX(offsetX);  // Add offset to the X coordinate
        rightLine.setStartY(0);
        rightLine.setEndX(offsetX + robotWidth); // Right line extending to the right
        rightLine.setEndY(0); // Y remains the same
    }

    private boolean isInteractable(Node node) {
        // Define which objects the robot can interact with
        return node != null && !(node instanceof WhiskerRobot); // Prevent interactions with itself
    }

    public void avoidObstacle() {
        if (!recentlyDetectedObstacle) {
            // Visual indication of detection by the sensor lines
            frontLine.setStroke(Color.RED);
            leftLine.setStroke(Color.RED);
            rightLine.setStroke(Color.RED);

            // Adjust direction to steer away from the obstacle
            setDirection(getDirection() + Math.random() * 110 - 45); // Randomized steering within ±45 degrees

            recentlyDetectedObstacle = true;

            // Reset obstacle detection after a short delay
            resetDetectionAfterDelay();
        }
    }

    private void resetDetectionAfterDelay() {
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Wait for 1 second before resetting
                frontLine.setStroke(Color.BLACK); // Reset line color
                leftLine.setStroke(Color.BLACK); // Reset left line color
                rightLine.setStroke(Color.BLACK); // Reset right line color
                recentlyDetectedObstacle = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
