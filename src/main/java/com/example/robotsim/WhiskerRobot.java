package com.example.robotsim;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * The WhiskerRobot class extends the Robot class to represent a robot that uses "whiskers" (sensor lines)
 * to detect obstacles in its environment. The whiskers are positioned in the robot's front, left, and right directions.
 * When an obstacle is detected, the robot changes its direction to avoid it.
 */
public class WhiskerRobot extends Robot {
    private transient Line frontLine;  // Line representing the front sensor whisker
    private transient Line leftLine;   // Line representing the left sensor whisker
    private transient Line rightLine;  // Line representing the right sensor whisker
    private boolean recentlyDetectedObstacle = false; // Flag to track obstacle detection
    private double offset = 35;  // Offset to move sensor lines to the right

    /**
     * Constructs a WhiskerRobot with the specified name, position (x, y), and size.
     * It removes the default image, adds a custom image for the WhiskerRobot, and creates
     * the three whisker sensor lines to detect obstacles.
     *
     * @param name The name of the robot.
     * @param x The X-coordinate of the robot's position.
     * @param y The Y-coordinate of the robot's position.
     * @param size The size of the robot (both width and height).
     */
    public WhiskerRobot(String name, double x, double y, double size) {
        super(name, x, y, size);

        // Clear the default image from the parent class and add the custom image for the whisker robot
        getChildren().clear();
        ImageView imageView = new ImageView(new Image(WhiskerRobot.class.getResource("/Images/whiskerRobot.png").toExternalForm()));
        imageView.setFitWidth(getRobotWidth());
        imageView.setFitHeight(getRobotHeight());
        getChildren().add(imageView); // Add robot image first

        // Create the three fixed sensor lines (whiskers) and add them
        createSensorLines();
        getChildren().addAll(frontLine, leftLine, rightLine); // Add whiskers on top of the robot image
    }

    /**
     * Creates and initializes the three sensor lines (whiskers) for the robot.
     * The lines are positioned in front, left, and right of the robot.
     */
    private void createSensorLines() {
        double whiskerLength = getRobotHeight() * 1.0;  // Set the whisker length based on the robot's height

        // Create front whisker (facing in the robot's forward direction)
        frontLine = new Line(0, 0, 0, -whiskerLength);
        frontLine.setStroke(Color.BLACK);
        frontLine.setStrokeWidth(2);

        // Create left whisker (facing 90 degrees to the left of the robot's forward direction)
        leftLine = new Line(0, 0, 0, -whiskerLength);
        leftLine.setStroke(Color.BLACK);
        leftLine.setStrokeWidth(2);

        // Create right whisker (facing 90 degrees to the right of the robot's forward direction)
        rightLine = new Line(0, 0, 0, -whiskerLength);
        rightLine.setStroke(Color.BLACK);
        rightLine.setStrokeWidth(2);

        // Position the whiskers relative to the robot's current position
        updateSensorPositions();
    }

    /**
     * Updates the positions of the whisker sensor lines to ensure they stay attached to the robot's current position.
     * The lines are positioned with a rightward offset relative to the robot.
     */
    private void updateSensorPositions() {
        double robotWidth = getRobotWidth();
        double robotHeight = getRobotHeight();

        // Apply offset to move whiskers to the right of the robot
        double offsetX = offset;  // Rightward offset for all whiskers

        // Update positions for each of the whiskers
        frontLine.setStartX(offsetX);
        frontLine.setStartY(0);
        frontLine.setEndX(offsetX);
        frontLine.setEndY(-robotHeight);  // Extend the front whisker in front of the robot

        leftLine.setStartX(offsetX);
        leftLine.setStartY(0);
        leftLine.setEndX(offsetX - robotWidth);  // Left whisker extending to the left of the robot
        leftLine.setEndY(0);  // Y remains constant for the left whisker

        rightLine.setStartX(offsetX);
        rightLine.setStartY(0);
        rightLine.setEndX(offsetX + robotWidth);  // Right whisker extending to the right of the robot
        rightLine.setEndY(0);  // Y remains constant for the right whisker
    }

    /**
     * Updates the position of the robot and checks for collisions using the whisker sensor lines.
     * If an obstacle is detected by any whisker, the robot attempts to avoid it.
     */
    @Override
    public void updatePosition() {
        super.updatePosition();  // Update the robot's position

        // Update the whisker sensor positions to remain attached to the robot
        updateSensorPositions();

        // Check for potential collisions with other objects in the scene
        Pane parent = (Pane) getParent();
        if (parent != null) {
            List<Node> childrenCopy = new ArrayList<>(parent.getChildren());

            for (Node child : childrenCopy) {
                if (child != this && child != frontLine && child != leftLine && child != rightLine && isInteractable(child)) {
                    // If a collision is detected by any of the whisker lines, avoid the obstacle
                    if (getBoundsInParent().intersects(child.getBoundsInParent())) {
                        avoidObstacle();
                        break;  // Avoid processing multiple collisions simultaneously
                    }
                }
            }
        }
    }

    /**
     * Determines which objects the whisker robot can interact with.
     * Prevents interaction with itself to avoid false detections.
     *
     * @param node The node to check for interaction.
     * @return true if the node is interactable, false otherwise.
     */
    private boolean isInteractable(Node node) {
        return node != null && !(node instanceof WhiskerRobot);  // Ignore interactions with other WhiskerRobots
    }

    /**
     * Initiates the process of avoiding an obstacle when detected by the whiskers.
     * This includes visual feedback by changing the whisker line colors and adjusting the robot's direction.
     */
    public void avoidObstacle() {
        if (!recentlyDetectedObstacle) {
            // Visual indication of obstacle detection by changing whisker line colors
            frontLine.setStroke(Color.RED);
            leftLine.setStroke(Color.RED);
            rightLine.setStroke(Color.RED);

            // Adjust the robot's direction to steer away from the obstacle (randomized within ±45 degrees)
            setDirection(getDirection() + Math.random() * 110 - 45);

            recentlyDetectedObstacle = true;

            // Reset the obstacle detection state after a delay
            resetDetectionAfterDelay();
        }
    }

    /**
     * Resets the obstacle detection state after a short delay (1 second).
     * The whisker line colors are reset to their original state.
     */
    private void resetDetectionAfterDelay() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);  // Wait for 1 second before resetting detection
                frontLine.setStroke(Color.BLACK);  // Reset the front whisker color
                leftLine.setStroke(Color.BLACK);   // Reset the left whisker color
                rightLine.setStroke(Color.BLACK);  // Reset the right whisker color
                recentlyDetectedObstacle = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
