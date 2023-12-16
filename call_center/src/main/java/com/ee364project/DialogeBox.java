package com.ee364project;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.concurrent.Phaser;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DialogeBox extends Thread {
    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    Stage stage = new Stage();
    VBox root = new VBox(10);

    public static LinkedList<DialogeBox> windows = new LinkedList<>();
    public DialogeBox thisWindow;
    private LinkedList<Solution> solutions;
    private Call currentCall;
    private Customer caller;
    private Agent receiver;
    LinkedList<String> content = new LinkedList<>();
    ScrollPane scrollPane;
    boolean scrollDown = true;
    boolean scrollDownCheck = true;
    Phaser phaser;
    private boolean stop;
    String startFrom;
    HBox hBox;
    String lastLine;
    int len;
    int lastVisted;

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
        len = currentCall.getSentences().size();
        lastVisted = len;
        for (int i = 0; i<len ;i++ ){
            startLength += currentCall.getlengths(i);
            Person person = currentCall.getPerson(i);
            TextArea textField = createArea(person);
            String startFrom = currentCall.getSentences(i);
            Platform.runLater(() -> textField.appendText(startFrom));
            if (startLength >= currentCall.getTimeElapsed()) {  
                lastVisted = i;              
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

    private void pacedPrint(String sentence , Person speaker) {
        TextArea textField = this.createArea(speaker);
        String[] words = sentence.split("\\s+");
        for (String word : words) {
            if (scrollDown) {
                scrollPane.setVvalue(1.0);
            }
            phaser.arriveAndAwaitAdvance();
            if (stop) {
                break;
            }
            Platform.runLater(() -> textField.appendText(word + " "));
        }
    }

    // private TextField createTextField(Person person) {
    //     TextField textField = new TextField(person.getTag());
    //     textField.setEditable(false);
    //     int identifier = 0;
    //     if (person instanceof Agent) {
    //         textField.setStyle("-fx-alignment: CENTER-RIGHT;");
    //     } 
    //     Platform.runLater(() -> {
    //         root.getChildren().add(textField);
    //     });
    //     return textField;
    // }
    private TextArea createArea(Person person) {
        TextArea textArea = new TextArea(person.getTag());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setFont(Font.font("Monospaced", FontWeight.NORMAL, FontPosture.REGULAR, 12));
        if (person instanceof Agent) {            
            textArea.setFont(Font.font("Monospaced", FontWeight.BOLD, FontPosture.REGULAR, 12));
        } textArea.setScrollTop(0);
        

        textArea.prefHeightProperty().bind(Bindings.createDoubleBinding(
                () -> textArea.getFont().getSize() * Math.ceil(
                        textArea.getText().split("\\s").length / textArea.getPrefWidth()),
                textArea.textProperty(), textArea.prefWidthProperty()));

        
        Platform.runLater(() -> {
            root.getChildren().add(textArea);
        });
        return textArea;
    }

    @Override
    public void run() {
        String thread = Thread.currentThread().getName();
        Platform.runLater(()->root.getChildren().add(new TextField(thread)));
        int resume = this.resumeDialogeFrom();
        Platform.runLater(() -> stage.show());
        this.startShowing(resume);

        for(int i = lastVisted+1; i<len;i++){
            pacedPrint(currentCall.getSentences(i),currentCall.getPerson(i));
        }
        Platform.runLater(() -> {stage.close();currentCall.getCheckBox().setSelected(false);currentCall.getCheckBox().setDisable(true);});
        phaser.arriveAndDeregister();
        windows.remove(this);
        Call.linkCBtoDB.remove(currentCall.getCheckBox());
    }

    public void openEmptyWindow(String windowTitle, double x, double y) {
        scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
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

        stage.setOnCloseRequest(e -> {
            this.exit();
            currentCall.getCheckBox().setSelected(false);
        });
        Scene scene = new Scene(scrollPane, 700, 200);
        scrollPane.prefWidthProperty().bind(scene.widthProperty());
        scrollPane.prefHeightProperty().bind(scene.heightProperty());
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