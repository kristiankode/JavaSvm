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
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import no.uib.svm.libsvm.api.options.PredictionWrapper;
import no.uib.svm.libsvm.api.options.TrainingWrapper;
import no.uib.svm.libsvm.api.options.kernel.*;
import no.uib.svm.libsvm.api.options.svmtype.*;
import no.uib.svm.libsvm.core.libsvm.Model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class SUI extends Application implements Initializable {

    public static void main(String[] args) {
        launch(SUI.class, args);
    }

    @FXML
    private Label selectedInputFileLabel;
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

    // prediction-params
    @FXML
    private Label testDataLabel;
    @FXML
    private Label modelLabel;
    private String predictionOutputFilePath;

    @FXML
    private Button predictBtn;

    // display model
    @FXML
    private TableView classTable;
    @FXML
    private Label numberOfClassesLabel;

    private TrainingWrapper trainingWrapper;
    private PredictionWrapper predictionWrapper;
    private Stage currentStage;

    private Kernel selectedKernel;
    private SvmType selectedSvmType;

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
        predictionWrapper = new PredictionWrapper();

        initKernelInput();
        initSvmTypeInput();
    }

    /**
     * Initializes the dropdown for selecting kernel
     */
    private void initKernelInput() {
        kernelInput.setItems(availableKernels());

        kernelInput.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        selectedKernel = availableKernels().get(newValue.intValue());
                        resetKernelParams();

                        if (selectedKernel instanceof PolynomialKernel) {
                            handlePolynomialKernelSelected((PolynomialKernel) selectedKernel);
                        } else if (selectedKernel instanceof SigmoidKernel) {
                            handleSigmoidKernelSelected((SigmoidKernel) selectedKernel);
                        } else if (selectedKernel instanceof RadialBasisKernel) {
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
    private void initSvmTypeInput() {
        svmTypeInput.setItems(availableSvmTypes());

        svmTypeInput.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        selectedSvmType = availableSvmTypes().get(newValue.intValue());
                        svmTypeLabel.setText(selectedSvmType.toString() + " parameters");
                        resetSvmParams();

                        if (selectedSvmType instanceof CSvc) {
                            handleCsvcSelected((CSvc) selectedSvmType);
                        } else if (selectedSvmType instanceof NuSvc) {
                            handleNuSvcSelected((NuSvc) selectedSvmType);
                        } else if (selectedSvmType instanceof NuSvr) {
                            handleNuSvrSelected((NuSvr) selectedSvmType);
                        } else if (selectedSvmType instanceof EpsilonSvr) {
                            handleEpsilonSvrSelected((EpsilonSvr) selectedSvmType);
                        } else if (selectedSvmType instanceof OneClassSvm) {
                            handleOneClassSvmSelected((OneClassSvm) selectedSvmType);
                        }

                    }
                }
        );

        svmTypeInput.getSelectionModel().selectLast();
    }

    private void handlePolynomialKernelSelected(PolynomialKernel kernel) {
        updateInput(gammaInput, kernel.getGamma());
        updateInput(coef0Input, kernel.getCoef0());
        updateInput(degreeInput, kernel.getDegree());
    }

    /**
     * Takes data from the text input and stores it in a kernel object
     */
    public void updateKernelParams() {
        if (selectedKernel instanceof PolynomialKernel) {
            updatePolynomialKernel((PolynomialKernel) selectedKernel);
        } else if (selectedKernel instanceof RadialBasisKernel) {
            updateRadialBasisKernel((RadialBasisKernel) selectedKernel);
        } else if (selectedKernel instanceof SigmoidKernel) {
            updateSigmoidKernel((SigmoidKernel) selectedKernel);
        }
    }

    public void updateSVMTypeParams() {
        if (selectedSvmType instanceof CSvc) {
            updateCSVCType((CSvc) selectedSvmType);
        } else if (selectedSvmType instanceof EpsilonSvr) {
            updateEpsilonSVRType((EpsilonSvr) selectedSvmType);
        } else if (selectedSvmType instanceof NuSvc) {
            updateNUSVCType((NuSvc) selectedSvmType);
        } else if (selectedSvmType instanceof NuSvr) {
            updateNUSVRType((NuSvr) selectedSvmType);
        } else if (selectedSvmType instanceof OneClassSvm) {
            updateOneClassSVM((OneClassSvm) selectedSvmType);
        }
    }

    private void updateOneClassSVM(OneClassSvm svm) {
        svm.setNu(Double.parseDouble(paramNu.getText())); //TODO: check if correct
    }

    private void updateNUSVRType(NuSvr svm) {
        svm.setNu(Double.parseDouble(paramNu.getText())); //TODO: check if correct
        svm.setC(Double.parseDouble(paramC.getText())); //TODO: check if correct
    }

    private void updateNUSVCType(NuSvc svm) {
        svm.setNu(Double.parseDouble(paramNu.getText())); //TODO: check if correct
    }

    private void updateEpsilonSVRType(EpsilonSvr svm) {
        svm.setC(Double.parseDouble(paramC.getText())); //TODO: Check if correct
        svm.setP(Double.parseDouble(paramP.getText())); //TODO: Check if correct
    }

    private void updateCSVCType(CSvc svm) {
        svm.setC(Double.parseDouble(paramC.getText())); //TODO: Check if correct
        svm.setNr_weight(Integer.parseInt(paramNrWeight.getText()));
        svm.setWeight(convertToDoubleArray(paramWeight.getText()));
        svm.setWeight_label(convertToIntArray(paramWeightLabel.getText()));

    }

    private int[] convertToIntArray(String text) {
        String[] stringArray = text.split(",");
        int[] array = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            array[i] = Integer.parseInt(stringArray[i].trim());
        }
        return array;
    }

    private double[] convertToDoubleArray(String text) {
        String[] stringArray = text.split(",");
        double[] array = new double[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            array[i] = Double.parseDouble(stringArray[i].trim());
        }
        return array;
    }


    /**
     * Takes data from the text inputs and stores it in a Sigmoid kernel Object
     *
     * @param kernel
     */
    private void updateSigmoidKernel(SigmoidKernel kernel) {
        kernel.setGamma(Double.parseDouble(gammaInput.getText()));
        kernel.setCoef0(Double.parseDouble(coef0Input.getText()));
    }


    /**
     * Takes data from the text input and stores it in a Radial Basis Kernel object
     *
     * @param kernel
     */
    private void updateRadialBasisKernel(RadialBasisKernel kernel) {
        kernel.setGamma(Double.parseDouble(gammaInput.getText()));
    }

    /**
     * Takes data from the text inputs and stores it in a polynomial kernel object
     *
     * @param kernel
     */
    private void updatePolynomialKernel(PolynomialKernel kernel) {
        kernel.setCoef0(Double.parseDouble(coef0Input.getText()));
        kernel.setGamma(Double.parseDouble(gammaInput.getText()));
        kernel.setDegree(kernel.getDegree());
    }

    private void handleRadialBasisKernelSelected(RadialBasisKernel kernel) {
        updateInput(gammaInput, kernel.getGamma());
    }

    private void handleSigmoidKernelSelected(SigmoidKernel kernel) {
        updateInput(gammaInput, kernel.getGamma());
        updateInput(coef0Input, kernel.getCoef0());
    }

    private void handleCsvcSelected(CSvc svm) {
        updateInput(paramC, svm.getC());
        updateInput(paramNrWeight, svm.getNr_weight());
        updateInput(paramWeight, svm.getWeight());
        updateInput(paramWeightLabel, svm.getWeight_label());
    }

    private void handleNuSvcSelected(NuSvc svm) {
        updateInput(paramNu, svm.getNu());
    }

    private void handleOneClassSvmSelected(OneClassSvm svm) {
        updateInput(paramNu, svm.getNu());
    }

    private void handleNuSvrSelected(NuSvr svm) {
        updateInput(paramNu, svm.getNu());
        updateInput(paramC, svm.getC());
    }

    private void handleEpsilonSvrSelected(EpsilonSvr svm) {
        updateInput(paramP, svm.getP());
        updateInput(paramC, svm.getC());
    }

    private void resetSvmParams() {
        clearInput(paramC);
        clearInput(paramNrWeight);
        clearInput(paramWeight);
        clearInput(paramWeightLabel);
        clearInput(paramNu);
        clearInput(paramP);
    }

    private void resetKernelParams() {
        clearInput(gammaInput);
        clearInput(coef0Input);
        clearInput(degreeInput);
    }

    private void clearInput(TextField input) {
        input.setText("");
        input.setDisable(true);
    }

    private void updateInput(TextField input, Object value) {
        input.setDisable(false);
        input.setText(String.valueOf(value));
    }

    private void updateTrainingEngine() {
        updateSVMTypeParams();
        updateKernelParams();
        trainingWrapper.setSelectedKernel(selectedKernel);
        trainingWrapper.setSelectedSvmType(selectedSvmType);
    }

    @FXML
    public void startTraining(final ActionEvent e) {
        updateTrainingEngine();
        try {
            Model model = trainingWrapper.train();
            if(model != null) {
                Logger.getAnonymousLogger().info("trained that shit");
                predictionWrapper.setModel(model);
                modelLabel.setText(model.toString());
            }
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
    public void handleTestDataBtnClick(final ActionEvent e) {
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(currentStage);
        if (file != null) {
            predictionWrapper.setTestDataFilePath(file.getAbsolutePath());
            testDataLabel.setText(predictionWrapper.getTestDataFilePath());

            predictionOutputFilePath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + ".prediction";
            predictionWrapper.setOutputFilePath(predictionOutputFilePath);
        }
    }

    @FXML
    public void handleModelBtnClick(final ActionEvent e) {
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(currentStage);
        if (file != null) {
            try {
                predictionWrapper.loadModel(file.getAbsolutePath());
                modelLabel.setText(predictionWrapper.getModel().toString());
                updateModelInfo(predictionWrapper.getModel());
            } catch (IOException e1) {
                Logger.getAnonymousLogger().info("Error while loading file.. " + e1.getMessage());
            }
            testDataLabel.setText(predictionWrapper.getTestDataFilePath());
        }
    }

    private void updateModelInfo(Model model){
        numberOfClassesLabel.setText(String.valueOf(model.nr_class));

        updateClassTableWithModel();
    }

    private void updateClassTableWithModel(){
        List<Integer> labelList = intArrayToList(predictionWrapper.getModel().label);
        ObservableList<Integer> labels = FXCollections.observableArrayList(labelList);
        classTable.setItems(labels);
    }

    public List<Integer> intArrayToList(int[] intArr){
        List<Integer> integerList = new ArrayList<Integer>();
        for(int i : intArr){
            integerList.add(i);
        }
        return integerList;
    }

    @FXML
    public void predict(){
        try {
            predictionWrapper.predict();
            Logger.getAnonymousLogger().info("Predicted that shit, prediction saved in " + predictionOutputFilePath);
        } catch (IOException e) {
            Logger.getAnonymousLogger().info("Error while predicting.." + e.getMessage());
        }
    }

    @FXML
    public ObservableList<Kernel> availableKernels() {
        return FXCollections.observableList(trainingWrapper.getAvailableKernels());
    }

    @FXML
    public ObservableList<SvmType> availableSvmTypes() {
        return FXCollections.observableList(trainingWrapper.getAvailableSvmTypes());
    }
}
