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
import no.uib.svm.gui.input.InputUpdater;
import no.uib.svm.gui.input.KernelUpdater;
import no.uib.svm.gui.input.SvmTypeUpdater;
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
    private final SvmTypeUpdater svmTypeUpdater = new SvmTypeUpdater(this);
    private final KernelUpdater kernelUpdater = new KernelUpdater(this);

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
                        inputUpdater.updateKernelInput();
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
                        inputUpdater.updateSvmInput();
                    }
                }
        );

        svmTypeInput.getSelectionModel().selectLast();
    }

    private void updateTrainingEngine() {
        svmTypeUpdater.updateSVMTypeParams();
        kernelUpdater.updateKernelParams();
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

    public Kernel getSelectedKernel() {
        return selectedKernel;
    }

    public SvmType getSelectedSvmType() {
        return selectedSvmType;
    }

    public void setSvmTrainer(SvmTrainer svmTrainer) {
        this.svmTrainer = svmTrainer;
    }

    public void setSvmTester(SvmTester svmTester) {
        this.svmTester = svmTester;
    }
}
