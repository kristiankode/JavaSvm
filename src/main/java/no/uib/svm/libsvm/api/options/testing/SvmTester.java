package no.uib.svm.libsvm.api.options.testing;

import no.uib.svm.libsvm.core.libsvm.Model;

import java.io.IOException;

/**
 * @author kristian
 *         Created 30.05.15.
 */
public interface SvmTester {
    void loadModel(String filePath) throws IOException;

    void predict() throws IOException;

    String getOutputFilePath();

    void setOutputFilePath(String outputFilePath);

    Model getModel();

    void setModel(Model model);

    String getModelFilePath();

    void setModelFilePath(String modelFilePath);

    void setTestDataFilePath(String filePath);

    String getTestDataFilePath();

    Boolean getPredictProbability();

    void setPredictProbability(Boolean predictProbability);
}
