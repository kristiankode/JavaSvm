package no.uib.svm.gui;
/**
 * GUI for svm
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
import no.uib.svm.gui.utils.StringToArray;
import no.uib.svm.libsvm.api.options.SvmFactoryImpl;
import no.uib.svm.libsvm.api.options.kernel.Kernel;
import no.uib.svm.libsvm.api.options.kernel.PolynomialKernel;
import no.uib.svm.libsvm.api.options.kernel.RadialBasisKernel;
import no.uib.svm.libsvm.api.options.kernel.SigmoidKernel;
import no.uib.svm.libsvm.api.options.svmtype.*;
import no.uib.svm.libsvm.api.options.testing.SvmTester;
import no.uib.svm.libsvm.api.options.training.SvmTrainer;
import no.uib.svm.libsvm.core.libsvm.Model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class SUI extends Application implements Initializable {

    private final InputUpdater inputUpdater = new InputUpdater(this);

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

    private Stage currentStage;

    // dependencies
    private SvmTrainer svmTrainer;
    private SvmTester svmTester;

    // selection
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
        svmTrainer = SvmFactoryImpl.getTrainer();
        svmTester = SvmFactoryImpl.getTester();

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
                        inputUpdater.resetKernelParams();

                        if (selectedKernel instanceof PolynomialKernel) {
                            handlePolynomialKernelSelected((PolynomialKernel) selectedKernel);
                        } else if (selectedKernel instanceof SigmoidKernel) {
                            inputUpdater.handleSigmoidKernelSelected((SigmoidKernel) selectedKernel);
                        } else if (selectedKernel instanceof RadialBasisKernel) {
                            inputUpdater.handleRadialBasisKernelSelected((RadialBasisKernel) selectedKernel);
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
                        inputUpdater.resetSvmParams();

                        if (selectedSvmType instanceof CSvc) {
                            inputUpdater.handleCsvcSelected((CSvc) selectedSvmType);
                        } else if (selectedSvmType instanceof NuSvc) {
                            inputUpdater.handleNuSvcSelected((NuSvc) selectedSvmType);
                        } else if (selectedSvmType instanceof NuSvr) {
                            inputUpdater.handleNuSvrSelected((NuSvr) selectedSvmType);
                        } else if (selectedSvmType instanceof EpsilonSvr) {
                            inputUpdater.handleEpsilonSvrSelected((EpsilonSvr) selectedSvmType);
                        } else if (selectedSvmType instanceof OneClassSvm) {
                            inputUpdater.handleOneClassSvmSelected((OneClassSvm) selectedSvmType);
                        }

                    }
                }
        );

        svmTypeInput.getSelectionModel().selectLast();
    }

    private void handlePolynomialKernelSelected(PolynomialKernel kernel) {
        inputUpdater.updateInput(gammaInput, kernel.getGamma());
        inputUpdater.updateInput(coef0Input, kernel.getCoef0());
        inputUpdater.updateInput(degreeInput, kernel.getDegree());
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
        svm.setNu(Double.parseDouble(paramNu.getText()));
    }

    private void updateNUSVRType(NuSvr svm) {
        svm.setNu(Double.parseDouble(paramNu.getText()));
        svm.setC(Double.parseDouble(paramC.getText()));
    }

    private void updateNUSVCType(NuSvc svm) {
        svm.setNu(Double.parseDouble(paramNu.getText()));
    }

    private void updateEpsilonSVRType(EpsilonSvr svm) {
        svm.setC(Double.parseDouble(paramC.getText()));
        svm.setP(Double.parseDouble(paramP.getText()));
    }

    private void updateCSVCType(CSvc svm) {
        svm.setC(Double.parseDouble(paramC.getText()));
        svm.setNr_weight(Integer.parseInt(paramNrWeight.getText()));
        svm.setWeight(StringToArray.convertToDoubleArray(paramWeight.getText()));
        svm.setWeight_label(StringToArray.convertToIntArray(paramWeightLabel.getText()));
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

    private void updateTrainingEngine() {
        updateSVMTypeParams();
        updateKernelParams();
        svmTrainer.setSelectedKernel(selectedKernel);
        svmTrainer.setSelectedSvmType(selectedSvmType);
    }

    @FXML
    public void startTraining(final ActionEvent e) {
        updateTrainingEngine();
        try {
            Model model = svmTrainer.train();
            if(model != null) {
                Logger.getAnonymousLogger().info("trained that shit");
                svmTester.setModel(model);
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
            svmTrainer.setInputFile(file.getAbsolutePath());
            selectedInputFileLabel.setText(svmTrainer.getInputFile());
        }
    }

    @FXML
    public void handleTestDataBtnClick(final ActionEvent e) {
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(currentStage);
        if (file != null) {
            svmTester.setTestDataFilePath(file.getAbsolutePath());
            testDataLabel.setText(svmTester.getTestDataFilePath());

            predictionOutputFilePath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + ".prediction";
            svmTester.setOutputFilePath(predictionOutputFilePath);
        }
    }

    @FXML
    public void handleModelBtnClick(final ActionEvent e) {
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(currentStage);
        if (file != null) {
            try {
                svmTester.loadModel(file.getAbsolutePath());
                modelLabel.setText(svmTester.getModel().toString());
                updateModelInfo(svmTester.getModel());
            } catch (IOException e1) {
                Logger.getAnonymousLogger().info("Error while loading file.. " + e1.getMessage());
            }
            testDataLabel.setText(svmTester.getTestDataFilePath());
        }
    }

    private void updateModelInfo(Model model){
        numberOfClassesLabel.setText(String.valueOf(model.getNr_class()));
        updateClassTableWithModel();
    }

    private void updateClassTableWithModel(){
        List<Integer> labelList = intArrayToList(svmTester.getModel().getLabel());
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
            svmTester.predict();
            Logger.getAnonymousLogger().info("Predicted that shit, prediction saved in " + predictionOutputFilePath);
        } catch (IOException e) {
            Logger.getAnonymousLogger().info("Error while predicting.." + e.getMessage());
        }
    }

    @FXML
    public ObservableList<Kernel> availableKernels() {
        return FXCollections.observableList(svmTrainer.getAvailableKernels());
    }

    @FXML
    public ObservableList<SvmType> availableSvmTypes() {
        return FXCollections.observableList(svmTrainer.getAvailableSvmTypes());
    }

    public TextField getParamC() {
        return paramC;
    }

    public TextField getParamWeight() {
        return paramWeight;
    }

    public TextField getParamNrWeight() {
        return paramNrWeight;
    }

    public TextField getParamWeightLabel() {
        return paramWeightLabel;
    }

    public TextField getParamNu() {
        return paramNu;
    }

    public TextField getParamP() {
        return paramP;
    }

    public TextField getDegreeInput() {
        return degreeInput;
    }

    public TextField getGammaInput() {
        return gammaInput;
    }

    public TextField getCoef0Input() {
        return coef0Input;
    }

    public void setSvmTrainer(SvmTrainer svmTrainer) {
        this.svmTrainer = svmTrainer;
    }

    public void setSvmTester(SvmTester svmTester) {
        this.svmTester = svmTester;
    }
}
