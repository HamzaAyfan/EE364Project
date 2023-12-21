package com.ee364project;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Phaser;

/**
 * The {@code DialogeBox} class represents a window displaying a dialogue
 * associated with a call.
 * Each instance of this class is created for a specific call and manages the
 * display of the dialogue
 * between a customer and an agent in a graphical user interface. The dialogue
 * is presented in a scrolling
 * window, and the class is designed to handle the pacing and display of
 * sentences, keeping track of
 * the conversation's progress.
 * 
 * This class extends the {@link Thread} class, enabling concurrent execution of
 * dialogue presentation.
 * It utilizes JavaFX for the graphical user interface components.
 * 
 */

public class DialogeBox extends Thread {
    /**
     * The {@code DialogeBox} class represents a window displaying a dialogue
     * associated with a call.
     * Each instance of this class is created for a specific call and manages the
     * display of the dialogue
     * between a customer and an agent in a graphical user interface. The class
     * includes various fields
     * for managing the state and properties of the dialogue box.
     * 
     * 
     * <b>Fields:</b>
     * <ul>
     * <li>{@code stage}: creates a stage for the dialoge.</li>
     * <li>{@code root}: vbox containing texareas with words.</li>
     * <li>{@code windows}: linked list that saves refrences to each instant of
     * DialogeBox.</li>
     * <li>{@code currentCall}: reference to the call being simulated.</li>
     * <li>{@code scrollpane}: used to make vbox scrollable.</li>
     * <li>{@code scrollDown},{@code scrollDownCheck}: involved in a meachanism to
     * scroll down as long as viewer is at the bottom.</li>
     * <li>{@code stop}: used to pause and play the dialoge in syncronisity with the
     * main loop.</li>
     * <li>{@code phaser}: syncronises the appending of each word according to the
     * main loop.</li>
     * <li>{@code len}: stores the number of sentences in the dialoge.</li>
     * <li>{@code lastVisted}: stores the number of words iterated through so
     * far.</li>
     * <ul>
     */
    private Stage stage = new Stage();
    private VBox root = new VBox(10);
    private static LinkedList<DialogeBox> windows = new LinkedList<>();
    private Call currentCall;
    private ScrollPane scrollPane;
    private boolean scrollDown = true;
    private boolean scrollDownCheck = true;
    private Phaser phaser;
    private boolean stop;
    private int len;
    private int lastVisted;

    /**
     * Constructs a new instance of {@code DialogeBox} associated with a specific
     * call, using the provided call number,
     * phaser for synchronization, and the current call information. The constructor
     * initializes various fields
     * and adds the new instance to the list of active dialogue boxes.
     *
     * @param callNumber  The call number associated with the dialogue box.
     * @param phaser      The phaser used for synchronization with other threads.
     * @param currentCall The current call associated with the dialogue box.
     */
    public DialogeBox(String callNumber, Phaser phaser, Call currentCall) {
        this.currentCall = currentCall;
        this.phaser = phaser;
        phaser.register();
        windows.add(this);
        this.openEmptyWindow(callNumber, 500, 200);
    }

    /**
     * Resumes the dialogue from the last visited point, calculating the starting
     * length based on elapsed time.
     * It iterates through the sentences of the current call, updating the starting
     * length until reaching
     * the point where the elapsed time matches or exceeds the calculated starting
     * length.
     *
     * @return The calculated starting length for resuming the dialogue.
     */
    public int resumeDialogeFrom() {
        int startLength = 0;
        ArrayList<String> sentence = currentCall.getSentences();
        len = sentence.size();
        lastVisted = len;
        for (int i = 0; i < len; i++) {
            startLength += currentCall.getlengths(i);
            Person person = currentCall.getPerson(i);
            TextArea textField = createArea(person);
            String startFrom = currentCall.getSentences(i);
            Platform.runLater(() -> textField.appendText(startFrom));
            int timeElapsed = currentCall.getTimeElapsed();
            if (startLength >= timeElapsed) {
                lastVisted = i;
                break;
            }
        }
        return startLength;
    }

    /**
     * Exits the dialogue, setting the stop flag to true. This method is used to
     * signal the interruption
     * or termination of the dialogue, and subsequent calls to other methods in the
     * class may respond
     * accordingly to stop or clean up the ongoing processes.
     */
    public void exit() {
        stop = true;
    }

    /**
     * Waits until the elapsed time of the current call reaches or exceeds the
     * specified starting length.
     * This method uses a {@link Phaser} for synchronization, and it ensures that
     * the dialogue presentation
     * begins when the elapsed time aligns with the calculated starting length.
     *
     * @param startLength The calculated starting length for resuming the dialogue
     *                    presentation.
     */
    public void startShowing(int startLength) {
        while (currentCall.getTimeElapsed() < startLength) {
            phaser.arriveAndAwaitAdvance();
        }

    }

    /**
     * Prints a sentence with a specific pace, displaying the words one by one in a
     * TextArea.
     * The method creates a TextArea associated with the specified speaker and
     * iterates through
     * the words in the sentence, appending each word to the TextArea with
     * synchronization
     * using a {@link Phaser}. The scrolling behavior is controlled by the
     * {@code scrollDown} flag.
     *
     * @param sentence The sentence to be printed.
     * @param speaker  The person speaking the sentence.
     */
    private void pacedPrint(String sentence, Person speaker) {
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

    /**
     * Creates a TextArea for displaying dialogue associated with a specific person.
     * The method sets the properties of the TextArea, including font style and
     * size, and
     * whether it's editable. The font style is adjusted based on whether the person
     * is an agent.
     * The method also binds the TextArea's height to the content, ensuring proper
     * display.
     *
     * @param person The person associated with the dialogue to be displayed in the
     *               TextArea.
     * @return A TextArea configured for displaying dialogue for the specified
     *         person.
     */
    private TextArea createArea(Person person) {
        TextArea textArea = new TextArea(person.getTag());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        Font fontCustomer = Font.font("Monospaced", FontWeight.NORMAL, FontPosture.REGULAR, 12);
        textArea.setFont(fontCustomer);
        if (person instanceof Agent) {
            Font fontAgent = Font.font("Monospaced", FontWeight.NORMAL, FontPosture.REGULAR, 12);
            textArea.setFont(fontAgent);
        }
        textArea.setScrollTop(0);
        textArea.setMaxHeight(1);

        Platform.runLater(() -> {
            root.getChildren().add(textArea);
        });
        return textArea;
    }

    /**
     * Executes the thread, displaying the dialogue in the graphical user interface.
     * The method retrieves the current thread's name, shows it in the UI, resumes
     * the dialogue from
     * the last visited point, and starts showing the dialogue. It then iterates
     * through the remaining
     * sentences, printing them with a specific pace using
     * {@link #pacedPrint(String, Person)}.
     * After completing the dialogue presentation, it closes the window, updates the
     * call checkbox state,
     * and performs cleanup by deregistering from the phaser and removing itself
     * from the list of active windows.
     */
    @Override
    public void run() {
        int resume = this.resumeDialogeFrom();
        Platform.runLater(() -> stage.show());
        this.startShowing(resume);
        CheckBox checkBox = currentCall.getCheckBox();
        for (int i = lastVisted + 1; i < len; i++) {
            String sentence = currentCall.getSentences(i);
            Person person = currentCall.getPerson(i);
            pacedPrint(sentence, person);
        }
        Platform.runLater(() -> {
            stage.close();
            checkBox.setSelected(false);
        });
        phaser.arriveAndDeregister();
        windows.remove(this);
        HashMap<CheckBox, DialogeBox> checkBoxLink = Call.getLinkBetweenCheckBoxesAndDialoge();
        checkBoxLink.remove(checkBox);
    }

    /**
     * Opens an empty window for displaying the dialogue. The window is configured
     * with a ScrollPane,
     * and its properties, such as title, size, and position, are set based on the
     * provided parameters.
     * The ScrollPane is linked to the root container, and its vertical scroll
     * position is monitored
     * to determine whether scrolling is at the bottom. The method also sets up an
     * event handler for
     * the window's close request, triggering the exit process and updating the
     * associated call checkbox state.
     *
     * @param windowTitle The title of the window.
     * @param x           The x-coordinate of the window.
     * @param y           The y-coordinate of the window.
     */
    public void openEmptyWindow(String windowTitle, double x, double y) {
        scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        DoubleProperty vBoxScrollPosition = scrollPane.vvalueProperty();
        vBoxScrollPosition.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // Check if the scroll pane is at the bottom
                if (scrollDownCheck) {
                    scrollDown = true;
                } else {
                    scrollDown = false;

                }
                double value = newValue.doubleValue();
                double MaxValue = scrollPane.getVmax();
                if (value == MaxValue) {
                    scrollDownCheck = true;
                } else {
                    scrollDownCheck = false;
                }
            }
        });
        /**
         * unchecks the checkbox when the window is closed
         */
        stage.setOnCloseRequest(e -> {
            this.exit();
            CheckBox checkBox = currentCall.getCheckBox();
            checkBox.setSelected(false);
        });
        Scene scene = new Scene(scrollPane, 700, 200);
        ReadOnlyDoubleProperty width = scene.widthProperty();
        ReadOnlyDoubleProperty height = scene.heightProperty();
        DoubleProperty bindWidth = scrollPane.prefWidthProperty();
        DoubleProperty bindHeight = scrollPane.prefHeightProperty();
        bindWidth.bind(width);
        bindHeight.bind(height);
        stage.setScene(scene);
        stage.setTitle(windowTitle);
        stage.setX(x);
        stage.setY(y);
        stage.setResizable(false);
    }

    /**
     * Closes the dialogue window, hiding it from view. This method hides the
     * graphical user interface
     * window associated with the dialogue, allowing for the termination or removal
     * of the visual representation
     * of the ongoing dialogue. It does not affect the internal state of the
     * dialogue or its associated data.
     */
    public void closeWindow() {
        stage.hide();
    }

    /**
     * Displays the hidden dialogue window, making it visible to the user. This
     * method shows the graphical user interface
     * window associated with the dialogue, allowing for the presentation or
     * continuation of the visual representation
     * of the ongoing dialogue. It does not affect the internal state of the
     * dialogue or its associated data.
     */
    public void showWindow() {
        stage.show();
    }

}