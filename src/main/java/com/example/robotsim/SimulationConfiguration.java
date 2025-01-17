package com.example.robotsim;

import java.io.Serializable;
import java.util.List;

public class SimulationConfiguration implements Serializable {
    private List<Robot> robots;
    private List<Obstacle> obstacles;

    public List<Robot> getRobots() {
        return robots;
    }

    public void setRobots(List<Robot> robots) {
        this.robots = robots;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public void setObstacles(List<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }
}
