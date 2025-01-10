package com.example.robotsim;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.util.Random;

public class Robot extends Group {
    private final Text nameText;
    private final ImageView imageView;

    public static final double DEFAULT_SIZE = 50.0;

    private double direction; // Angle in degrees
    private double speed;
    private final Random random = new Random();

    public Robot(String name, double x, double y) {
        // Load robot image
        Image robotImage = new Image(getClass().getResource("/Images/robot.png").toExternalForm());
        imageView = new ImageView(robotImage);
        imageView.setFitWidth(DEFAULT_SIZE);
        imageView.setFitHeight(DEFAULT_SIZE);

        // Set position
        setLayoutX(x);
        setLayoutY(y);

        // Add name display
        nameText = new Text(name);
        nameText.setStyle("-fx-font-size: 14; -fx-fill: black;");
        nameText.setX(0); // Center align name
        nameText.setY(-10); // Above the robot

        // Combine image and name
        getChildren().addAll(imageView, nameText);

        // Initialize movement parameters
        direction = random.nextDouble() * 360; // Random direction
        speed = 2 + random.nextDouble() * 2; // Random speed between 2 and 4

        // Enable drag-and-drop
        enableDrag();
    }


    // Add getters for position and name
    public double getX() {
        return getLayoutX();
    }

    public double getY() {
        return getLayoutY();
    }

    public String getName() {
        return nameText.getText();
    }

    private void handleObstacleInteraction(Robot robot, Obstacle obstacle) {
        Bounds robotBounds = robot.getBoundsInParent();
        Bounds obstacleBounds = obstacle.getBoundsInParent();

        if (robotBounds.intersects(obstacleBounds)) {
            switch (obstacle.getType()) {
                case "Lampost":
                    robot.navigateAround(obstacle);
                    break;
                case "Rock":
                    robot.jumpOver(obstacle);
                    break;
                case "Lake":
                    robot.navigateAround(obstacle);
                    break;
            }
        }
    }


    public void updatePosition() {
        // Convert direction to radians
        double radians = Math.toRadians(direction);

        // Calculate deltas
        double dx = Math.cos(radians) * speed;
        double dy = Math.sin(radians) * speed;

        // Update position
        setLayoutX(getLayoutX() + dx);
        setLayoutY(getLayoutY() + dy);
    }

    public void changeDirection() {
        // Adjust direction randomly within ±45 degrees
        direction += random.nextDouble() * 90 - 45;

        // Keep direction within [0, 360]
        if (direction < 0) direction += 360;
        if (direction >= 360) direction -= 360;
    }

    public void bounceHorizontally() {
        // Reverse the horizontal component of the direction
        direction = 180 - direction;

        // Keep direction within [0, 360]
        if (direction < 0) direction += 360;
    }

    public void bounceVertically() {
        // Reverse the vertical component of the direction
        direction = 360 - direction;

        // Keep direction within [0, 360]
        if (direction < 0) direction += 360;
    }

    private void enableDrag() {
        setOnMousePressed(event -> {
            event.setDragDetect(true);
            toFront(); // Bring the robot to the front during drag
        });

        setOnMouseDragged(event -> {
            setLayoutX(event.getSceneX() - imageView.getFitWidth() / 2);
            setLayoutY(event.getSceneY() - imageView.getFitHeight() / 2);
        });
    }

    public void navigateAround(Obstacle obstacle) {
        // Logic to navigate around the obstacle
    }

    public void jumpOver(Obstacle obstacle) {
        // Logic to simulate jumping over the obstacle
    }
}
