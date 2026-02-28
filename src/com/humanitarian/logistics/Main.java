package com.humanitarian.logistics;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(
                "/com/humanitarian/logistics/userinterface/collectData/problemSelectMenu/MainScreen.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("Humanitarian Logistics Project");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
