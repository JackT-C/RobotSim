package com.example.robotsim;

/**
 * The DefaultRobot class is a concrete implementation of the abstract Robot class.
 * It represents a basic robot with standard behavior, using the constructor from the Robot class
 * to initialize its name, position, and size.
 */
public class DefaultRobot extends Robot {

    /**
     * Constructs a new DefaultRobot with the specified name, position (x, y), and size.
     * This constructor delegates the initialization to the parent Robot class constructor.
     *
     * @param name The name of the robot.
     * @param x The X-coordinate of the robot's position.
     * @param y The Y-coordinate of the robot's position.
     * @param size The size of the robot (both width and height).
     */
    public DefaultRobot(String name, double x, double y, double size) {
        // Call the constructor of the parent Robot class
        super(name, x, y, size);
    }
}
