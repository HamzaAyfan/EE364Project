package com.ee364project.Fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainFx extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Call Centre Simulation");
        primaryStage.setScene(new Scene(root, 600, 550));
        primaryStage.show();

        MainSceneController controller = loader.getController();
        controller.showYesNoDialog(primaryStage);

    } catch (Exception e) {
        e.printStackTrace(); // Print the stack trace to the console
        throw e; // Rethrow the exception
    }
}

    public static void main(String[] args) {
        launch(args);
    }

}


//"vmArgs": "--module-path \"C:\\VSCode_Java\\EE364\\javaFX\\lib\" --add-modules javafx.controls,javafx.fxml"