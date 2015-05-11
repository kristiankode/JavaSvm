package no.uib.svm.libsvm.api.options;

import no.uib.svm.libsvm.api.options.kernel.*;
import no.uib.svm.libsvm.api.options.svmtype.*;
import no.uib.svm.libsvm.core.libsvm.svm;
import no.uib.svm.libsvm.core.libsvm.svm_model;
import no.uib.svm.libsvm.core.libsvm.svm_parameter;
import no.uib.svm.libsvm.core.svm_train;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by kristianhestetun on 05.05.15.
 * Class that trains an SVM.
 * The result is a model which can be used for prediction.
 */
public class TrainingWrapper {

    static final Logger logger = Logger.getLogger(String.valueOf(TrainingWrapper.class));

    // Usage: svm_train [options] training_set_file [model_file]\n
    private Kernel selectedKernel = new LinearKernel();
    private SvmType selectedSvmType = C_SVC.defaultCsvc;

    private svm_train trainingEngine = new svm_train();

    // these are for training only
    public double cache_size = 100; // in MB
    public double eps = 1e-3;    // stopping criteria
    public int shrinking = 1;    // use the shrinking heuristics
    public int probability = 0; // do probability estimates

    // filenames
    private String inputFile = "";
    private String trainingOutputFile = "result.model";

    private List<Kernel> availableKernels = Arrays.asList(
            PolynomialKernel.defaultPolynomialKernel,
            RadialBasisKernel.defaultRadialBasisKernel,
            new LinearKernel(),
            new PrecomputedKernel(),
            new SigmoidKernel());

    private List<SvmType> availableSvmTypes = Arrays.asList(
            C_SVC.defaultCsvc,
            EPSILON_SVR.defaultEpsilonSvr,
            NU_SVC.defaultNuSvc,
            NU_SVR.defaultNuSvr,
            OneClassSvm.defaultOneClass);

    private svm_parameter fillSvmParam() {
        svm_parameter param = new svm_parameter();

        param.cache_size = this.cache_size;
        param.eps = this.eps;
        param.shrinking = this.shrinking;
        param.probability = this.probability;

        selectedSvmType.fillSvmParameter(param);
        selectedKernel.fillSvmParameter(param);

        return param;
    }

    public void updateTrainingParams() {
        trainingEngine.setParam(fillSvmParam());
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
    public svm_model train() throws IOException {
        loadTrainingDataFromFile();
        if (isParametersValid()) {
            svm_model model =
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
