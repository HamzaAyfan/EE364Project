package com.ee364project.Fx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadPoolExecutor;

import com.ee364project.Agent;
import com.ee364project.Call;
import com.ee364project.CallCenter;
import com.ee364project.Customer;
import com.ee364project.DialogeBox;
import com.ee364project.Department;
import com.ee364project.HasData;
import com.ee364project.Problem;
import com.ee364project.Timekeeper;
import com.ee364project.file_manage.Csv;
import com.ee364project.file_manage.Zip;
import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

/**
 * The {@code MainSceneController} class manages the GUI of the application.
 * It holds references to agents, customers, problems, and departments, as well
 * as
 * handles recent files, thread synchronization, and thread execution.
 *
 * {@code agents}: an array of Agent objects.
 * {@code customers}: an array of Customer objects.
 * {@code problems}: an array of objects implementing the HasData interface
 * representing problems.
 * {@code departments}: an array of objects implementing the HasData interface
 * representing departments.
 * {@code recentFiles}: a list of recently accessed files.
 * {@code RECENT_FILES_FILE}: the file name to store recent files information.
 * {@code MAX_RECENT_FILES}: the maximum number of recent files to keep track
 * of.
 * {@code phaser}: a Phaser for synchronization.
 * {@code pausePlay}: a thread used for pausing and playing the simulation.
 * {@code endThread}: a flag indicating the end of the simulation thread.
 * {@code executor}: a ThreadPoolExecutor for managing threads.
 * {@code customerImage}: the image for representing customers.
 * {@code agentImage}: the image for representing agents.
 * {@code callImage}: the image for representing calls.
 * {@code callCenter}: the reference to the CallCenter.
 * {@code timerTimeline}: the timeline for handling timer events.
 * {@code callCount}: counts call number to display in GUI
 * {@code ActiveText}: a Text component displaying information about active
 * elements.
 * {@code AgentVbox}: a VBox for organizing Agent-related components.
 * {@code MenBar}: the main menu bar.
 * {@code saveAsbtn}: a MenuItem for the "Save As" option.
 * {@code MenNew}: a MenuItem for the "New" option.
 * {@code MenPasue}: a MenuItem for the "Pause" option.
 * {@code MenPlay}: a MenuItem for the "Play" option.
 * {@code MenStart}: a MenuItem for the "Start" option.
 * {@code MenuOld}: a MenuItem for the "Old" option.
 * {@code OpenRecentMenu}: a Menu for displaying recent files.
 * {@code anchorPane}: an AnchorPane for organizing components.
 * {@code flowPane}: a FlowPane for organizing components.
 * {@code timeer}: a Text component displaying information about the timer.
 * {@code checkPoint}: a CheckBox component.
 * {@code phase2MenItem}: a CheckMenuItem for the "Phase 2" option.
 * {@code Vox}: a VBox for organizing components.
 * {@code CallVbox}: a VBox for organizing components related to calls.
 * {@code connected}: a Button component.
 */
public class MainSceneController {
    private Agent[] agents;
    private Customer[] customers;
    private HasData[] problems;
    private HasData[] departments;

    private ArrayList<File> recentFiles = new ArrayList<>();
    private static final String RECENT_FILES_FILE = "recent_files.txt";
    private static final int MAX_RECENT_FILES = 5;
    private static Phaser phaser = new Phaser(0);
    private Thread pausePlay = new Thread();
    /**
     * Indicates whehter to end the current thread or not.
     */
    public static boolean endThread;
    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private ChangeListener<Number> timePropertyListener;
    private static long callCount;
    private int lastMaxIndex = -1;

    // initializing the images that is going to be used for loading the main stage
    private Image customerImage = new Image("com\\ee364project\\Fx\\resources\\user.png");
    private Image agentImage = new Image("com\\ee364project\\Fx\\resources\\agent.png");
    private Image callImage = new Image("com\\ee364project\\Fx\\resources\\green.jpg");
    private CallCenter callCenter;
    private Timeline timerTimeline;

    /**
     * Indicate whehter the UI is running or not.
     */
    public static boolean running = true;

    @FXML
    private Text ActiveText;
    @FXML
    private VBox AgentVbox;
    @FXML
    private Menu MenBar;
    @FXML
    private MenuItem saveAsbtn;
    @FXML
    private MenuItem MenNew;
    @FXML
    private MenuItem MenPasue;
    @FXML
    private MenuItem MenPlay;
    @FXML
    private MenuItem MenStart;
    @FXML
    private MenuItem MenuOld;
    @FXML
    private Menu OpenRecentMenu;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private VBox flowPane;
    @FXML
    private Text timeer;
    @FXML
    private CheckBox checkPoint;
    @FXML
    private CheckMenuItem phase2MenItem;
    @FXML
    private VBox Vox;
    /**
     * The VBox for the call.
     */
    @FXML
    public VBox CallVbox;
    @FXML
    private Button connected;
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, String> customerColumn;
    @FXML
    private TableColumn<Customer, Number> AWTcolumn;
    @FXML
    private TableColumn<Customer, Number> MAXTcolumn;

    /**
     * Initializes the main scene components and sets up event handlers.
     * It sets references and configurations for various UI elements, such as
     * buttons, panes, and timelines.
     * Additionally, it configures the checkbox to add or remove a time property
     * listener based on its state.
     */
    @FXML
    public void initialize() {
        // Set cell value factory to map the Customer objects to the corresponding
        // property
        customerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShortInfo()));
        // setting all the important references and spaces for panes internal look
        MenPasue.setOnAction(e -> {
            this.pause();
        });
        MenPasue.setDisable(true);
        MenPlay.setDisable(true);
        MenPasue.setDisable(true);
        Call.vBox = CallVbox;
        Call.phaser = phaser;
        CallVbox.setSpacing(4);
        AgentVbox.setSpacing(4);
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), this::updateTimer));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        BooleanProperty checkpointBooleanProperty = checkPoint.selectedProperty();
        checkpointBooleanProperty.addListener((checkbox, oldValue, newValue) -> {
            if (newValue) {
                // If the checkbox is checked, add the number property listener
                addTimePropertyListener();
            } else {
                // If the checkbox is unchecked, remove the number property listener
                removeTimePropertyListener();
            }
        });
        try {
            // calling the methods below would load the recent files once the program runs
            loadRecentFiles();
        } catch (IOException e) {
            return;
        }
    }

    /**
     * Adds a listener to the time property of the checkpoint.
     * The listener checks if the time value is a multiple of 50 and, if true,
     * triggers the execution of the {@code checkPoint} method on the JavaFX
     * Application Thread.
     * The listener is implemented using a lambda expression.
     */
    private void addTimePropertyListener() {
        // Add a listener to the number property using a lambda expression
        timePropertyListener = (observable, oldValue, newValue) -> {
            // Check if the number is a multiple of 50
            int timeElapsed = newValue.intValue();
            if (timeElapsed % 50 == 0) {
                // Show the dialog on the next iteration of the JavaFX Application Thread
                Platform.runLater(() -> checkPoint());
            }
        };
        // Add the listener to the number property
        SimpleIntegerProperty timeProperty = Timekeeper.getTimeProperty();
        timeProperty.addListener(timePropertyListener);
    }

    /**
     * Removes the previously added listener from the time property of the
     * Timekeeper class. This is when the checkpoint is unchecked
     * If no listener was added before, this method has no effect.
     */
    private void removeTimePropertyListener() {
        // Remove the listener from the number property
        if (timePropertyListener != null) {
            SimpleIntegerProperty timeProperty = Timekeeper.getTimeProperty();
            timeProperty.removeListener(timePropertyListener);
        }
    }

    /**
     * Pauses the simulation, disabling the pause button and enabling the play
     * button.
     * The timeline controlling the timer is paused, and a separate thread is
     * started to wait
     * for the play button to be clicked. Once the play button is clicked, the
     * simulation resumes,
     * re-enabling the pause button and disabling the play button. This is done by
     * deresitering the
     * thread from the phaser and ending the loop
     */
    public void pause() {
        MenPasue.setDisable(true);
        MenPlay.setDisable(false);
        timerTimeline.pause();
        pausePlay = new Thread(() -> {
            phaser.register();
            MenPlay.setOnAction(e -> {
                phaser.arriveAndDeregister();
                endThread = true;
                MenPasue.setDisable(false);
                MenPlay.setDisable(true);
                timerTimeline.play();
            });
            while (!endThread) {
            }
            endThread = false;
        });
        pausePlay.start();
    }

    /**
     * Updates the display of the simulation timer and relevant statistics.
     * This method is invoked by the JavaFX timeline at regular intervals.
     *
     * @param event the action event triggering the method call (automatically
     *              provided by JavaFX).
     */
    private void updateTimer(ActionEvent event) {
        LocalDateTime properTime = Timekeeper.getProperTime();
        String time = properTime.toString();
        long customerCount = Customer.getAllCallCount();
        long waitTime = Customer.getAllTotalWaitTime();
        long meanWaitTime = Customer.getAllAverageWaitTime();
        timeer.setText(time +
                "\nTotal Calls: " + customerCount + " calls" +
                "\nTotal Wait Time: " + waitTime + "s" +
                "\nAverage Wait Time: " + meanWaitTime + "s");
    }

    /**
     * Displays a confirmation dialog for selecting the simulation type: old
     * customers or new customers.
     * This method creates a JavaFX Alert with custom buttons for the two simulation
     * types and waits for
     * the user's response. Depending on the chosen option, the corresponding
     * callback method is invoked.
     *
     * @param primaryStage the primary stage used as the owner of the dialog.
     */
    public void showYesNoDialog(Stage primaryStage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Simulation Type");
        alert.setHeaderText("How would you like to start the simulation?");
        alert.initStyle(StageStyle.UNIFIED);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(primaryStage);

        // Create Costume buttons
        ButtonType costumeYesButtonType = new ButtonType("Old Costumers");
        ButtonType costumeNoButtonType = new ButtonType("New Costumers");

        // Add buttons
        ObservableList<ButtonType> buttons = alert.getButtonTypes();
        buttons.setAll(costumeYesButtonType, costumeNoButtonType);

        // Show and wait for user response
        Optional<ButtonType> selection = alert.showAndWait();
        selection.ifPresent(response -> {
            if (response == costumeYesButtonType) {
                oldCosbtnClicked();
            } else if (response == costumeNoButtonType) {
                newCosbtnClicked();
            }
        });
    }

    /**
     * Handles the action when the "Old Environment" button is clicked. Opens a file
     * explorer to
     * allow the user to choose a zip file. The selected zip file is then processed
     * to load the panes
     * and prepare them for the simulation process.
     */
    void oldCosbtnClicked() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose zip file");
            ExtensionFilter fileChooserWithExtention = new FileChooser.ExtensionFilter("Zip Files", "*.zip");
            ObservableList<ExtensionFilter> extentionFilter = fileChooser.getExtensionFilters();
            extentionFilter.add(fileChooserWithExtention);
            Stage stage = new Stage();
            File zipFile = fileChooser.showOpenDialog(stage);
            handleOpenFile(zipFile);
        } catch (Exception e) {
            showErrorAlert("File Explorer", "This file cannot be processed please choose another");
        }

    }

    /**
     * Handles the opening of a zip file selected by the user. The method processes
     * the zip file by
     * extracting its components into a directory named "extracted." It then
     * processes the CSV files
     * extracted from the zip file and deletes the extracted files and directory
     * after processing.
     * The method also updates the recent files list, the recent files menu, and
     * saves the recent files.
     *
     * @param zipFile the zip file selected by the user to be opened and processed.
     */
    public void handleOpenFile(File zipFile) {
        System.out.println("Handling now");
        // Check if the file is already in the recent files list
        if (!recentFiles.stream()
                .anyMatch(existingFile -> existingFile.getAbsolutePath().equals(zipFile.getAbsolutePath()))) {
            System.out.println("Checked 1");
            // Add the opened file to recentFiles
            recentFiles.add(0, zipFile);
            System.out.println("Added to recent");

            // Remove duplicates from recentFiles (keeping only the most recent occurrence)
            Set<File> uniqueRecentFiles = new LinkedHashSet<>(recentFiles);
            recentFiles.clear();
            recentFiles.addAll(uniqueRecentFiles);

            // Trim the list to the maximum allowed size
            int size = recentFiles.size();
            if (size > MAX_RECENT_FILES) {
                List<File> file = recentFiles.subList(MAX_RECENT_FILES, size);
                file.clear();
            }

            // Update the recent files menu
            updateRecentFilesMenu();
            System.out.println("Updated recent");

            // Save the recent files
            saveRecentFiles();
            System.out.println("Saved recent");
        }
        // after all the previous checkpoints, we are now ready to deal with the zip
        // file
        // open file process
        if (zipFile != null) {
            System.out.println("Opening");
            String outputDirectory = "extracted";
            try {
                Zip.extractZip(zipFile, outputDirectory); // this method takes a zip file and extracts its components in
                                                          // a directory named "extracted"
                processCsvFiles(outputDirectory); // this method will process the CSV files extraced from the zip file
                                                  // chosen
                System.out.println("CSV files processed successfully.");

                Zip.deleteExtracted(outputDirectory); // after processing the CSV files, the files and the extracted
                                                      // directory will be deleted
            } catch (IOException e) {
                showErrorAlert("File Extraction", "Failed to extract, try another File");

            }
        }

    }

    /**
     * Processes the CSV files located in the specified extracted directory
     * individually. The method iterates
     * through an array of predefined CSV file names ("Problem.csv", "Customer.csv",
     * "Agent.csv",
     * "Department.csv") and calls specific methods to process each corresponding
     * CSV file.
     *
     * @param extractedDirectory the directory where the CSV files are extracted
     *                           from the zip file.
     */
    private void processCsvFiles(String extractedDirectory) {
        String[] csvFileNames = { "Problem.csv", "Customer.csv", "Agent.csv", "Department.csv" };
        for (String fileName : csvFileNames) {
            Path csvFilePath = Paths.get(extractedDirectory, fileName);

            try {
                if (fileName.contains(csvFileNames[0])) {
                    File problemFile = csvFilePath.toFile();
                    processProblemFile(problemFile);
                } else if (fileName.contains(csvFileNames[1])) {
                    File customerFile = csvFilePath.toFile();
                    processCustomerFile(customerFile);
                } else if (fileName.contains(csvFileNames[2])) {
                    File agentFile = csvFilePath.toFile();
                    processAgentFile(agentFile);
                } else if (fileName.contains(csvFileNames[3])) {
                    File departmentFile = csvFilePath.toFile();
                    processDepartmentFile(departmentFile);
                }
            } catch (IOException e) {
                showErrorAlert(fileName, "Failed to extract " + fileName);
            }
        }
    }

    /**
     * Handles the action when the "Old" menu item is clicked. Call on
     * {@oldCosbtnClicked}
     *
     * @param event The ActionEvent associated with the "Old" menu item click.
     */
    @FXML
    void oldCosbtnClicked(ActionEvent event) {
        try {
            oldCosbtnClicked(); // if the menubar item "Old" was clicked, call the method
        } catch (Exception e) {
            return;
        }
    }

    /**
     * Processes a CSV file containing customer data. Reads the CSV file using the
     * {@link Csv#read(String)} method, creates an array of {@code Customer} objects
     * from the read data, and updates the GUI to display the customer information.
     * 
     * @param selectedFile The CSV file to be processed.
     * @throws IOException If an I/O error occurs during the file processing.
     */
    @FXML
    private void processCustomerFile(File selectedFile) throws IOException {
        try {
            // depending on the static Csv.read method, we will be reading the customersCSV
            // file here and store its content in an array of HasData objects
            String file = selectedFile.getAbsolutePath();
            HasData[] customersCSV = Csv.read(file); // Done

            // initialzing the array of Customer type that will be iterated over
            int length = customersCSV.length;
            customers = new Customer[length];

            // this for loop loads the array of Customer type with the customersCSV content
            // with the help of casting
            int customerIndex = 0;
            for (HasData customer : customersCSV) {
                customers[customerIndex] = (Customer) customer;
                customerIndex = customerIndex + 1;
            }
            ObservableList<Node> flowPaneChildren = flowPane.getChildren();
            int flowPaneSize = flowPaneChildren.size();
            if (flowPaneSize != 0) {
                flowPaneChildren.clear();
            } // clear the customersPane if it has previous content

            // this for loop loads the GUI with the customers in thier places
            int customerArrayLength = customers.length;
            for (int i = 0; i < customerArrayLength; i++) {
                ImageView imageView = new ImageView(customerImage);
                imageView.setFitWidth(30);
                imageView.setFitHeight(30);
                StackPane stackPane = new StackPane();
                Rectangle rectangle = new Rectangle(30, 30, Color.TRANSPARENT);
                stackPane.getChildren().addAll(imageView, rectangle);
                String CustomerInfo = customers[i].getStringInfo();
                addTooltip(rectangle, CustomerInfo);
                ObservableList<Node> flowPaneChild = flowPane.getChildren();
                flowPaneChild.add(stackPane);
            }
            loadCustomersTable();
        } catch (NumberFormatException e) {
            showErrorAlert("Agent.csv", "Failed to process please use the correct format");
        }
    }

    /**
     * Processes a CSV file containing agent data. Reads the CSV file using the
     * {@link Csv#read(String)} method, creates an array of {@code Agent} objects
     * from the read data, and updates the GUI to display the agent information.
     * 
     * @param selectedFile The CSV file to be processed.
     * @throws IOException If an I/O error occurs during the file processing.
     */
    @FXML
    private void processAgentFile(File selectedFile) throws IOException {
        try {
            // depending on the static Csv.read method, we will be reading the agentsCSV
            // file here and store its content in an array of HasData objects
            HasData[] agentsCSV = Csv.read(selectedFile.getAbsolutePath()); // Done

            // initialzing the array of Agent type that will be iterated over
            agents = new Agent[agentsCSV.length];

            // this for loop loads the array of Agent type with the agentsCSV content with
            // the help of casting
            int j = 0;
            for (HasData agent : agentsCSV) {
                agents[j] = (Agent) agent;
                j = j + 1;
            }

            if (AgentVbox.getChildren().size() != 0) {
                AgentVbox.getChildren().clear();
            } // clear the agentsPane if it has previous content

            // this for loop loads the GUI with the agents in thier places
            for (int i = 0; i < agents.length; i++) {

                ImageView imageView = new ImageView(agentImage);
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);

                StackPane stackPane = new StackPane();

                Rectangle rectangle = new Rectangle(20, 20, Color.TRANSPARENT);
                stackPane.getChildren().addAll(imageView, rectangle);

                Agent agent = (Agent) agents[i];
                addTooltip(rectangle, agent.getStringInfo());

                AgentVbox.getChildren().add(stackPane);
            }
            System.out.println("Finished loading FP");

        } catch (Exception e) {
            showErrorAlert("Agent.csv", "Failed to process please use the correct format");
        }
    }

    /**
     * Processes a CSV file containing problem data. Reads the CSV file using the
     * {@link Csv#read(String, String)} method, creates an array of {@code Problem}
     * objects
     * from the read data, and handles any exceptions that may occur during the
     * process.
     * 
     * @param problemFile The CSV file containing problem data to be processed.
     */
    public void processProblemFile(File problemFile) {
        try {
            problems = Csv.read(problemFile.getAbsolutePath(), Vars.DataClasses.Problem);
        } catch (Exception e) {
            showErrorAlert("Problem.csv", "Failed to process please use the correct format");
        }

    }

    /**
     * Processes a CSV file containing department data. Reads the CSV file using the
     * {@link Csv#read(String, String)} method, creates an array of
     * {@code Department} objects
     * from the read data, and handles any exceptions that may occur during the
     * process.
     * 
     * @param departmentFile The CSV file containing department data to be
     *                       processed.
     */
    public void processDepartmentFile(File departmentFile) {
        try {
            departments = Csv.read(departmentFile.getAbsolutePath(), Vars.DataClasses.Department);
        } catch (Exception e) {
            showErrorAlert("Department.csv", "Failed to process please use the correct format");
        }
    }

/**
 * Adds a tooltip to a rectangle, providing additional information when the mouse hovers over it.
 *
 * This method helps to add tooltips for both customers and agents by associating a rectangle
 * representing them and the text to be displayed in the tooltip.
 *
 * @param rectangle The rectangle representing the customer or agent.
 * @param tooltipText The text to be displayed in the tooltip.
 */
    @FXML
    private void addTooltip(Rectangle rectangle, String tooltipText) {
        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(rectangle, tooltip);

        tooltip.setMaxWidth(300);

        rectangle.setOnMouseEntered((MouseEvent event) -> { // show the toolTip when the mouse hovers above a Person
            tooltip.show(rectangle, event.getScreenX(), event.getScreenY() + 15);
        });

        rectangle.setOnMouseExited((MouseEvent event) -> { // hide the toolTip when the mouse dehovers from a Person
            tooltip.hide();
        });
    }

/**
 * Opens an input dialog to allow the user to create a new call center environment by entering parameters.
 *
 * This method displays an input dialog prompting the user to enter the main parameters that define a call center:
 * the number of customers, agents, departments, and problems. After the user enters the parameters, it generates fake
 * problems and departments based on the user's input and creates fake customers and agents accordingly.
 */
    void newCosbtnClicked() {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Input Dialog");
        inputDialog.setHeaderText("Enter new environment parameters");
        inputDialog.initStyle(StageStyle.UNDECORATED);
        inputDialog.initModality(Modality.APPLICATION_MODAL);

        // Create a GridPane to organize the layout
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);

        // Add labels and text fields to the GridPane
        gridPane.add(new Label("Enter No. Customers:"), 0, 0);
        TextField textField1 = new TextField();
        gridPane.add(textField1, 1, 0);

        gridPane.add(new Label("Enter No. Agnets:"), 0, 1);
        TextField textField2 = new TextField();
        gridPane.add(textField2, 1, 1);

        gridPane.add(new Label("Enter No. Departments:"), 0, 2);
        TextField textField3 = new TextField();
        gridPane.add(textField3, 1, 2);

        gridPane.add(new Label("Enter No. Problems:"), 0, 3);
        TextField textField4 = new TextField();
        gridPane.add(textField4, 1, 3);

        // Set the content of the TextInputDialog to the GridPane
        inputDialog.getDialogPane().setContent(gridPane);
        inputDialog.getDialogPane().setStyle("-fx-border-color: black");

        // Show and wait for user response
        Optional<String> result = inputDialog.showAndWait();

        result.ifPresent(values -> {
            try {
                int numberOfCustomers = Integer.parseInt(textField1.getText());
                int numberOfAgents = Integer.parseInt(textField2.getText());
                int numberOfDepartments = Integer.parseInt(textField3.getText());
                int numberOfProblems = Integer.parseInt(textField4.getText());

                // generate fake problems and departments depending on the user's input
                departments = Utilities.getFakeData(numberOfDepartments, Vars.DataClasses.Department);
                problems = Utilities.getFakeData(numberOfProblems, Vars.DataClasses.Problem);

                // generate fake customers and agents depending on the user's input
                generateNewCustomers(numberOfCustomers);
                generateNewAgents(numberOfAgents);

            } catch (NumberFormatException e) {
                showErrorAlert("Invalid Input", "Please enter a valid integer.");
            }
        });
    }
/**
 * Handles the "New" menu item click event, triggering the creation of a new call center environment.
 * This method delegates to the {@link #newCosbtnClicked()} method.
 *
 * @param event The ActionEvent triggered by clicking the "New" menu item.
 */
    @FXML
    void newCosbtnClicked(ActionEvent event) {
        newCosbtnClicked(); // the menubar item "New" will call this method whenever it was clicked
    }
/**
 * Generates fake customers based on the user's input and updates the UI accordingly.
 *
 * This method generates a specified number of fake customers and updates the UI to display them.
 * It uses a FlowPane to organize the visual representation of customers, consisting of an image view
 * and a transparent rectangle. Tooltips are added to provide additional information when hovering
 * over each customer's representation.
 *
 * @param number The number of fake customers to generate.
 */
    private void generateNewCustomers(int number) {
        if (flowPane.getChildren().size() != 0) {
            flowPane.getChildren().clear();
        }

        customers = new Customer[number];

        int i = 0;
        for (HasData datum : Utilities.getFakeData(number, Vars.DataClasses.Customer)) {
            customers[i] = (Customer) datum;

            ImageView imageView = new ImageView(customerImage);
            imageView.setFitWidth(30);
            imageView.setFitHeight(30);

            StackPane stackPane = new StackPane();

            Rectangle rectangle = new Rectangle(30, 30, Color.TRANSPARENT);
            stackPane.getChildren().addAll(imageView, rectangle);

            addTooltip(rectangle, ((Customer) customers[i]).getStringInfo());

            flowPane.getChildren().add(stackPane);

            i = i + 1;
        }
        loadCustomersTable();
    }

/**
 * Generates fake agents based on the user's input and updates the UI accordingly.
 *
 * This method generates a specified number of fake agents and updates the UI to display them.
 * It uses a VBox (vertical box) to organize the visual representation of agents, consisting of an
 * image view and a transparent rectangle. Tooltips are added to provide additional information when
 * hovering over each agent's representation.
 *
 * @param number The number of fake agents to generate.
 */
    private void generateNewAgents(int number) {
        if (AgentVbox.getChildren().size() != 0) {
            AgentVbox.getChildren().clear();
        }

        agents = new Agent[number];

        int i = 0;
        for (HasData datum : Utilities.getFakeData(number, Vars.DataClasses.Agent)) {
            agents[i] = (Agent) datum;

            ImageView imageView = new ImageView(agentImage);
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);

            StackPane stackPane = new StackPane();

            Rectangle rectangle = new Rectangle(20, 20, Color.TRANSPARENT);

            stackPane.getChildren().addAll(imageView, rectangle);

            addTooltip(rectangle, ((Agent) agents[i]).getStringInfo());

            AgentVbox.getChildren().add(stackPane);

            i = i + 1;

        }

        System.out.println("Finished agents");
    }


/**
 * Highlights the row of the customer with the maximum wait time in the customer table.
 *
 * This method identifies the customer with the maximum wait time, finds its corresponding row index in
 * the customer table, and highlights that row. If the identified row index is the same as the last highlighted
 * index, the method does nothing to avoid unnecessary refreshes. The method utilizes the {@code resetHighlightRows}
 * and {@code highlightRow} methods to handle the highlighting logic.
 */
    public void highlightMax() {
        Customer maxCustomer = Customer.getAllMaxWaitTime();
        int maxCustomerIndex = 0;
        for (int i = 0; i < customers.length; i++) {
            if (maxCustomer == customers[i]) {
                maxCustomerIndex = i;
                break;
            }
        }
        if (maxCustomerIndex == lastMaxIndex) {
            return;
        }
        if (maxCustomerIndex < customers.length) {
            lastMaxIndex = maxCustomerIndex;
            resetHighlightRows();
            customerTable.refresh();
            highlightRow(maxCustomerIndex - 1);
            customerTable.refresh();
        }
    }
/**
 * Highlights a specific row in the customer table.
 *
 * This method sets a custom background color for the specified row in the customer table.
 * It utilizes the {@code setStyle} method to apply the styling. The highlighted row will have
 * a red background color, and other rows will have the default background color. This method is
 * typically used in conjunction with the {@code highlightMax} method to visually emphasize specific
 * rows, such as the one representing the customer with the maximum wait time.
 *
 * @param rowIndex The index of the row to be highlighted in the customer table.
 */
    private void highlightRow(int rowIndex) {
            customerTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                if (getIndex() == rowIndex) {
                    setStyle("-fx-background-color: red;");
                } else {
                    setStyle("");
                }
            }
        });
    }

/**
 * Resets the highlighting of all rows in the customer table.
 *
 * This method sets the style of all rows in the customer table to the default background color,
 * effectively removing any previous highlighting. It utilizes the {@code setStyle} method to apply
 * the styling. This method is typically used when there's a need to clear or reset the highlighting
 * of rows in the customer table, ensuring a clean visual state.
 */
    private void resetHighlightRows() {
        customerTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                    setStyle("");
            }
        });
    }
/**
 * Highlights the visual representation of customers in the UI based on their current state.
 *
 * This method iterates through the array of customers, retrieves their visual representation from
 * the UI, and applies specific highlights based on their current state. The highlights are applied by
 * adjusting the properties of the ImageView associated with each customer. Different states result in
 * different highlight colors (green for INCALL, yellow for WAITING, blue for CHECK_FAQS), while the
 * default state removes any applied effects.
 */
    public void highlightCustomers() {
        int index = 0;
        for (Customer customer : customers) {
            StackPane customerStackPane = (StackPane) flowPane.getChildren().get(index);
            ImageView customerImageView = (ImageView) customerStackPane.getChildren().get(0);

            switch (customer.getState()) {
                case INCALL:
                    applyHighlight(customerImageView, 0.0, 1.0, 0.0, 0.5); // Green
                    break;

                case WAITING:
                    applyHighlight(customerImageView, 1.0, 1.0, 0.0, 0.5); // Yellow
                    break;

                case CHECK_FAQS:
                    applyHighlight(customerImageView, 0.0, 0.0, 1.0, 0.5); // blue
                    break;

                default:
                    customerImageView.setEffect(null);
                    break;
            }

            index++;
        }
    }

/**
 * Applies a highlight effect to an ImageView based on specified color and opacity values.
 *
 * This method creates a ColorInput with the specified color and opacity and uses it as the top input
 * for a Blend effect with the MULTIPLY blending mode. The resulting Blend effect is then set as the effect
 * for the provided ImageView. This process creates a visual highlight effect by adjusting the color and opacity
 * of the original image.
 *
 * @param imageView The ImageView to which the highlight effect will be applied.
 * @param red       The red component of the highlight color (0.0 to 1.0).
 * @param green     The green component of the highlight color (0.0 to 1.0).
 * @param blue      The blue component of the highlight color (0.0 to 1.0).
 * @param opacity   The opacity of the highlight effect (0.0 to 1.0).
 */
    private void applyHighlight(ImageView imageView, double red, double green, double blue, double opacity) {
        ColorInput colorInput = new ColorInput(
                0, 0, imageView.getBoundsInLocal().getWidth(), imageView.getBoundsInLocal().getHeight(),
                javafx.scene.paint.Color.rgb((int) (255 * red), (int) (255 * green), (int) (255 * blue), opacity));

        Blend blend = new Blend(BlendMode.MULTIPLY);
        blend.setTopInput(colorInput);

        imageView.setEffect(blend);
    }

/**
 * Displays an error alert with the specified title and content.
 *
 * This method creates and shows an error alert using the JavaFX Alert class with the ERROR alert type.
 * The alert includes a title, optional header text (null in this case), and content text that describes
 * the error or provides additional information. The alert is displayed modally, and the execution is
 * paused until the user closes the alert using the "OK" button.
 *
 * @param title   The title of the error alert.
 * @param content The content text of the error alert describing the error or providing information.
 */
    public static void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
/**
 * Loads customer data into the customer table.
 *
 * This method populates the data in the customer table by adding all customers to the table's items.
 * The method utilizes the JavaFX TableView class and assumes that the customerTable has been properly
 * initialized and configured with the necessary columns. By calling this method, the customer data is
 * displayed in the UI within the customer table.
 */
    private void loadCustomersTable() {
        customerTable.getItems().addAll(customers);
    }
/**
 * Updates the content of specific columns in the customer table.
 *
 * This method is responsible for updating the content of specific columns in the customer table.
 * It sets the cell value factories for the AWTcolumn (Average Wait Time) and MAXTcolumn (Max Wait Time)
 * columns to retrieve the corresponding values from each customer. The TableView is then refreshed to
 * reflect the changes in the displayed data.
 */
    private void updateCustomersTable() {
        // Load numbers into the second column when the button is clicked

        AWTcolumn.setCellValueFactory(cellData -> {
            double someDoubleValue = cellData.getValue().getAverageWaiTime();
            return new SimpleDoubleProperty(someDoubleValue);
        });

        MAXTcolumn.setCellValueFactory(cellData -> {
            double someDoubleValue = cellData.getValue().getMaxWaitTime();
            return new SimpleDoubleProperty(someDoubleValue);
        });

        // Update the table to reflect the changes
        customerTable.refresh();
    }

/**
 * Handles the action when the "Save as" menu item is clicked in the File menu.
 *
 * This method displays a FileChooser dialog allowing the user to select a location to save the call center
 * data as a ZIP file. It then writes CSV files for customers, agents, problems, and departments to the specified
 * output directory. After writing the CSV files, it compresses them into a ZIP file and deletes the intermediate
 * extraction directory. The final ZIP file is saved to the user-selected location.
 *
 * @param event The ActionEvent triggered when the "Save as" menu item is clicked.
 * @throws IOException If an I/O exception occurs during file operations.
 */
    @FXML
    void saveAsbtnClicked(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP Files", "*.zip"));

        // Show Save As dialog
        File saveFile = fileChooser.showSaveDialog(new Stage()); // asks the user to enter the file name he wants to
                                                                 // save

        if (saveFile != null) {
            // Get the chosen file path from the user
            String zipFilePath = saveFile.getAbsolutePath();

            // write the csvfiles in the output directory
            // the static method Csv.write takes the HasData[] array and the path to where
            // to save the array.
            Csv.write((HasData[]) customers, "call_center/output/Customer.csv");
            Csv.write((HasData[]) agents, "call_center/output/Agent.csv");
            Csv.write((HasData[]) Problem.getAllProblems(), "call_center/output/Problem.csv");

            Department[] departments = Department.getAllDepartments().values()
                    .toArray(new Department[Department.getAllDepartments().size()]);
            Csv.write(departments, "call_center/output/Department.csv");

            // after writing and generating the 4 main CSV files, it is now time to compress
            // them and generate one zip file for the user
            Zip.compressToZip(zipFilePath, "call_center/output"); // generate the zip file
            Zip.deleteExtracted("call_center/output"); // delete the extraction directory

            System.out.println("ZIP file saved to: " + zipFilePath);
        }

    }

/**
 * Handles the opening of a recently used file from the "Open Recent" menu.
 *
 * This method is called when a specific MenuItem in the "Open Recent" menu is clicked. It retrieves the selected
 * file from the UserData of the MenuItem and then delegates the file opening process to the {@code handleOpenFile}
 * method. The {@code handleOpenFile} method typically loads the data from the selected file into the application,
 * treating it similarly to choosing an old environment.
 *
 * @param menuItem The MenuItem representing the recently used file that was clicked.
 */
    @FXML
    private void handleOpenRecent(MenuItem menuItem) {
        File selectedFile = (File) menuItem.getUserData();
        handleOpenFile(selectedFile);
    }

/**
 * Updates the "Open Recent" menu with the list of recently used files.
 *
 * This method clears the existing items in the "Open Recent" menu and populates it with new MenuItems,
 * each representing a recently used file. The MenuItems are created based on the files stored in the
 * {@code recentFiles} list. Each MenuItem is associated with an event handler that calls the {@code handleOpenRecent}
 * method when clicked, passing the corresponding file as a parameter.
 */
    @FXML
    private void updateRecentFilesMenu() {
        OpenRecentMenu.getItems().clear();
        for (File file : recentFiles) {
            MenuItem menuItem = new MenuItem(file.getName());
            menuItem.setOnAction(e -> handleOpenRecent(menuItem));
            menuItem.setUserData(file);
            OpenRecentMenu.getItems().add(menuItem);
        }
    }

/**
 * Loads the list of recently used files from the "recent_files.txt" file.
 *
 * This method reads the file paths of recently used files from a text file named "recent_files.txt".
 * It populates the {@code recentFiles} list with File objects representing these files, ensuring uniqueness
 * and limiting the number of recent files to {@code MAX_RECENT_FILES}. If the file "recent_files.txt" does not
 * exist, an empty file is created. Any IOException during the process is printed to the standard error stream.
 *
 * @throws IOException If an I/O exception occurs during file operations.
 */
    private void loadRecentFiles() throws IOException {
        // the loading method depends on reading the recent file paths from a text file
        // named recent_files.txt
        try (BufferedReader reader = new BufferedReader(new FileReader(RECENT_FILES_FILE))) {
            Set<String> uniquePaths = new HashSet<>();
            recentFiles.clear();

            String line;
            while ((line = reader.readLine()) != null && recentFiles.size() < MAX_RECENT_FILES) {
                // Ensure uniqueness
                if (uniquePaths.add(line)) {
                    File file = new File(line);
                    recentFiles.add(file);
                }
            }
        } catch (FileNotFoundException e) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(RECENT_FILES_FILE))) {

            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

/**
 * Saves the list of recently used files to the "recent_files.txt" file.
 *
 * This method is called during the file handling process to ensure that the opened file is saved in the
 * "recent_files.txt" file. It writes the absolute paths of the recently used files to the text file, ensuring
 * uniqueness and saving only the last {@code MAX_RECENT_FILES} files. Any IOException during the process is
 * printed to the standard error stream.
 */
    private void saveRecentFiles() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RECENT_FILES_FILE))) {
            Set<String> uniquePaths = new HashSet<>();

            // Save only the last MAX_RECENT_FILES files
            int startIndex = Math.max(0, recentFiles.size() - MAX_RECENT_FILES);
            for (int i = startIndex; i < recentFiles.size(); i++) {
                File file = recentFiles.get(i);

                // Ensure uniqueness
                if (uniquePaths.add(file.getAbsolutePath())) {
                    writer.write(file.getAbsolutePath());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/**
 * Handles the action when the "Pause" button is clicked.
 *
 * This method is invoked in response to the "Pause" button being clicked. It pauses the timer timeline.
 *
 * @param event The ActionEvent triggered by the "Pause" button click.
 */
    @FXML
    void pausebtnClicked(ActionEvent event) {
        timerTimeline.pause();
    }
/**
 * Handles the action when the "Play" button is clicked.
 *
 * This method is invoked in response to the "Play" button being clicked. It plays the timer timeline.
 *
 * @param event The ActionEvent triggered by the "Play" button click.
 */
    @FXML
    void playbtnClicked(ActionEvent event) {
        timerTimeline.play();
    }
/**
 * Initiates the process of connecting a call by displaying a "Connecting..." label, activating the call,
 * and connecting it to the call center.
 *
 * This method is responsible for updating the UI to indicate that the call is in the process of connecting.
 * It adds a "Connecting..." label to the call's HBox, and after a brief delay, removes the label and proceeds
 * to activate the call and connect it to the call center.
 *
 * @param call The Call object representing the call to be connected.
 */
    public void connectingCall(Call call) {
        Label label = new Label("Connecting...");
        Platform.runLater(() -> 
            call.getHbox().getChildren().add(label));
        PauseTransition delay = new PauseTransition(Duration.millis(600));
        delay.setOnFinished(event -> {
            // Code to execute after the delay
            Platform.runLater(() -> call.getHbox().getChildren().remove(label));
            this.activateCall(call.getHbox(), call.getReceiver(), call);
            call.connectCall(CallCenter.getCallCentre());
        });
        delay.play();
    }
/**
 * Activates a call by updating the UI with relevant information such as call icon, agent details, and call number.
 *
 * This method is responsible for enhancing the visual representation of an active call in the UI. It adds
 * components like the call icon, agent image, checkbox, and call number to the specified HBox. Additionally,
 * it associates a tooltip with the agent's details for informational purposes.
 *
 * @param hBox  The HBox in the UI where the call components will be added.
 * @param agent The Agent associated with the call.
 * @param call  The Call object representing the active call.
 */
    public void activateCall(HBox hBox, Agent agent, Call call) {
        ImageView callIcon = new ImageView(callImage);
        ImageView callImageViews = new ImageView(agentImage);
        CheckBox checkBox = call.getCheckBox();
        Text callNumber = call.getCallNumber();
        callIcon.setFitWidth(15);
        callIcon.setFitHeight(15);
        callImageViews.setFitWidth(30);
        callImageViews.setFitHeight(30);
        Rectangle rectangle = new Rectangle(20, 20, Color.TRANSPARENT);
        StackPane stackPane = new StackPane();
        Platform.runLater(() -> {            
            stackPane.getChildren().addAll(callImageViews, rectangle);
            hBox.getChildren().addAll(checkBox, callIcon, stackPane, callNumber);
            addTooltip(rectangle, agent.getStringInfo());
        });
    }

    /**
     * Creates and returns an array of JavaFX nodes representing a graphical
     * representation
     * of a customer's call in the user interface.
     *
     * @param customer The customer associated with the call.
     * @return An array of JavaFX nodes representing the graphical representation of
     *         the call.
     */
    public Node[] createHbox(Customer customer) {
        long thisCallCount = ++callCount;
        HBox hBox = new HBox();
        CheckBox checkBox = new CheckBox();
        Text callnumberr = new Text();
        callnumberr.setText(String.valueOf(thisCallCount));
        hBox.setSpacing(4);
        ImageView callImageView = new ImageView(customerImage);
        Rectangle rectangle = new Rectangle(20, 20, Color.TRANSPARENT);
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(callImageView, rectangle);
        callImageView.setFitWidth(30);
        callImageView.setFitHeight(30);
        hBox.getChildren().add(stackPane);
        addTooltip(rectangle, customer.getStringInfo());
        HBox.setHgrow(checkBox, Priority.ALWAYS);
        checkBox.setAlignment(Pos.BOTTOM_RIGHT);
        checkBox.setOnAction(e -> handleCheckboxAction("Call" + thisCallCount, checkBox));
        Node[] pointers = { hBox, checkBox, callnumberr };
        return pointers;
    }
/**
 * Handles the action triggered by checking or unchecking a checkbox associated with a call.
 *
 * This method is responsible for managing the behavior when a call's checkbox is checked or unchecked.
 * If a checkbox is checked, it creates or shows the associated dialog window for the call. If unchecked, 
 * it closes or exits the associated dialog window.
 *
 * @param callNumber The identifier of the call associated with the checkbox.
 * @param checkbox   The checkbox representing the call.
 */
    public void handleCheckboxAction(String callNumber, CheckBox checkbox) {
        HashMap<CheckBox, DialogeBox> checkBoxLink = Call.getLinkBetweenCheckBoxesAndDialoge();
        HashMap<CheckBox, Call> callLink = Call.getLinkBetweenCheckBoxesAndCalls();
        if (checkbox.isSelected()) {
            if (checkBoxLink.containsKey(checkbox)) {
                if (!endThread) {
                    checkBoxLink.get(checkbox).showWindow();
                    return;
                }
            }
            Call call = callLink.get(checkbox);
            Runnable dialoge = new DialogeBox(callNumber, phaser, call);
            checkBoxLink.put(checkbox, (DialogeBox) dialoge);
            executor.execute(dialoge);

        } else {
            if (!endThread) {
                checkBoxLink.get(checkbox).closeWindow();
                return;
            }
            try {
                
                checkBoxLink.get(checkbox).exit();
                checkBoxLink.remove(checkbox);
            } catch (NullPointerException e) {
                return;
            }

        }
    }
/**
 * Checks the simulation progress and prompts the user for a decision at regular intervals.
 *
 * This method is called periodically to assess the simulation progress. If the current time is a multiple
 * of 50, the simulation is paused, and a custom confirmation dialog is presented to the user. The user can choose
 * to end the simulation and view the results or continue with the simulation.
 *
 * The results include total call count, total wait time, and average wait time for all customers.
 */
    public void checkPoint() {
        if (Timekeeper.getTime() % 50 == 0) {
            pause();
            // Create a custom confirmation dialog without the top bar
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

            // Disable the default close button ("X") in the title bar
            dialog.initStyle(javafx.stage.StageStyle.UNDECORATED);

            // Set content for the dialog
            Label label = new Label("Do you want to end the simulation and see the results?");
            VBox content = new VBox(label);
            dialog.getDialogPane().setContent(content);

            // Show the dialog and handle the result
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    displayExitMessage(
                            "Total Calls: " + Customer.getAllCallCount() + " calls" +
                                    "\nTotal Wait Time: " + Customer.getAllTotalWaitTime() + "s" +
                                    "\nAverage Wait Time: " + Customer.getAllAverageWaitTime() + "s");

                } else {
                    phaser.arriveAndDeregister();
                    endThread = true;
                    MenPasue.setDisable(false);
                    MenPlay.setDisable(true);
                    timerTimeline.play();
                    // User clicked "No" or closed the dialog
                    System.out.println("User clicked No or closed the dialog");
                }
            });

        }
    }
/**
 * Displays an exit message with simulation results and prompts the user to close the application.
 *
 * This method creates an information dialog with the provided message, displaying simulation results.
 * It includes a "Close" button for the user to acknowledge the results. If the user clicks the "Close" button,
 * the application is exited.
 *
 * @param message The message containing simulation results to be displayed in the dialog.
 */
    private void displayExitMessage(String message) {
        ButtonType closeButton = new ButtonType("Close");
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, closeButton);
        alert.setTitle("Simulation Results");
        alert.setHeaderText("Simulation Results"); // No header text
        alert.showAndWait();

        // Close the application when the "Exit" button is clicked
        if (alert.getResult() == closeButton) {
            System.exit(0);
            System.out.println("User clicked Yes --> Ended");
        }
    }
/**
 * Handles the ActionEvent when the "phase2MenItem" menu item is selected or deselected.
 * Updates the "Vars.projectPhase" variable based on the selection state of the menu item.
 *
 * @param event The ActionEvent triggered by selecting or deselecting the menu item.
 */    
    @FXML
    void phaseChecked(ActionEvent event) {
        if (phase2MenItem.isSelected()) {
            Vars.projectPhase = true;
        } else {
            Vars.projectPhase = false;
        }
    }
/**
 * Handles the ActionEvent when the "Start" button is clicked.
 * Initiates the simulation by creating a new CallCenter and starting a thread to simulate the call center operations.
 * Additionally, starts the timer for time tracking during the simulation.
 *
 * @param event The ActionEvent triggered by clicking the "Start" menu.
 */
    @FXML
    void startbtnClicked(ActionEvent event) {
        try {
            // Check if the timer is not already running

            callCenter = new CallCenter(agents);
            System.out.println("casted");

            new Thread(() -> {
                phaser.register();
                while (running) {
                    for (Customer customer : customers) {
                        customer.step();
                    }

                    for (Call call : Call.getActiveCalls()) {
                        call.step();
                    }
                    Call.terminateCalls();
                    callCenter.step();
                    Timekeeper.step();
                    highlightCustomers();
                    highlightMax();
                    updateCustomersTable();

                    try {
                        Thread.sleep(Timekeeper.getDelayMs());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        phaser.arriveAndAwaitAdvance();
                        HeapSizeChecker.checkMemory();
                        Platform.setImplicitExit(true);
                    }
                }
            }).start();

            if (!timerTimeline.getStatus().equals(Timeline.Status.RUNNING)) {
                // Start the timer when the "Start" button is clicked
                timerTimeline.play();

                // Disable the "Start" button to prevent further clicks
                ((MenuItem) event.getSource()).setDisable(true);
                MenPasue.setDisable(false);
                phase2MenItem.setDisable(true);
            }
        } catch (Exception e) {
            showErrorAlert("Starting Simulation", "Environment is not Loaded");
            return;
        }
    }
}
