package no.uib.svm.libsvm.api.options;

import no.uib.svm.libsvm.core.libsvm.svm;
import no.uib.svm.libsvm.core.libsvm.svm_model;
import no.uib.svm.libsvm.core.svm_predict;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by kristianhestetun on 06.05.15.
 */
public class PredictionWrapper {

    private String
            modelFilePath,
            testDataFilePath;
    private svm_model model;

    private Boolean predictProbability = false;

    public void loadModel(String filePath) throws IOException {
        model = svm.svm_load_model(filePath);
    }

    public void predict() {
        BufferedReader inputReader = getTestDataReader();

        svm_predict.predict(
                inputReader, outputStream, this.model, predictProbability ? 1 : 0);
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

}
