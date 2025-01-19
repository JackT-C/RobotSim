    package com.example.robotsim;

    import javafx.scene.Node;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.Pane;
    import javafx.scene.paint.Color;
    import javafx.scene.shape.Polygon;
    import java.util.ArrayList;
    import java.util.List;

    /**
     * The SensorRobot class extends the Robot class to represent a robot equipped with a sensor to detect obstacles.
     * It features a cone beam that visually represents the robot's sensor detection range.
     */

    public class SensorRobot extends Robot {
        private Polygon coneBeam; // Sensor cone
        private boolean recentlyDetectedObstacle = false; // Flag to prevent immediate re-detection

        /**
         * Constructs a SensorRobot with the specified name, position (x, y), and size.
         * It also initializes the robot's visual representation and sensor cone.
         *
         * @param name The name of the robot.
         * @param x The X-coordinate of the robot's position.
         * @param y The Y-coordinate of the robot's position.
         * @param size The size of the robot (both width and height).
         */
        public SensorRobot(String name, double x, double y, double size) {
            super(name, x, y, size);

            // Clear default image and set custom image for SensorRobot
            getChildren().clear();
            ImageView imageView = new ImageView(new Image(SensorRobot.class.getResource("/Images/sensorRobot.png").toExternalForm()));
            imageView.setFitWidth(getRobotWidth());
            imageView.setFitHeight(getRobotHeight());
            getChildren().add(imageView);

            // Create and configure the sensor cone
            coneBeam = new Polygon();
            coneBeam.setFill(Color.GREEN);
            coneBeam.setOpacity(0.7);

            // Set the shape of the cone
            double coneWidth = getRobotWidth() * 0.6;
            double coneHeight = getRobotHeight() * 1.2;
            coneBeam.getPoints().addAll(
                    0.0, 0.0, // Tip of the cone
                    -coneWidth / 2, coneHeight, // Bottom-left point
                    coneWidth / 2, coneHeight  // Bottom-right point
            );

            // Add the cone to the robot's display
            getChildren().add(coneBeam);

            // Align the cone with the robot's direction
            updateConePosition();
        }

        /**
         * Updates the robot's position by calling the parent class's method and adjusting the position of the sensor cone.
         * It also checks for any collisions with interactable objects and avoids obstacles by adjusting direction.
         */
        @Override
        public void updatePosition() {
            super.updatePosition(); // Update the robot's position

            // Update cone position relative to the robot's new position and direction
            updateConePosition();

            // Check for collisions in the scene with obstacles
            Pane parent = (Pane) getParent();
            if (parent != null) {
                List<Node> childrenCopy = new ArrayList<>(parent.getChildren());

                for (Node child : childrenCopy) {
                    if (child != this && child != coneBeam && isInteractable(child)) {
                        if (getBoundsInParent().intersects(child.getBoundsInParent())) {
                            // Collision detected, change cone color to red and steer away
                            coneBeam.setFill(Color.RED);
                            avoidObstacle();
                            break; // Avoid processing multiple objects simultaneously
                        }
                    }
                }
            }
        }

        /**
         * Updates the position of the sensor cone based on the robot's direction.
         * The cone is placed at the front of the robot, with the tip aligned with the robot's forward direction.
         */
        private void updateConePosition() {
            // Convert robot's direction to radians
            double radians = Math.toRadians(getDirection());

            // Calculate the offset to position the cone at the front of the robot
            double offsetX = Math.cos(radians) * (getRobotHeight() * 0.5); // Forward by half robot height
            double offsetY = Math.sin(radians) * (getRobotHeight() * 0.5); // Forward by half robot height

            // Set the cone's position relative to the robot
            coneBeam.setTranslateX(offsetX);
            coneBeam.setTranslateY(offsetY);

            // Rotate the cone so its tip aligns with the robot's forward direction
            coneBeam.setRotate(getDirection() - 90); // Adjust for cone's inward orientation
        }

        /**
         * Defines the criteria for interactable objects in the scene. The robot can interact with objects such as obstacles.
         *
         * @param node The node to check for interactability.
         * @return true if the node is interactable, false otherwise.
         */
        private boolean isInteractable(Node node) {
            return node != null && !(node instanceof SensorRobot); // Exclude other SensorRobots from interaction
        }

        /**
         * Avoids obstacles by adjusting the robot's direction away from the detected object.
         * The robot's cone color is changed to red during obstacle detection to indicate an issue.
         */
        public void avoidObstacle() {
            if (!recentlyDetectedObstacle) {
                coneBeam.setFill(Color.RED); // Visual indication of obstacle detection

                // Randomize the direction adjustment within ±45°
                setDirection(getDirection() + Math.random() * 90 - 45);

                recentlyDetectedObstacle = true;

                // Reset the detection after a brief delay
                resetDetectionAfterDelay();
            }
        }

        /**
         * Resets the obstacle detection flag and the cone's color back to the default state after a brief delay.
         */
        private void resetDetectionAfterDelay() {
            recentlyDetectedObstacle = false;
            coneBeam.setFill(Color.LIGHTBLUE); // Reset cone color
        }
    }