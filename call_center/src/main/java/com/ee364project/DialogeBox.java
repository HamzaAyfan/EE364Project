package com.ee364project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.util.concurrent.Phaser;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.ee364project.Fx.MainSceneController;
import com.ee364project.helpers.Utilities;
import java.util.concurrent.Phaser;
public class DialogeBox extends Thread {
    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    Stage stage = new Stage();
    VBox root = new VBox(10);

    public static LinkedList<DialogeBox> windows = new LinkedList<>();
    public DialogeBox thisWindow;
    private LinkedList<Solution> solutions;
    public static int ActiveCallNumber;
    private Call currentCall;
    private Customer caller;
    private Agent receiver;
    ProgressBar customerVoice;
    ProgressBar agentVoice;
    Timeline timeline;
    LinkedList<String> content = new LinkedList<>();
    KeyFrame kfTalking;
    KeyFrame kfTransitionalPause;
    KeyFrame kfStopTalking;
    ScrollPane scrollPane;
    boolean scrollDown = true;
    boolean scrollDownCheck = true;
    int index;
    public static int numberOfThreads = 1;
    Phaser phaser;
    private boolean stop;
    String startFrom;
    HBox hBox;
    String lastLine;

    public DialogeBox(String callNumber, Phaser phaser, Call currentCall) {
        this.currentCall = currentCall;
        this.hBox = currentCall.hbox;
        this.phaser = phaser;
        phaser.register();
        windows.add(this);

        this.openEmptyWindow(callNumber, 500, 200);
    }

    public int resumeDialogeFrom() {

        int startLength = 0;
        for (Map.Entry<String, Integer> entry : currentCall.lengthsSaved.entrySet()) {
            startLength += entry.getValue();
            Person person = currentCall.sentencesHashMap.get(entry.getKey());
            TextField textField = new TextField(person.getTag() + entry.getKey());
            textField.setEditable(false);
            Platform.runLater(() -> root.getChildren().add(textField));
            startFrom = entry.getKey();
            if (startLength >= currentCall.getTimeElapsed()) {                
                break;
            }

        }
        return startLength;
    }

    public void exit() {
        stop = true;
    }

    public void startShowing(int startLength) {
        while (currentCall.getTimeElapsed() < startLength) {
            phaser.arriveAndAwaitAdvance();
        }
        
    }

    private void pacedPrint(String sentence) {
        Person speaker = currentCall.sentencesHashMap.get(sentence);
        TextField textField = this.createTextField(speaker);
        String[] words = sentence.split("\\s+");

        for (String word : words) {
            if (scrollDown) {
                scrollPane.setVvalue(1.0);
            }
            // Test.waiting = true;
            phaser.arriveAndAwaitAdvance();
            if (stop) {
                break;
            }

            Platform.runLater(() -> textField.appendText(word + " "));
        }
    }

    private TextField createTextField(Person person) {
        TextField textField = new TextField(person.getTag());
        textField.setEditable(false);
        int identifier = 0;
        if (person instanceof Agent) {
            textField.setStyle("-fx-alignment: CENTER-RIGHT;");
        } else {
            identifier = 1;
        }
        Platform.runLater(() -> {
            root.getChildren().add(textField);
        });
        return textField;
    }

    @Override
    public void run() {
        int resume = this.resumeDialogeFrom();
        Platform.runLater(() -> stage.show());
        this.startShowing(resume);

        boolean start = false;
        Iterator<String> iterator = currentCall.lengthsSaved.keySet().iterator();

        while (iterator.hasNext() && !stop) {
            String key = iterator.next();
            if (start) {
                pacedPrint(key);
            }
            if (startFrom.equals(key)) {
                start = true;
            }
        }
        Platform.runLater(() -> stage.close());

        phaser.arriveAndDeregister();
        windows.remove(this);
    }

    public void openEmptyWindow(String windowTitle, double x, double y) {
        scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);

        agentVoice = new ProgressBar();
        agentVoice.setRotate(-90);
        VBox agentVolumeBarBox = new VBox(agentVoice);
        agentVoice.setProgress(0);
        customerVoice = new ProgressBar();
        customerVoice.setRotate(-90);
        customerVoice.setProgress(0);
        VBox customerVolumeBarBox = new VBox(customerVoice);

        agentVolumeBarBox.setAlignment(Pos.CENTER);
        customerVolumeBarBox.setAlignment(Pos.CENTER);
        HBox mainPane = new HBox(customerVolumeBarBox, scrollPane, agentVolumeBarBox);
        agentVolumeBarBox.prefWidthProperty().bind(mainPane.widthProperty().divide(5));
        agentVolumeBarBox.prefHeightProperty().bind(mainPane.heightProperty());
        customerVolumeBarBox.prefWidthProperty().bind(mainPane.widthProperty().divide(5));
        customerVolumeBarBox.prefHeightProperty().bind(mainPane.heightProperty());
        scrollPane.prefWidthProperty().bind(mainPane.widthProperty().multiply(0.6));

        scrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // Check if the scroll pane is at the bottom
                if (scrollDownCheck) {
                    scrollDown = true;
                } else {
                    scrollDown = false;

                }
                if (newValue.doubleValue() == scrollPane.getVmax()) {
                    scrollDownCheck = true;
                } else {
                    scrollDownCheck = false;
                }
            }
        });

        System.out.print(scrollDown);
        stage.setOnCloseRequest(e -> {
            this.exit();
            currentCall.getCheckBox().setSelected(false);

        });
        Scene scene = new Scene(mainPane, 700, 200);

        mainPane.prefWidthProperty().bind(scene.widthProperty());
        mainPane.prefHeightProperty().bind(scene.heightProperty());
        stage.setScene(scene);
        stage.setTitle(windowTitle);
        stage.setX(x);
        stage.setY(y);
        stage.setResizable(false);
    }

    public void closeWindow() {
        stage.hide();
    }

    public void showWindow() {
        stage.show();
    }

}