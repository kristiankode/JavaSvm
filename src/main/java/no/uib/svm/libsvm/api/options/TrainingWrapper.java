package no.uib.svm.libsvm.api.options;

import no.uib.svm.libsvm.api.options.kernel.Kernel;
import no.uib.svm.libsvm.api.options.kernel.KernelFactory;
import no.uib.svm.libsvm.api.options.kernel.KernelFactoryImpl;
import no.uib.svm.libsvm.api.options.logging.Messages;
import no.uib.svm.libsvm.api.options.svmtype.SvmProducer;
import no.uib.svm.libsvm.api.options.svmtype.SvmProducerImpl;
import no.uib.svm.libsvm.api.options.svmtype.SvmType;
import no.uib.svm.libsvm.core.libsvm.Model;
import no.uib.svm.libsvm.core.libsvm.Problem;
import no.uib.svm.libsvm.core.libsvm.SvmParameter;
import no.uib.svm.libsvm.core.libsvm.svm;

import java.io.IOException;
import java.util.List;

/**
 * Class that trains an SVM.
 * The result is a model which can be used for prediction.
 */
public class TrainingWrapper {

    // GUI messages
    Messages msg = new Messages();

    // dependencies
    SvmProducer svmFactory = new SvmProducerImpl();
    SvmType selectedSvmType = svmFactory.getDefault();
    KernelFactory kernelFactory = new KernelFactoryImpl();
    Kernel selectedKernel = kernelFactory.getDefault();
    Configurator configurator = new Configurator();

    // state
    Problem problem = null;
    SvmParameter configuration;

    // filenames
    private String inputFile = "";
    private String trainingOutputFile = "result.model";

    // Outputs
    private String trainingResultInfo;

    private List<Kernel> availableKernels = kernelFactory.getAvailableKernels();
    private List<SvmType> availableSvmTypes = svmFactory.getAvailableTypes();

    /**
     * Loads training data from file. Data must be in SvmLight-format.
     *
     * @throws IOException
     */
    public void loadTrainingDataFromFile() throws IOException {
        configuration = configurator.getConfiguration(selectedSvmType, selectedKernel);
        problem = ProblemLoader.loadProblemFromFile(inputFile, configuration);
        msg.print("file loaded successfully");
    }

    /**
     * Trains the model using the selected parameters.
     *
     * @return
     * @throws IOException
     */
    public Model train() throws IOException {
        loadTrainingDataFromFile();
        if (isParametersValid()) {
            Model model =
                    svm.svm_train(problem, configuration);

            svm.svm_save_model(inputFile + ".model", model);

            return model;
        }
        return null;
    }

    public boolean isParametersValid() {
        String error = svm.svm_check_parameter(
                problem, configuration);
        if (error != null) {
            msg.print("Parameters invalid: " + error);
        }
        return error == null;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public List<Kernel> getAvailableKernels() {
        return this.availableKernels;
    }

    public List<SvmType> getAvailableSvmTypes() {
        return availableSvmTypes;
    }

    public SvmType getSelectedSvmType() {
        return selectedSvmType;
    }

    public void setSelectedSvmType(SvmType selectedSvmType) {
        this.selectedSvmType = selectedSvmType;
    }

    public Kernel getSelectedKernel() {
        return selectedKernel;
    }

    public void setSelectedKernel(Kernel selectedKernel) {
        this.selectedKernel = selectedKernel;
    }

}
