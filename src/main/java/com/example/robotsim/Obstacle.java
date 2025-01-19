package com.example.robotsim;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The Obstacle class represents a general obstacle object in the simulation.
 * It is an abstract class that can be extended by specific types of obstacles.
 * The class provides functionality for displaying the obstacle image, resizing it,
 * and enabling dragging functionality in the scene. Subclasses must define how the
 * obstacle interacts with robots (collision handling).
 */
public abstract class Obstacle extends ImageView {
    private String type;  // Type of the obstacle (e.g., "Static", "Dynamic")
    private double size;  // Size of the obstacle (width and height)
    private ImageView imageView;  // ImageView to display the obstacle image
    private double mouseX;  // X-coordinate for mouse drag event
    private double mouseY;  // Y-coordinate for mouse drag event
    private String imagePath;  // Path to the obstacle image file

    /**
     * Constructs an Obstacle with the specified position (x, y), image path, and size.
     * It loads the image, sets the size of the obstacle, and enables drag functionality.
     *
     * @param x The X-coordinate of the obstacle's position.
     * @param y The Y-coordinate of the obstacle's position.
     * @param imagePath The file path to the image representing the obstacle.
     * @param size The size (width and height) of the obstacle.
     */
    public Obstacle(double x, double y, String imagePath, double size) {
        super(new Image(Obstacle.class.getResourceAsStream("/Images/" + imagePath)));  // Load image from path
        this.imagePath = imagePath;  // Store the image path
        this.size = size;

        setX(x);  // Set the X-coordinate of the obstacle
        setY(y);  // Set the Y-coordinate of the obstacle
        setFitWidth(size);  // Set the width of the obstacle
        setFitHeight(size);  // Set the height of the obstacle
        this.type = "Static";  // Default type is "Static"

        // Enable dragging functionality for the obstacle
        enableDrag();
    }

    /**
     * Enables dragging functionality for the obstacle.
     * The obstacle can be dragged by pressing and moving the mouse on the obstacle image.
     */
    private void enableDrag() {
        this.setOnMousePressed(event -> {
            mouseX = event.getSceneX() - getX();  // Calculate the offset between mouse position and obstacle position
            mouseY = event.getSceneY() - getY();  // Calculate the offset between mouse position and obstacle position
        });

        this.setOnMouseDragged(event -> {
            setX(event.getSceneX() - mouseX);  // Update the obstacle's X-coordinate while dragging
            setY(event.getSceneY() - mouseY);  // Update the obstacle's Y-coordinate while dragging
        });
    }

    /**
     * Returns the size (width/height) of the obstacle.
     *
     * @return The size of the obstacle.
     */
    public double getSize(){
        return size;
    }

    /**
     * Sets the type of the obstacle (e.g., "Lamp", "Lake").
     *
     * @param type The type to set for the obstacle.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the type of the obstacle (e.g., "Lamp", "Lake").
     *
     * @return The type of the obstacle.
     */
    public String getName(){
        return type;
    }

    /**
     * Abstract method for handling collisions between the obstacle and a robot.
     * This method must be implemented by subclasses to define how the obstacle reacts
     * when a robot collides with it.
     *
     * @param robot The robot that collided with the obstacle.
     */
    public abstract void handleCollision(Robot robot);
}
