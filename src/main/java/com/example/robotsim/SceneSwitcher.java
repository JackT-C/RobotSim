package com.example.robotsim;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {
    public static void switchToHelpScene(Scene currentScene) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("help.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
