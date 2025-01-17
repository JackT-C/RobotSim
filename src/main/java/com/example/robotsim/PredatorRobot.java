package com.example.robotsim;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.Node;

import java.io.Serializable;

public class PredatorRobot extends Robot implements Serializable {

    public PredatorRobot(String name, double x, double y, double size) {
        super(name, x, y, size);

        // Remove the default image from the parent class and add a new one
        getChildren().clear();
        ImageView imageView = new ImageView(new Image(PredatorRobot.class.getResourceAsStream("/Images/predatorRobot.png")));
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        getChildren().add(imageView);
    }

    // Detect interaction with another robot or obstacle
    public void interactWithObject(Node object) {
        if (object instanceof Robot) {
            // If the PredatorRobot interacts with another robot, remove the robot from the arena
            Robot otherRobot = (Robot) object;
            Pane parent = (Pane) getParent(); // Get the parent pane that contains the robots
            if (parent != null) {
                parent.getChildren().remove(otherRobot); // Remove the other robot from the arena
                System.out.println(getName() + " has eaten " + otherRobot.getName()); // Log the interaction
            }
        } else if (object instanceof Obstacle) {
            // If the PredatorRobot interacts with an obstacle, remove it from the arena
            Obstacle obstacle = (Obstacle) object;
            Pane parent = (Pane) getParent(); // Get the parent pane that contains the obstacles
            if (parent != null) {
                parent.getChildren().remove(obstacle); // Remove the obstacle from the arena
                System.out.println(getName() + " has destroyed an obstacle"); // Log the interaction
            }
        }
    }

    @Override
    public void updatePosition() {
        super.updatePosition();

        // Check if the PredatorRobot interacts with anything
        Pane parent = (Pane) getParent(); // Get the parent pane of the robot
        if (parent != null) {
            for (Node child : parent.getChildren()) {
                if (child != this) {
                    if (getBoundsInParent().intersects(child.getBoundsInParent())) {
                        interactWithObject(child); // Interact with any object that collides with the PredatorRobot
                    }
                }
            }
        }
    }

    // Override bounce methods to keep the behavior consistent
    @Override
    public void bounceHorizontally() {
        super.bounceHorizontally();
        System.out.println(getName() + " bounced horizontally");
    }

    @Override
    public void bounceVertically() {
        super.bounceVertically();
        System.out.println(getName() + " bounced vertically");
    }
}
