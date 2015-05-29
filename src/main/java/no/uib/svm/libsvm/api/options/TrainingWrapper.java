package no.uib.svm.libsvm.api.options;

import no.uib.svm.libsvm.api.options.kernel.*;
import no.uib.svm.libsvm.api.options.svmtype.*;
import no.uib.svm.libsvm.core.libsvm.svm;
import no.uib.svm.libsvm.core.libsvm.Model;
import no.uib.svm.libsvm.core.libsvm.SvmParameter;
import no.uib.svm.libsvm.core.SvmTrainer;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class that trains an SVM.
 * The result is a model which can be used for prediction.
 */
public class TrainingWrapper {

    static final Logger logger = Logger.getLogger(String.valueOf(TrainingWrapper.class));

    SvmProducer svmFactory = new SvmProducerImpl();
    SvmType selectedSvmType = svmFactory.getDefault();
    KernelFactory kernelFactory = new KernelFactoryImpl();
    Kernel selectedKernel = kernelFactory.getDefault();
    SvmTrainer trainingEngine = new SvmTrainer();
    Configurator configurator = new Configurator();

    // filenames
    private String inputFile = "";
    private String trainingOutputFile = "result.model";

    // Outputs
    private String trainingResultInfo;

    private List<Kernel> availableKernels = kernelFactory.getAvailableKernels();
    private List<SvmType> availableSvmTypes = svmFactory.getAvailableTypes();

    public void updateTrainingParams() {
        trainingEngine.setParam(configurator.getConfiguration(selectedSvmType, selectedKernel));
    }

    /**
     * Loads training data from file. Data must be in SvmLight-format.
     * @throws IOException
     */
    public void loadTrainingDataFromFile() throws IOException {
        updateTrainingParams();
        trainingEngine.setInput_file_name(inputFile);
        trainingEngine.read_problem();
        logger.info("file loaded successfully");
    }

    /**
     * Trains the model using the selected parameters.
     * @return
     * @throws IOException
     */
    public Model train() throws IOException {
        loadTrainingDataFromFile();
        if (isParametersValid()) {
            Model model =
                    svm.svm_train(trainingEngine.getProb(), trainingEngine.getParam());

            svm.svm_save_model(inputFile + ".model", model);

            return model;
        }
        return null;
    }

    public boolean isParametersValid() {
        String error = svm.svm_check_parameter(
                trainingEngine.getProb(), trainingEngine.getParam());
        if(error != null){
            Logger.getAnonymousLogger().info("Parameters invalid: " + error);
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

    public String getTrainingOutputFile() {
        return trainingOutputFile;
    }

    public void setTrainingOutputFile(String trainingOutputFile) {
        this.trainingOutputFile = trainingOutputFile;
    }
}
