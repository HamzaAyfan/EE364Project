package com.ee364project.Fx;

import java.io.File;
import java.io.IOException;
//import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//import java.util.ArrayList;
import java.util.Optional;

import com.ee364project.Agent;
import com.ee364project.Customer;
import com.ee364project.HasData;
import com.ee364project.file_manage.Csv;
import com.ee364project.file_manage.ZipExtractor;
import com.ee364project.helpers.Utilities;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MainSceneController {

    @FXML
    private Text timeer;

    @FXML
    private AnchorPane anchorPane;
    
    @FXML
    private FlowPane flowPane;

    @FXML
    private VBox AgentVbox;

    @FXML
    private Button oldCostumersbButton = new Button();

    @FXML
    private Button newCostumersbButton = new Button();

    @FXML
    private Button startbButton = new Button();

    @FXML
    private Button pausebButton = new Button();

    @FXML
    private Button playbButton = new Button();




    //////////////// Timer methods (can be modified)//////////////
    private Timeline timerTimeline;
    private int secondsElapsed;

    @FXML
    public void initialize() {
        // Initialize the timer
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), this::updateTimer));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void updateTimer(ActionEvent event) {
        // Update the timer and display in the Text element
        secondsElapsed++;
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;
        timeer.setText(String.format("%02d:%02d", minutes, seconds));
    }




    ////////////////////////////////





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
                oldCosbtnClicked(primaryStage);
            } else if (response == costumeNoButtonType) {
                newCosbtnClicked();
            }
        });
    }

    void oldCosbtnClicked(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose zip file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Zip Files", "*.zip"));
        File zipFile = fileChooser.showOpenDialog(new Stage());

        if (zipFile != null) {
            String outputDirectory = "extracted";

            try {
                ZipExtractor.extractZip(zipFile, outputDirectory);
                processCsvFiles(outputDirectory);
                System.out.println("CSV files processed successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processCsvFiles(String extractedDirectory) {
        String[] csvFileNames = {"Customer.csv", "Department.csv", "Agent.csv", "Problem.csv"};

        for (String fileName : csvFileNames) {
            Path csvFilePath = Paths.get(extractedDirectory, fileName);

            // Now you can work with each CSV file
            // Example: Read the CSV file and perform some operations
            try {
                // Read the CSV file using Files.readAllLines
                //Files.readAllLines(csvFilePath).forEach(System.out::println);
                if(fileName.contains(csvFileNames[0])){
                    File customerFile = csvFilePath.toFile();
                    processCustomerFile(customerFile);
                }
                else if(fileName.contains(csvFileNames[2])){
                    File agentFile = csvFilePath.toFile();
                    processAgentFile(agentFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void oldCosbtnClicked(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        oldCosbtnClicked(stage);
    }
    


    @FXML
    private void processCustomerFile(File selectedFile) throws IOException {
        try{

            HasData[] customers = Csv.read(selectedFile.getAbsolutePath()); //Done
            int number = customers.length;
            //System.out.println(number);

            if(flowPane.getChildren().size() != 0){ flowPane.getChildren().clear();}

            for (int i = 0; i < number; i++) {
                Image image = new Image("com\\ee364project\\Fx\\resources\\user.png", true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(20);
                imageView.setFitHeight(20);

                StackPane stackPane = new StackPane();

                Rectangle rectangle = new Rectangle(20, 20, Color.TRANSPARENT);         
                //rectangle.setStyle("-fx-fill: green;");
                //rectangle.setFill(new ImagePattern(image));
                stackPane.getChildren().addAll(imageView, rectangle);    
                //rectangle.setStyle("-fx-fill: green;");

                Customer customer = (Customer) customers[i];
                addTooltip(rectangle, customer.getStringInfo());

                flowPane.getChildren().add(stackPane);
            }
            System.out.println("Finished loading FP");


        } catch (NumberFormatException e) {
            System.out.println("Please enter a a valid CSV file.");
        }


    }
    
    @FXML
    private void processAgentFile(File selectedFile) throws IOException {
        try{

            HasData[] agents = Csv.read(selectedFile.getAbsolutePath()); //Done
            int number = agents.length;
            //System.out.println(number);

            if(AgentVbox.getChildren().size() != 0){ AgentVbox.getChildren().clear();}

            for (int i = 0; i < number; i++) {
                Image image = new Image("com\\ee364project\\Fx\\resources\\agent.png", true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(20);
                imageView.setFitHeight(20);

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


        } catch (NumberFormatException e) {
            System.out.println("Please enter a a valid CSV file.");
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
        inputDialog.setHeaderText("Enter two integers");
        inputDialog.initStyle(StageStyle.UNDECORATED);
        inputDialog.initModality(Modality.APPLICATION_MODAL);
        inputDialog.initOwner(newCostumersbButton.getScene().getWindow());

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

        // Set the content of the TextInputDialog to the GridPane
        inputDialog.getDialogPane().setContent(gridPane);

        // Show and wait for user response
        Optional<String> result = inputDialog.showAndWait();

        result.ifPresent(values -> {
            try {
                int numberOfCustomers = Integer.parseInt(textField1.getText());
                int numberOfAgents = Integer.parseInt(textField2.getText());

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
              
        HasData[] customers = Utilities.getFakeData(number, "Customer");
        
        for (int i = 0; i < number; i++) {
            Image image = new Image("com\\ee364project\\Fx\\resources\\user.png", true);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(20);
            imageView.setFitHeight(20);

            StackPane stackPane = new StackPane();

            Rectangle rectangle = new Rectangle(20, 20, Color.TRANSPARENT);         
            //rectangle.setStyle("-fx-fill: green;");
            //rectangle.setFill(new ImagePattern(image));
            stackPane.getChildren().addAll(imageView, rectangle);    
            //rectangle.setStyle("-fx-fill: green;");

            Customer customer = (Customer) customers[i];
            addTooltip(rectangle, customer.getStringInfo());

            flowPane.getChildren().add(stackPane);

            //System.out.println(((Customer) customers[i]).getStringInfo()); Done
        }
    }

    private void generateNewAgents(int number) {
        if(AgentVbox.getChildren().size() != 0){ AgentVbox.getChildren().clear();}
              
        HasData[] agents = Utilities.getFakeData(number, "Agent");
        
        for (int i = 0; i < number; i++) {
            Image image = new Image("com\\ee364project\\Fx\\resources\\agent.png", true);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(20);
            imageView.setFitHeight(20);

            StackPane stackPane = new StackPane();

            Rectangle rectangle = new Rectangle(20, 20, Color.TRANSPARENT);         
            //rectangle.setStyle("-fx-fill: green;");
            //rectangle.setFill(new ImagePattern(image));
            stackPane.getChildren().addAll(imageView, rectangle);    
            //rectangle.setStyle("-fx-fill: green;");

            Agent agent = (Agent) agents[i];
            addTooltip(rectangle, agent.getStringInfo());

            AgentVbox.getChildren().add(stackPane);

            //System.out.println(((Customer) customers[i]).getStringInfo()); Done
        }
    }
    
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
        ((Button) event.getSource()).setDisable(true);
    }
    }

}



