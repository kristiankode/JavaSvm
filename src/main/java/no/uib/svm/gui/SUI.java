package no.uib.svm.gui;
/**
 * Created by kristianhestetun on 05.05.15.
 */

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import no.uib.svm.libsvm.api.options.TrainingWrapper;
import no.uib.svm.libsvm.api.options.kernel.*;
import no.uib.svm.libsvm.api.options.svmtype.*;

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
    @FXML
    private ChoiceBox<Kernel> kernelInput;
    @FXML
    private ChoiceBox<SvmType> svmTypeInput;

    @FXML
    private Label svmTypeLabel;

    // svm-params
    @FXML
    private TextField paramC;
    @FXML
    private TextField paramWeight;
    @FXML
    private TextField paramNrWeight;
    @FXML
    private TextField paramWeightLabel;
    @FXML
    private TextField paramNu;
    @FXML
    private TextField paramP;

    // kernel-params
    @FXML
    private TextField degreeInput;
    @FXML
    private TextField gammaInput;
    @FXML
    private TextField coef0Input;

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

        initKernelInput();
        initSvmTypeInput();

    }

    /**
     * Initializes the dropdown for selecting kernel
     */
    private void initKernelInput(){
        kernelInput.setItems(availableKernels());

        kernelInput.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        Kernel selectedKernel = availableKernels().get(newValue.intValue());
                        resetKernelParams();

                        if (selectedKernel instanceof PolynomialKernel) {
                            handlePolynomialKernelSelected((PolynomialKernel) selectedKernel);
                        } else if (selectedKernel instanceof SigmoidKernel){
                            handleSigmoidKernelSelected((SigmoidKernel) selectedKernel);
                        } else if (selectedKernel instanceof RadialBasisKernel){
                            handleRadialBasisKernelSelected((RadialBasisKernel) selectedKernel);
                        }

                    }
                }
        );

        kernelInput.getSelectionModel().selectLast();
    }

    /**
     * Initializes the dropdown for selecting svm-types
     */
    private void initSvmTypeInput(){
        svmTypeInput.setItems(availableSvmTypes());

        svmTypeInput.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        SvmType selectedType = availableSvmTypes().get(newValue.intValue());
                        svmTypeLabel.setText(selectedType.toString() + " parameters");
                        resetSvmParams();

                        if (selectedType instanceof C_SVC) {
                            handleCsvcSelected((C_SVC) selectedType);
                        } else if (selectedType instanceof NU_SVC) {
                            handleNuSvcSelected((NU_SVC) selectedType);
                        } else if (selectedType instanceof NU_SVR) {
                            handleNuSvrSelected((NU_SVR) selectedType);
                        } else if (selectedType instanceof EPSILON_SVR) {
                            handleEpsilonSvrSelected((EPSILON_SVR) selectedType);
                        } else if (selectedType instanceof OneClassSvm) {
                            handleOneClassSvmSelected((OneClassSvm) selectedType);
                        }

                    }
                }
        );

        svmTypeInput.getSelectionModel().selectLast();
    }

    private void handlePolynomialKernelSelected(PolynomialKernel kernel){
        updateInput(gammaInput, kernel.getGamma());
        updateInput(coef0Input, kernel.getCoef0());
        updateInput(degreeInput, kernel.getDegree());
    }

    private void handleRadialBasisKernelSelected(RadialBasisKernel kernel){
        updateInput(gammaInput, kernel.getGamma());
    }

    private void handleSigmoidKernelSelected(SigmoidKernel kernel){
        updateInput(gammaInput, kernel.getGamma());
        updateInput(coef0Input, kernel.getCoef0());
    }

    private void handleCsvcSelected(C_SVC svm){
        updateInput(paramC, svm.getC());
        updateInput(paramNrWeight, svm.getNr_weight());
        updateInput(paramWeight, svm.getWeight());
        updateInput(paramWeightLabel, svm.getWeight_label());
    }

    private void handleNuSvcSelected(NU_SVC svm) {
        updateInput(paramNu, svm.getNu());
    }

    private void handleOneClassSvmSelected(OneClassSvm svm){
        updateInput(paramNu, svm.getNu());
    }

    private void handleNuSvrSelected(NU_SVR svm){
        updateInput(paramNu, svm.getNu());
        updateInput(paramC, svm.getC());
    }

    private void handleEpsilonSvrSelected(EPSILON_SVR svm){
        updateInput(paramP, svm.getP());
        updateInput(paramC, svm.getC());
    }
    private void resetSvmParams(){
        clearInput(paramC);
        clearInput(paramNrWeight);
        clearInput(paramWeight);
        clearInput(paramWeightLabel);
        clearInput(paramNu);
        clearInput(paramP);
    }

    private void resetKernelParams(){
        clearInput(gammaInput);
        clearInput(coef0Input);
        clearInput(degreeInput);
    }

    private void clearInput(TextField input){
        input.setText("");
        input.setDisable(true);
    }

    private void updateInput(TextField input, Object value){
        input.setDisable(false);
        input.setText(String.valueOf(value));
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

    @FXML
    public ObservableList<Kernel> availableKernels(){
        return FXCollections.observableList(trainingWrapper.getAvailableKernels());
    }

    @FXML
    public ObservableList<SvmType> availableSvmTypes(){
        return FXCollections.observableList(trainingWrapper.getAvailableSvmTypes());
    }
}
