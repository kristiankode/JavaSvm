package no.uib.svm.libsvm.api.options;

import no.uib.svm.libsvm.core.libsvm.svm;
import no.uib.svm.libsvm.core.libsvm.Model;
import no.uib.svm.libsvm.core.SvmTester;

import java.io.*;
import java.util.logging.Logger;

/**
 * Created by kristianhestetun on 06.05.15.
 */
public class PredictionWrapper {

    private String
            modelFilePath,
            testDataFilePath,
            outputFilePath;
    private Model model;

    private Boolean predictProbability = false;

    public void loadModel(String filePath) throws IOException {
        model = svm.svm_load_model(filePath);
    }

    public void predict() throws IOException {
        BufferedReader inputReader = getTestDataReader();

        SvmTester.predict(
                inputReader, getOutputStream(), this.model, booleanToInt(predictProbability));
    }

    public BufferedReader getTestDataReader() {
        if (testDataFilePath != null && !testDataFilePath.isEmpty()) {
            try {
                return new BufferedReader(new FileReader(testDataFilePath));
            } catch (FileNotFoundException e) {
                Logger.getAnonymousLogger().info("couldnt find specified file.." + testDataFilePath);
            }
        }
        return null;
    }

    private DataOutputStream getOutputStream() throws FileNotFoundException {
        return new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(outputFilePath)));
    }

    int booleanToInt(boolean bool) {
        return bool ? 1 : 0;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getModelFilePath() {
        return modelFilePath;
    }

    public void setModelFilePath(String modelFilePath) {
        this.modelFilePath = modelFilePath;
    }

    public void setTestDataFilePath(String filePath) {
        this.testDataFilePath = filePath;
    }

    public String getTestDataFilePath() {
        return testDataFilePath;
    }

    public Boolean getPredictProbability() {
        return predictProbability;
    }

    public void setPredictProbability(Boolean predictProbability) {
        this.predictProbability = predictProbability;
    }
}
