package no.uib.svm.libsvm.api.options.testing;

import no.uib.svm.libsvm.api.options.logging.Messages;
import no.uib.svm.libsvm.core.LibSvmTester;
import no.uib.svm.libsvm.core.libsvm.Model;
import no.uib.svm.libsvm.core.libsvm.PrintInterface;
import no.uib.svm.libsvm.core.libsvm.svm;

import java.io.*;

/**
 * Class for testing the performance of a model.
 */
public class TestingWrapper implements SvmTester {

    private String
            modelFilePath,
            testDataFilePath,
            outputFilePath;
    private Model model;

    private Boolean predictProbability = false;

    private PrintInterface msg = new Messages();

    @Override
    public void loadModel(String filePath) throws IOException {
        model = svm.svm_load_model(filePath);
    }

    @Override
    public void predict() throws IOException {
        BufferedReader inputReader = getTestDataReader();

        LibSvmTester.predict(
                inputReader, getOutputStream(), this.model, booleanToInt(predictProbability));
    }

    private BufferedReader getTestDataReader() {
        if (testDataFilePath != null && !testDataFilePath.isEmpty()) {
            try {
                Reader reader = new InputStreamReader(new FileInputStream(testDataFilePath), "Unicode");
                return new BufferedReader(reader);
            } catch (FileNotFoundException e) {
                msg.print("couldnt find specified file.." + testDataFilePath);
            } catch (UnsupportedEncodingException e) {
                msg.print("Unsupported encoding.. " + e.getMessage());
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

    @Override
    public String getOutputFilePath() {
        return outputFilePath;
    }

    @Override
    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public String getModelFilePath() {
        return modelFilePath;
    }

    @Override
    public void setModelFilePath(String modelFilePath) {
        this.modelFilePath = modelFilePath;
    }

    @Override
    public void setTestDataFilePath(String filePath) {
        this.testDataFilePath = filePath;
    }

    @Override
    public String getTestDataFilePath() {
        return testDataFilePath;
    }

    @Override
    public Boolean getPredictProbability() {
        return predictProbability;
    }

    @Override
    public void setPredictProbability(Boolean predictProbability) {
        this.predictProbability = predictProbability;
    }
}
