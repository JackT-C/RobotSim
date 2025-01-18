package com.example.robotsim;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import java.util.ArrayList;
import java.util.List;

public class PredatorRobot extends Robot {

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
            Robot otherRobot = (Robot) object;
            Pane parent = (Pane) getParent();
            if (parent != null && parent.getChildren().contains(otherRobot)) {
                parent.getChildren().remove(otherRobot);
                System.out.println(getName() + " has eaten " + otherRobot.getName());
            }
        } else if (object instanceof Obstacle) {
            Obstacle obstacle = (Obstacle) object;
            Pane parent = (Pane) getParent();
            if (parent != null && parent.getChildren().contains(obstacle)) {
                parent.getChildren().remove(obstacle);
                System.out.println(getName() + " has destroyed an obstacle");
            }
        }
    }

    @Override
    public void updatePosition() {
        super.updatePosition();

        // Check if the PredatorRobot interacts with anything
        Pane parent = (Pane) getParent(); // Get the parent pane of the robot
        if (parent != null) {
            List<Node> toRemove = new ArrayList<>(); // List to keep track of nodes to remove

            // Create a copy of the children list to avoid ConcurrentModificationException
            List<Node> childrenCopy = new ArrayList<>(parent.getChildren());

            // Iterate over the copied list
            for (Node child : childrenCopy) {
                if (child != this && isInteractable(child)) {
                    if (getBoundsInParent().intersects(child.getBoundsInParent())) {
                        toRemove.add(child); // Mark objects for removal after iteration
                        interactWithObject(child); // Interact with any object that collides with the PredatorRobot
                    }
                }
            }

            // Now remove the objects that collided after the iteration
            parent.getChildren().removeAll(toRemove);
        }
    }

    // Helper method to determine if the object is interactable (e.g., not the text area)
    private boolean isInteractable(Node object) {
        return object instanceof Robot || object instanceof Obstacle;
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
