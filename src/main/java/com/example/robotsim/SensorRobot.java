package com.example.robotsim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

public class SensorRobot extends Robot {
    private transient Polygon coneBeam; // Light beam represented as a cone
    private boolean recentlyDetectedObstacle = false; // Flag to prevent repeated obstacle reactions

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
        coneBeam.setOpacity(0.5);

        // Set a smaller cone shape, closer to the robot
        double coneWidth = getRobotWidth() * 0.6; // 60% of robot width
        double coneHeight = getRobotHeight() * 1.2; // 1.2x robot height
        coneBeam.getPoints().addAll(
                0.0, 0.0, // Top point of the cone
                -coneWidth / 2, coneHeight, // Bottom-left point
                coneWidth / 2, coneHeight // Bottom-right point
        );

        // Add the cone to the robot (group)
        getChildren().add(coneBeam);

        // Initially align the cone with the robot's position and direction
        updateConePosition();
    }

    @Override
    public void updatePosition() {
        // Call the parent class's method to move the robot
        super.updatePosition();

        // Ensure the cone beam moves at the same speed and stays attached to the robot
        updateConePosition();

        // Dynamically detect obstacles in the scene and adjust movement
        if (detectObstaclesInScene()) {
            avoidObstacle();
        }
    }

    private void updateConePosition() {
        // Get the robot's direction and convert to radians
        double radians = Math.toRadians(getDirection());

        // Offset the cone to keep it closer to the robot
        double offsetX = Math.cos(radians) * (getRobotHeight() * 0.5); // Half of the robot height
        double offsetY = Math.sin(radians) * (getRobotHeight() * 0.5); // Half of the robot height

        // Position the cone relative to the robot's position
        coneBeam.setTranslateX(offsetX);
        coneBeam.setTranslateY(offsetY);

        // Rotate the cone to match the robot's direction
        coneBeam.setRotate(getDirection());
    }

    public void avoidObstacle() {
        if (!recentlyDetectedObstacle) {
            // Indicate obstacle detection visually
            coneBeam.setFill(Color.RED);

            // Turn slightly to avoid the obstacle
            setDirection(getDirection() + Math.random() * 60 - 30); // Randomize turning angle

            recentlyDetectedObstacle = true;

            // Reset the detection flag after a short delay
            resetDetectionAfterDelay();
        }
    }

    private void resetDetectionAfterDelay() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
            recentlyDetectedObstacle = false;
            coneBeam.setFill(Color.LIGHTBLUE); // Reset cone color
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    private boolean detectObstaclesInScene() {
        if (getParent() == null) return false; // Ensure the robot is in a scene

        // Loop through all children of the robot's parent (simulation environment)
        for (Node node : getParent().getChildrenUnmodifiable()) {
            // Skip self and the cone beam
            if (node == this || node == coneBeam) continue;

            // Check if the cone intersects the node
            if (coneBeam.localToParent(coneBeam.getBoundsInLocal()).intersects(node.getBoundsInParent())) {
                return true; // Obstacle detected
            }
        }
        return false; // No obstacle detected
    }

    public Node getCone() {
        return coneBeam;
    }
}
