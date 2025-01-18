package com.example.robotsim;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

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

        // Set cone shape (triangle pointing up initially)
        double width = getRobotWidth();
        double height = getRobotHeight() * 2; // Cone height = 2x robot height
        coneBeam.getPoints().addAll(
                0.0, 0.0, // Top point of the cone
                -width / 2, height, // Bottom-left point
                width / 2, height // Bottom-right point
        );

        // Add the cone to the robot (group)
        getChildren().add(coneBeam);

        // Position the cone at the center of the robot
        coneBeam.setTranslateX(0);
        coneBeam.setTranslateY(0);

        // Initially align the cone with the robot's direction
        updateConePosition();
    }

    @Override
    public void updatePosition() {
        // Call the parent class's method to move the robot
        super.updatePosition();

        // Update the cone's position and rotation based on the robot's new position and direction
        updateConePosition();
    }

    private void updateConePosition() {
        // Get the direction from the parent class (in degrees)
        double radians = Math.toRadians(getDirection());

        // Update the cone position: it stays attached to the robot, with a slight offset to point in the robot's direction
        double offsetX = Math.cos(radians) * getRobotHeight();  // Move the cone forward based on the robot's direction
        double offsetY = Math.sin(radians) * getRobotHeight();  // Similar vertical movement

        // Apply the offset to the cone
        coneBeam.setTranslateX(offsetX);
        coneBeam.setTranslateY(offsetY);

        // Rotate the cone to match the robot’s direction
        coneBeam.setRotate(getDirection());  // The cone should point in the same direction as the robot
    }

    public void avoidObstacle() {
        if (!recentlyDetectedObstacle) {
            coneBeam.setFill(Color.RED); // Indicate obstacle detection
            setDirection(getDirection() + 15); // Turn slightly to avoid the obstacle
            recentlyDetectedObstacle = true;

            // Reset the detection flag after a short delay
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Delay to prevent constant detection
                    recentlyDetectedObstacle = false;
                    coneBeam.setFill(Color.LIGHTBLUE); // Reset cone color
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public Node getCone() {
        return coneBeam;
    }
}
