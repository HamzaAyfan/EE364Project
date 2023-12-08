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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
//import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import com.ee364project.Agent;
import com.ee364project.Call;
import com.ee364project.CallCenter;
import com.ee364project.Customer;
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
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
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

    Image customerImage = new Image("com\\ee364project\\Fx\\resources\\user.png");
    Image agentImage = new Image("com\\ee364project\\Fx\\resources\\agent.png");

    
    
    //************************Mshari Edit****************************** *//
    private CallCenter callCenter;
   
       //////////////// Timer methods from TimeKeeper Class//////////////
    private Timeline timerTimeline;
    //private Timekeeper timekeeper;

    @FXML
    public void initialize() {
        Call.vBox = CallVbox;
        CallVbox.setSpacing(4);
        AgentVbox.setSpacing(4);
        
        //timekeeper = new Timekeeper();
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), this::updateTimer));
        timerTimeline.setCycleCount(Timeline.INDEFINITE); 

        try {
            loadRecentFiles();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void updateTimer(ActionEvent event) {
        Timekeeper.step();
        LocalDateTime properTime = Timekeeper.getProperTime();
        int minutes = properTime.getMinute();
        int seconds = properTime.getSecond();
        // timeer.setText(String.format("%02d:%02d", minutes, seconds));
        timeer.setText(properTime.toString());
    }
    ///////////////////////////////

    


    


    
       





    public void showYesNoDialog(Stage primaryStage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Simulation Type");
        alert.setHeaderText("How would you like to start the simulation?");
        alert.initStyle(StageStyle.UNIFIED);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(primaryStage);

        //Create Costume buttons
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



    void oldCosbtnClicked() {
        try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose zip file");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Zip Files", "*.zip"));
            File zipFile = fileChooser.showOpenDialog(new Stage());
            handleOpenFile(zipFile);
        }
        catch(Exception e){

        }

        
    }
    
    public void handleOpenFile(File zipFile){
        System.out.println("Handling now");
        // Check if the file is already in the recent files list
        if (!recentFiles.stream().anyMatch(existingFile -> existingFile.getAbsolutePath().equals(zipFile.getAbsolutePath()))) {
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
            // open file process
            if (zipFile != null) {
                System.out.println("Opening");

                String outputDirectory = "extracted";

                try {
                    Zip.extractZip(zipFile, outputDirectory);
                    processCsvFiles(outputDirectory);
                    System.out.println("CSV files processed successfully.");

                    Zip.deleteExtracted(outputDirectory);

                    //recentFiles.add(zipFile);
                    //updateRecentFilesMenu();
//
                    //saveRecentFiles();

                } catch (IOException e) {
                    e.printStackTrace();

                } 
            }
        

    }

    private void processCsvFiles(String extractedDirectory) {
        String[] csvFileNames = { "Problem.csv", "Customer.csv", "Agent.csv", "Department.csv"};


        for (String fileName : csvFileNames) {
            Path csvFilePath = Paths.get(extractedDirectory, fileName);

            try {
                
                if(fileName.contains(csvFileNames[0])){
                    File problemFile = csvFilePath.toFile();
                    processProblemFile(problemFile);
                }
                else if(fileName.contains(csvFileNames[1])){
                    File customerFile = csvFilePath.toFile();
                    processCustomerFile(customerFile);
                }
                else if(fileName.contains(csvFileNames[2])){
                    File agentFile = csvFilePath.toFile();
                    processAgentFile(agentFile);
                } else if(fileName.contains(csvFileNames[3])) {
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
        //Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {
            oldCosbtnClicked();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    


    @FXML
    private void processCustomerFile(File selectedFile) throws IOException {
        try{

            HasData[] customersCSV = Csv.read(selectedFile.getAbsolutePath()); //Done
            customers = new Customer[customersCSV.length];

            int j = 0;
            for(HasData customer: customersCSV){
                customers[j] = (Customer) customer;
                j = j + 1;
            }
            System.out.println("Customers creeated");

            if(flowPane.getChildren().size() != 0){ flowPane.getChildren().clear();}

            for (int i = 0; i < customers.length; i++) {
                
                ImageView imageView = new ImageView(customerImage);
                imageView.setFitWidth(20);
                imageView.setFitHeight(20);

                StackPane stackPane = new StackPane();

                Rectangle rectangle = new Rectangle(20, 20, Color.TRANSPARENT);         
                //rectangle.setStyle("-fx-fill: green;");
                //rectangle.setFill(new ImagePattern(image));
                stackPane.getChildren().addAll(imageView, rectangle);    
                //rectangle.setStyle("-fx-fill: green;");

                addTooltip(rectangle, customers[i].getStringInfo());

                flowPane.getChildren().add(stackPane);
            }
            System.out.println("Finished loading FP");


        } catch (NumberFormatException e) {
            System.out.println("Please enter a a valid CSV file.");
        }
        finally{
            
        }


    }
    
    @FXML
    private void processAgentFile(File selectedFile) throws IOException {
        try{

            HasData[] agentsCSV = Csv.read(selectedFile.getAbsolutePath()); //Done
            agents = new Agent[agentsCSV.length];
            
            int j = 0;
            for(HasData agent: agentsCSV){
                agents[j] = (Agent) agent;
                j = j + 1;
            }

            System.out.println("Agents creeated");

            if(AgentVbox.getChildren().size() != 0){ AgentVbox.getChildren().clear();}

            for (int i = 0; i < agents.length; i++) {
            
                ImageView imageView = new ImageView(agentImage);
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);

                StackPane stackPane = new StackPane();

                Rectangle rectangle = new Rectangle(20, 20, Color.TRANSPARENT);         
                //rectangle.setStyle("-fx-fill: green;");
                //rectangle.setFill(new ImagePattern(image));
                stackPane.getChildren().addAll(imageView, rectangle);    
                //rectangle.setStyle("-fx-fill: green;");

                Agent agent = (Agent) agents[i];
                addTooltip(rectangle, agent.getStringInfo());

                AgentVbox.getChildren().add(stackPane);
            }
            System.out.println("Finished loading FP");


        } catch (Exception e) {
            System.out.println("Please enter a a valid CSV file.");
        }

    }

    public void processProblemFile(File problemFile){
        try{
            // Utilities.getFakeData(5, Vars.DataClasses.Department);
            problems = Csv.read(problemFile.getAbsolutePath(), Vars.DataClasses.Problem);
            System.out.println("Problem loaded");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void processDepartmentFile(File departmentFile){
        try{
            // Utilities.getFakeData(5, Vars.DataClasses.Department);
            departments = Csv.read(departmentFile.getAbsolutePath(), Vars.DataClasses.Department);
            System.out.println("Problem loaded");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }

    @FXML
    private void addTooltip(Rectangle rectangle, String tooltipText) {
        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(rectangle, tooltip);

        rectangle.setOnMouseEntered((MouseEvent event) -> {
            tooltip.show(rectangle, event.getScreenX(), event.getScreenY() + 15);
        });

        rectangle.setOnMouseExited((MouseEvent event) -> {
            tooltip.hide();
        });
    }
    
    
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

                // generate fake problems and departments
                departments = Utilities.getFakeData(numberOfDepartments, Vars.DataClasses.Department);
                problems = Utilities.getFakeData(numberOfProblems, Vars.DataClasses.Problem);

                // generate fake customers and agents
                generateNewCustomers(numberOfCustomers);
                generateNewAgents(numberOfAgents);
                
            } catch (NumberFormatException e) {
                showErrorAlert("Invalid Input", "Please enter a valid integer.");
            }
        });
    }

    @FXML
    void newCosbtnClicked(ActionEvent event) {
        newCosbtnClicked();
    }

    private void generateNewCustomers(int number) {
        if(flowPane.getChildren().size() != 0){ flowPane.getChildren().clear();}
        
        customers = new Customer[number];
        
        int i = 0;
        for (HasData datum : Utilities.getFakeData(number, Vars.DataClasses.Customer)) {
            customers[i] = (Customer) datum;

            
            ImageView imageView = new ImageView(customerImage);
            imageView.setFitWidth(20);
            imageView.setFitHeight(20);

            StackPane stackPane = new StackPane();

            Rectangle rectangle = new Rectangle(20, 20, Color.TRANSPARENT);         
            //rectangle.setStyle("-fx-fill: green;");
            //rectangle.setFill(new ImagePattern(image));
            stackPane.getChildren().addAll(imageView, rectangle);    
            //rectangle.setStyle("-fx-fill: green;");

            //Customer customer = customers[i];
            
            addTooltip(rectangle, ((Customer) customers[i]).getStringInfo());

            flowPane.getChildren().add(stackPane);

            i = i + 1;
            
            //System.out.println(((Customer) customers[i]).getStringInfo()); Done
        }
    }

    private void generateNewAgents(int number) {
        if(AgentVbox.getChildren().size() != 0){ AgentVbox.getChildren().clear();}
              
        //HasData[] agents = Utilities.getFakeData(number, Vars.DataClasses.Agent);
        agents = new Agent[number];

        int i = 0;
        for (HasData datum : Utilities.getFakeData(number, Vars.DataClasses.Agent)) {
            agents[i] = (Agent) datum;

            
            ImageView imageView = new ImageView(agentImage);
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);

            StackPane stackPane = new StackPane();

            Rectangle rectangle = new Rectangle(20, 20, Color.TRANSPARENT);         
            //rectangle.setStyle("-fx-fill: green;");
            //rectangle.setFill(new ImagePattern(image));
            stackPane.getChildren().addAll(imageView, rectangle);    
            //rectangle.setStyle("-fx-fill: green;");

            //Agent agent = (Agent) agents[i];
            addTooltip(rectangle, ((Agent) agents[i]).getStringInfo());

            AgentVbox.getChildren().add(stackPane);

            i = i + 1;
            //System.out.println(((Customer) customers[i]).getStringInfo()); Done
        }
        //CallCenter callCenter = new CallCenter(agents);
        System.out.println("Finished agents");
    }
    
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void saveAsbtnClicked(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP Files", "*.zip"));

        // Show Save As dialog
        File saveFile = fileChooser.showSaveDialog(new Stage());

        if (saveFile != null) {
            // Get the chosen file path from the user
            String zipFilePath = saveFile.getAbsolutePath();

            // write the csvfiles in the output directory
            Csv.write((HasData[]) customers, "call_center/output/Customer.csv");
            Csv.write((HasData[]) agents, "call_center/output/Agent.csv");
            Csv.write((HasData[]) Problem.getAllProblems(), "call_center/output/Problem.csv");
            Department[] departments = Department.getAllDepartments().values().toArray(new Department[Department.getAllDepartments().size()]);
            Csv.write(departments, "call_center/output/Department.csv");


            // Perform the saving logic
            Zip.compressToZip(zipFilePath, "call_center/output"); // generate the zip file
            Zip.deleteExtracted("call_center/output"); // delete the extraction directory
            
            System.out.println("ZIP file saved to: " + zipFilePath);
        }

    }

    // Handling Open Recent

    @FXML
    private void handleOpenRecent(MenuItem menuItem) {
        File selectedFile = (File) menuItem.getUserData();
        handleOpenFile(selectedFile);
    }
    
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

    private void loadRecentFiles() throws IOException {
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
        } catch(FileNotFoundException e){
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(RECENT_FILES_FILE))){
                
            }
        }
         
        catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    @FXML
    void startbtnClicked(ActionEvent event) {
        // Check if the timer is not already running
        if (!timerTimeline.getStatus().equals(Timeline.Status.RUNNING)) {
            // Start the timer when the "Start" button is clicked
            timerTimeline.play();

            // Disable the "Start" button to prevent further clicks
            ((MenuItem) event.getSource()).setDisable(true);
        }

        callCenter = new CallCenter(agents);
        System.out.println("casted");

        new Thread(() -> {
            while (true) {
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
                }
            }}).start();
    }

}



