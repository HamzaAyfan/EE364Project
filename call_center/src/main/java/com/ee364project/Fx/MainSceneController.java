package com.ee364project.Fx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
//import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
//import java.util.ArrayList;
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
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
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
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.prefs.Preferences;

public class MainSceneController {
    Agent[] agents;
    Customer[] customers;
    HasData[] problems;
    HasData[] departments;

    private ArrayList<File> recentFiles = new ArrayList<>();
    private static final String RECENT_FILES_FILE = "recent_files.txt";
    private static final int MAX_RECENT_FILES = 5;
    private static Phaser phaser = new Phaser(0);
    private Thread pausePlay = new Thread();
    public static boolean endThread;
    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private int checkedCount = 0;

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
    private FlowPane flowPane; // Customers FlowPane

    @FXML
    private Text timeer;

    @FXML
    private VBox Vox;

    @FXML
    public VBox CallVbox;

    @FXML
    private Button connected;

    // initializing the images that is going to be used for loading the main stage
    // panes
    Image customerImage = new Image("com\\ee364project\\Fx\\resources\\user.png");
    Image agentImage = new Image("com\\ee364project\\Fx\\resources\\agent.png");
    Image callImage = new Image("com\\ee364project\\Fx\\resources\\green.jpg");
    Image showCallImage = new Image("com\\ee364project\\Fx\\resources\\show.png");
    Image hideCallImage = new Image("com\\ee364project\\Fx\\resources\\hide.png");

    // ************************Mshari Edit****************************** *//
    private CallCenter callCenter;

    private Timeline timerTimeline;
    // private Timekeeper timekeeper;
    private boolean newThreadAdded;
    private VBox Vbox = CallVbox;

    @FXML
    public void initialize() {
        // setting all the important references and spaces for panes internal look
        MenPasue.setOnAction(e -> {
            this.pause();
        });
        MenPlay.setDisable(true);
        MenPasue.setDisable(true);
        Call.vBox = CallVbox;
        Call.phaser = phaser;
        CallVbox.setSpacing(4);
        AgentVbox.setSpacing(4);

        

        // timekeeper = new Timekeeper();
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), this::updateTimer));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);

        try {
            // calling the methods below would load the recent files once the program runs
            loadRecentFiles();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

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

    //////////////// Timer methods from TimeKeeper Class//////////////
    private void updateTimer(ActionEvent event) {
        Timekeeper.step();
        LocalDateTime properTime = Timekeeper.getProperTime();
        int minutes = properTime.getMinute();
        int seconds = properTime.getSecond();
        // timeer.setText(String.format("%02d:%02d", minutes, seconds));
        timeer.setText(properTime.toString() + "\nAverage Wait Time: " + Customer.getAllAverageWaitTime() + "s");
    }
    ////////////////////////////////

    

    // ******************************************************** *//

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
        alert.getButtonTypes().setAll(costumeYesButtonType, costumeNoButtonType);

        // Show and wait for user response
        alert.showAndWait().ifPresent(response -> {
            if (response == costumeYesButtonType) {
                oldCosbtnClicked();
            } else if (response == costumeNoButtonType) {
                newCosbtnClicked();
            }
        });
    }

    // if the "OLD Environment" button was clicked, the file explorer will open and
    // asks the user for a file to load in the panes
    // and prepare them for the simulation process
    void oldCosbtnClicked() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose zip file");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Zip Files", "*.zip"));
            File zipFile = fileChooser.showOpenDialog(new Stage());
            handleOpenFile(zipFile);
        } catch (Exception e) {

        }

    }

    // after the user chooses a file (Zip File) from the file explorer it is the
    // time to process it in this method
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
            if (recentFiles.size() > MAX_RECENT_FILES) {
                recentFiles.subList(MAX_RECENT_FILES, recentFiles.size()).clear();
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

                // recentFiles.add(zipFile);
                // updateRecentFilesMenu();
                //
                // saveRecentFiles();

            } catch (IOException e) {
                e.printStackTrace();

            }
        }

    }

    // this method takes in the directory that contains the CSV files, and process
    // each one of them individually
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
                e.printStackTrace();
            }
        }
    }

    @FXML
    void oldCosbtnClicked(ActionEvent event) {
        try {
            oldCosbtnClicked(); // if the menubar item "Old" was clicked, call the method
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @FXML
    private void processCustomerFile(File selectedFile) throws IOException {
        try {
            // depending on the static Csv.read method, we will be reading the customersCSV
            // file here and store its content in an array of HasData objects
            HasData[] customersCSV = Csv.read(selectedFile.getAbsolutePath()); // Done

            // initialzing the array of Customer type that will be iterated over
            customers = new Customer[customersCSV.length];

            // this for loop loads the array of Customer type with the customersCSV content
            // with the help of casting
            int j = 0;
            for (HasData customer : customersCSV) {
                customers[j] = (Customer) customer;
                j = j + 1;
            }
            System.out.println("Customers creeated");

            if (flowPane.getChildren().size() != 0) {
                flowPane.getChildren().clear();
            } // clear the customersPane if it has previous content

            // this for loop loads the GUI with the customers in thier places
            for (int i = 0; i < customers.length; i++) {

                ImageView imageView = new ImageView(customerImage);
                imageView.setFitWidth(20);
                imageView.setFitHeight(20);

                StackPane stackPane = new StackPane();

                Rectangle rectangle = new Rectangle(20, 20, Color.TRANSPARENT);
                // rectangle.setStyle("-fx-fill: green;");
                // rectangle.setFill(new ImagePattern(image));
                stackPane.getChildren().addAll(imageView, rectangle);
                // rectangle.setStyle("-fx-fill: green;");

                addTooltip(rectangle, customers[i].getStringInfo());

                flowPane.getChildren().add(stackPane);
            }
            System.out.println("Finished loading FP");

        } catch (NumberFormatException e) {
            System.out.println("Please enter a a valid CSV file.");
        } finally {

        }

    }

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

            System.out.println("Agents creeated");

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
            System.out.println("Please enter a a valid CSV file.");
        }

    }

    public void processProblemFile(File problemFile) {
        try {
            problems = Csv.read(problemFile.getAbsolutePath(), Vars.DataClasses.Problem);

            System.out.println("Problem loaded");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void processDepartmentFile(File departmentFile) {
        try {
            departments = Csv.read(departmentFile.getAbsolutePath(), Vars.DataClasses.Department);
            System.out.println("Problem loaded");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // this method helps us to add toolTips for both customers and agents by taking
    // thier representing rectangle and the text we want to display
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

    // this method allows the user to create his own call center environment by
    // asking him to enter the main
    // parameters that synethize a call center, which are the number of customer,
    // agents, departments, and problems
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

    @FXML
    void newCosbtnClicked(ActionEvent event) {
        newCosbtnClicked(); // the menubar item "New" will call this method whenever it was clicked
    }

    // this method generates fake customers depending on the user's input
    private void generateNewCustomers(int number) {
        if (flowPane.getChildren().size() != 0) {
            flowPane.getChildren().clear();
        }

        customers = new Customer[number];

        int i = 0;
        for (HasData datum : Utilities.getFakeData(number, Vars.DataClasses.Customer)) {
            customers[i] = (Customer) datum;

            ImageView imageView = new ImageView(customerImage);
            imageView.setFitWidth(20);
            imageView.setFitHeight(20);

            StackPane stackPane = new StackPane();

            Rectangle rectangle = new Rectangle(20, 20, Color.TRANSPARENT);
            // rectangle.setStyle("-fx-fill: green;");
            // rectangle.setFill(new ImagePattern(image));
            stackPane.getChildren().addAll(imageView, rectangle);
            // rectangle.setStyle("-fx-fill: green;");

            // Customer customer = customers[i];

            addTooltip(rectangle, ((Customer) customers[i]).getStringInfo());

            flowPane.getChildren().add(stackPane);

            i = i + 1;

            // System.out.println(((Customer) customers[i]).getStringInfo()); Done
        }
    }

    // this method generates fake agents depending on the user's input
    private void generateNewAgents(int number) {
        if (AgentVbox.getChildren().size() != 0) {
            AgentVbox.getChildren().clear();
        }

        // HasData[] agents = Utilities.getFakeData(number, Vars.DataClasses.Agent);
        agents = new Agent[number];

        int i = 0;
        for (HasData datum : Utilities.getFakeData(number, Vars.DataClasses.Agent)) {
            agents[i] = (Agent) datum;

            ImageView imageView = new ImageView(agentImage);
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);

            StackPane stackPane = new StackPane();

            Rectangle rectangle = new Rectangle(20, 20, Color.TRANSPARENT);
            // rectangle.setStyle("-fx-fill: green;");
            // rectangle.setFill(new ImagePattern(image));
            stackPane.getChildren().addAll(imageView, rectangle);
            // rectangle.setStyle("-fx-fill: green;");

            // Agent agent = (Agent) agents[i];
            addTooltip(rectangle, ((Agent) agents[i]).getStringInfo());

            AgentVbox.getChildren().add(stackPane);

            i = i + 1;
            // System.out.println(((Customer) customers[i]).getStringInfo()); Done
        }
        // CallCenter callCenter = new CallCenter(agents);
        System.out.println("Finished agents");
    }

    // this method is being used to show an error alert whenever is needed to pop
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // this method is called when the menubar item "Save as" is clicked
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

    // when a file is clicked from the menubar item "recents" the file is handled
    // similarly to choosing an old environment
    @FXML
    private void handleOpenRecent(MenuItem menuItem) {
        File selectedFile = (File) menuItem.getUserData();
        handleOpenFile(selectedFile);
    }

    // this method is responsible for updating the recentFiles whenever a file was
    // opened
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

    // this method is being called once the program runs to load the recentFiles
    // list with the recently opened files
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

    // this method is called during the file handling process and it insures to save
    // the opened file in the recent_files.txt
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

    @FXML
    void pausebtnClicked(ActionEvent event) {
        timerTimeline.pause();
    }

    @FXML
    void playbtnClicked(ActionEvent event) {
        timerTimeline.play();
    }

    // Code For DialogueBox
    // *****************************************************************************************
    // */
    public Node[] createHbox() {
        HBox hBox = new HBox();
        CheckBox checkBox = new CheckBox();
        
         ImageView callImageView = new ImageView(customerImage);
         ImageView callIcon = new ImageView(callImage);
         ImageView callImageViews = new ImageView(agentImage);
         ImageView showhideImageView = new ImageView(showCallImage);
         
         callImageView.setFitWidth(20);
         callImageView.setFitHeight(20);
         callIcon.setFitWidth(10);
         callIcon.setFitHeight(10);
         callImageViews.setFitWidth(20);
         callImageViews.setFitHeight(20);
         showhideImageView.setFitWidth(10);
         showhideImageView.setFitHeight(10);

         //Label label = new Label("", showhideImageView);


        // Rectangle rectangle = new Rectangle(50, 50, Color.TRANSPARENT);
         hBox.getChildren().add(callImageView);
         hBox.getChildren().add(callIcon);
         hBox.getChildren().add(callImageViews);
        // hBox.getChildren().add(rectangle);
        hBox.getChildren().add(checkBox);
        HBox.setHgrow(checkBox, Priority.ALWAYS);
        checkBox.setAlignment(Pos.BOTTOM_RIGHT);
        //checkBox.setGraphic(showhideImageView);
        checkBox.setOnAction(e -> handleCheckboxAction("Call", checkBox));
        // checkBox.selectedProperty().addListener(createChangeListener(checkBox));
        Node[] pointers = { hBox, checkBox };
        return pointers;
    }

    
    public void handleCheckboxAction(String callNumber, CheckBox checkbox) {

        // Count the number of checked checkboxes

        // for (int i = 0; i < CallVbox.getChildren().size(); i++) {
        // HBox currentHBox = (HBox) CallVbox.getChildren().get(i);
        // CheckBox currentCheckBox = (CheckBox)currentHBox.getChildren().get(0);
        // if (currentCheckBox.isSelected()) {
        // checkedCount++;
        // }
        // }

        // If more than the allowed checkboxes are checked, uncheck the current checkbox

        if (checkedCount >= 3) {
            checkbox.setSelected(false);
            return;
        }
        if (checkbox.isSelected()) {
            checkedCount++;
            if (Call.linkCBtoDB.containsKey(checkbox)) {
                if (!endThread) {
                    Call.linkCBtoDB.get(checkbox).showWindow();
                    return;
                }
            }
            newThreadAdded = true;
            Call call = Call.CheckBoxAndCall.get(checkbox);
            Runnable dialoge = new DialogeBox(callNumber, phaser, call);
            Call.linkCBtoDB.put(checkbox, (DialogeBox) dialoge);

            // ((DialogeBox)dialoge).setupCall(call);

            executor.execute(dialoge);

        } else {
            checkedCount--;
            if (!endThread) {
                Call.linkCBtoDB.get(checkbox).closeWindow();
                return;
            }
            try {
                Call.linkCBtoDB.get(checkbox).exit();
            } catch (NullPointerException e) {
            }

        }
    }
    // *****************************************************************************************
    // */
    // End of DialogueBox code
    public static boolean running = true;

    @FXML
    void startbtnClicked(ActionEvent event) {
        try{
        // Check if the timer is not already running
        

        callCenter = new CallCenter(agents);
        System.out.println("casted");

        new Thread(() -> {
            phaser.register();
            while (running) {
                for (Customer customer : customers) {
                    customer.step();
                }

                for (Call call : Call.activeCalls) {
                    call.step();
                }
                Call.terminateCalls();
                callCenter.step();
                Timekeeper.step();

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    phaser.arriveAndAwaitAdvance();
                }
            }
        }).start();

        if (!timerTimeline.getStatus().equals(Timeline.Status.RUNNING)) {
            // Start the timer when the "Start" button is clicked
            timerTimeline.play();

            // Disable the "Start" button to prevent further clicks
            ((MenuItem) event.getSource()).setDisable(true);
            MenPasue.setDisable(false);
        }
        }
        catch(Exception e){
            showErrorAlert("Starting Simulation", "Environment is not Loaded");
        }
    }
}
