package no.uib.svm.libsvm.api.options.training;

import no.uib.svm.libsvm.api.options.Configurator;
import no.uib.svm.libsvm.api.options.ProblemLoader;
import no.uib.svm.libsvm.api.options.kernel.Kernel;
import no.uib.svm.libsvm.api.options.kernel.KernelFactory;
import no.uib.svm.libsvm.api.options.kernel.KernelFactoryImpl;
import no.uib.svm.libsvm.api.options.logging.Messages;
import no.uib.svm.libsvm.api.options.svmtype.SvmProducer;
import no.uib.svm.libsvm.api.options.svmtype.SvmProducerImpl;
import no.uib.svm.libsvm.api.options.svmtype.SvmType;
import no.uib.svm.libsvm.core.libsvm.*;

import java.io.IOException;
import java.util.List;

/**
 * Class that trains an SVM.
 * The result is a model which can be used for prediction.
 */
public class TrainingWrapper implements SvmTrainer {

    public static final String MODEL_FILE_SUFFIX = ".model";
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
    private String trainingOutputFile = inputFile + MODEL_FILE_SUFFIX;

    private List<Kernel> availableKernels = kernelFactory.getAvailableKernels();
    private List<SvmType> availableSvmTypes = svmFactory.getAvailableTypes();

    /**
     * Loads training data from file. Data must be in SvmLight-format.
     *
     * @throws IOException
     */
    @Override
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
    @Override
    public Model train() throws IOException {
        loadTrainingDataFromFile();
        if (isParametersValid()) {
            svm.setPrintInterface(msg);

            SvmModel model = svm.svm_train(problem, configuration);
            svm.svm_save_model(trainingOutputFile, model);

            return model;
        }
        return null;
    }

    private boolean isParametersValid() {
        String error = svm.svm_check_parameter(
                problem, configuration);
        if (error != null) {
            msg.print("Parameters invalid: " + error);
        }
        return error == null;
    }

    @Override
    public String getInputFile() {
        return inputFile;
    }

    @Override
    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
        this.trainingOutputFile = inputFile + MODEL_FILE_SUFFIX;
    }

    @Override
    public List<Kernel> getAvailableKernels() {
        return this.availableKernels;
    }

    @Override
    public List<SvmType> getAvailableSvmTypes() {
        return availableSvmTypes;
    }

    @Override
    public SvmType getSelectedSvmType() {
        return selectedSvmType;
    }

    @Override
    public void setSelectedSvmType(SvmType selectedSvmType) {
        this.selectedSvmType = selectedSvmType;
    }

    @Override
    public Kernel getSelectedKernel() {
        return selectedKernel;
    }

    @Override
    public void setSelectedKernel(Kernel selectedKernel) {
        this.selectedKernel = selectedKernel;
    }

}
