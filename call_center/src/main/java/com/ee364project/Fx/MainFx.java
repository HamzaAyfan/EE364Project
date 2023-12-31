package com.ee364project.Fx;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Main class for the JavaFX application.
 * 
 * @author Team 2
 */
public class MainFx extends Application {
    /**
     * The starting point of the JavaFX application.
     *
     * @param primaryStage The primary stage for the application.
     * @throws Exception If an error occurs during application initialization.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            URL fxml = getClass().getResource("MainScene.fxml");
            FXMLLoader loader = new FXMLLoader(fxml);
            Parent root = loader.load();
            Scene scene = new Scene(root);

            primaryStage.setTitle("Call Centre Simulation");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            // MainSceneController controller = loader.getController();
            // controller.showYesNoDialog(primaryStage);

            primaryStage.setOnCloseRequest(e -> {
                MainSceneController.running = false;
            });

        } catch (Exception e) {
            MainSceneController.showErrorAlert("Start up", "Failed to intialze program");
        }
    }
    /**
    * The main entry point for the JavaFX application. It launches the JavaFX application
     * by calling the {@code launch} method with the specified command-line arguments.
    *
    * @param args The command-line arguments passed to the application.
    */
    public static void main(String[] args) {
        launch(args);
    }
}