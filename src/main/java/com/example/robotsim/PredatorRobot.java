package com.example.robotsim;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a PredatorRobot in the simulation, which consumes any robots or obstacles it collides with.
 */
public class PredatorRobot extends Robot {
    private ArenaController arenaController;

    /**
     * Constructor for PredatorRobot.
     *
     * @param name            The name of the robot.
     * @param x               The initial X-coordinate of the robot.
     * @param y               The initial Y-coordinate of the robot.
     * @param size            The size of the robot.
     * @param arenaController The ArenaController managing the simulation.
     */
    public PredatorRobot(String name, double x, double y, double size, ArenaController arenaController) {
        super(name, x, y, size);
        this.arenaController = arenaController;  // Set the ArenaController instance

        // Replace the default image with a custom image for the PredatorRobot
        getChildren().clear();
        ImageView imageView = new ImageView(new Image(PredatorRobot.class.getResourceAsStream("/Images/predatorRobot.png")));
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        getChildren().add(imageView);
    }

    /**
     * Handles interaction between the PredatorRobot and another object (Robot or Obstacle).
     *
     * @param object The object to interact with.
     */
    public void interactWithObject(Node object) {
        if (object instanceof Robot || object instanceof Obstacle) {
            Pane parent = (Pane) getParent();
            if (parent != null && parent.getChildren().contains(object)) {
                parent.getChildren().remove(object); // Remove the object from the parent pane
                arenaController.removeObject(object); // Notify the ArenaController to handle the object removal
            }
        }
    }

    /**
     * Updates the position of the PredatorRobot and checks for collisions with other objects.
     * If a collision is detected, the interacting object is consumed.
     */
    @Override
    public void updatePosition() {
        super.updatePosition();

        // Check for collisions with other objects in the parent pane
        Pane parent = (Pane) getParent(); // Get the parent pane of the robot
        if (parent != null) {
            List<Node> toRemove = new ArrayList<>(); // List to track nodes for removal

            // Create a copy of the children list to avoid concurrent modification issues
            List<Node> childrenCopy = new ArrayList<>(parent.getChildren());

            // Iterate over the copied list of children
            for (Node child : childrenCopy) {
                if (child != this && isInteractable(child)) {
                    if (getBoundsInParent().intersects(child.getBoundsInParent())) {
                        toRemove.add(child); // Mark object for removal
                        interactWithObject(child); // Handle interaction with the object
                    }
                }
            }

            // Remove all objects marked for removal
            parent.getChildren().removeAll(toRemove);
        }
    }

    /**
     * Determines if an object is interactable (can be consumed by the PredatorRobot).
     *
     * @param object The object to check.
     * @return True if the object is interactable; false otherwise.
     */
    private boolean isInteractable(Node object) {
        return object instanceof Robot || object instanceof Obstacle;
    }

    /**
     * Overrides the horizontal bounce behavior for the PredatorRobot.
     * Keeps the bounce behavior consistent with the parent class.
     */
    @Override
    public void bounceHorizontally() {
        super.bounceHorizontally();
    }

    /**
     * Overrides the vertical bounce behavior for the PredatorRobot.
     * Keeps the bounce behavior consistent with the parent class.
     */
    @Override
    public void bounceVertically() {
        super.bounceVertically();
    }
}
