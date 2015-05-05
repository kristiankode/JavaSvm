package no.uib.svm.gui;
/**
 * Created by kristianhestetun on 05.05.15.
 */

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import no.uib.svm.libsvm.api.options.TrainingWrapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class SUI extends Application implements Initializable {

    public static void main(String[] args) {
        launch(SUI.class, args);
    }

    @FXML
    private GridPane lol1;
    @FXML
    private Label selectedInputFileLabel;
    @FXML
    private Button trainBtn;

    private TrainingWrapper trainingWrapper;
    private Stage currentStage;

    @Override
    public void start(final Stage primaryStage) throws IOException {
        currentStage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("/SUI.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        trainingWrapper = new TrainingWrapper();
    }

    @FXML
    public void startTraining(final ActionEvent e){
        try {
            trainingWrapper.train();
            Logger.getAnonymousLogger().info("trained that shit");
        } catch (IOException e1) {
            Logger.getAnonymousLogger().info("Error while training.. " + e1.getMessage());
        }
    }

    @FXML
    public void handleButtonClick(final ActionEvent e) {
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(currentStage);
        if (file != null) {
            trainingWrapper.setInputFile(file.getAbsolutePath());
            selectedInputFileLabel.setText(trainingWrapper.getInputFile());
        }
    }
}
