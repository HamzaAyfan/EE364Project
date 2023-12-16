package com.ee364project;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.concurrent.Phaser;

public class DialogeBox extends Thread {
    private Stage stage;
    private VBox root = new VBox(10);
    public DialogeBox thisWindow;
    public Call currentCall;
    private ScrollPane scrollPane;
    private boolean scrollDown = true;
    private boolean scrollDownCheck = true;
    private Phaser phaser;
    private boolean stop;
    private int len;
    private int lastVisted;
    private int wordIndex;
    private String callNumber; 

    public DialogeBox(String callNumber, Phaser phaser, Call currentCall) {
        this.currentCall = currentCall;
        this.phaser = phaser;
        this.callNumber = callNumber;              
    }

    public void resumeDialogeFrom() {
        int startLength = 0;
        len = currentCall.getSentences().size();
        lastVisted = len;
        for (int i = 0; i<len ;i++ ){
            int temp = startLength;
            startLength += currentCall.getlengths(i);
            Person person = currentCall.getPerson(i);
            TextArea textArea = createArea(person);
            String startFrom = currentCall.getSentences(i);            
            if (startLength > currentCall.getTimeElapsed()) {  
                resumeLastPartsOfSentence(startFrom,null,(startLength-temp));              
                lastVisted = i;
                break;
            }else if(startLength == currentCall.getTimeElapsed()){
                Platform.runLater(()->textArea.appendText(startFrom));
                lastVisted = i;
                break;
            }
            Platform.runLater(() -> textArea.appendText(startFrom));                        
        }
    }
    
    private void resumeLastPartsOfSentence(String sentence, TextArea textArea, int displayUpTo) {           
        String[] words = sentence.split("\\s+");
        for (int i = 0; i < words.length;i++){            
            try{
                String wordToDisplay = words[i];
                // Platform.runLater(()-> textArea.appendText(wordToDisplay + " ")); 
            }catch(Exception e){

            }           
            
            if (wordIndex>=displayUpTo){
                phaser.arriveAndAwaitAdvance();
            }
        }
        // Platform.runLater(() -> stage.show());
    }

    public void exit() {
        stop = true;
    }

   
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
    private void pacedPrint(String sentence , Person speaker) {
        TextArea textArea = this.createArea(speaker);
        String[] words = sentence.split("\\s+");
        for (String word : words) {
            if (scrollDown) {
                scrollPane.setVvalue(1.0); 
            }            
            System.out.println(phaser); 
            phaser.arriveAndAwaitAdvance();
            System.out.println(phaser); 
            Platform.runLater(() -> textArea.appendText(word + " "));
            }
        }   

    @Override
    public void run() {
        phaser.register();          
        this.resumeDialogeFrom();
        
        for(int i = lastVisted + 1; i<len;i++){            
            pacedPrint(currentCall.getSentences(i),currentCall.getPerson(i));            
        }           
        Platform.runLater(()-> closeWindow());
        Platform.runLater(()->root.getChildren().clear());
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
            // this.exit();
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
        root.getChildren().clear();
        phaser.arriveAndDeregister();
    }

    public void showWindow(Phaser phaser) {
        stage.show();
        this.phaser = phaser;
    }

    public void createStage() {
        stage = new Stage();
        this.openEmptyWindow(callNumber, 500, 200);
    }

}