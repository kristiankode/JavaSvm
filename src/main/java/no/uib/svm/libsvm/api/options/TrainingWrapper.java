package no.uib.svm.libsvm.api.options;

import no.uib.svm.libsvm.api.options.kernel.*;
import no.uib.svm.libsvm.api.options.svmtype.*;
import no.uib.svm.libsvm.core.libsvm.svm;
import no.uib.svm.libsvm.core.libsvm.Model;
import no.uib.svm.libsvm.core.libsvm.SvmParameter;
import no.uib.svm.libsvm.core.SvmTrainer;

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
    private SvmType selectedSvmType = CSvc.defaultCsvc;

    private SvmTrainer trainingEngine = new SvmTrainer();

    // these are for training only
    public double cache_size = 100; // in MB
    public double eps = 1e-3;    // stopping criteria
    public int shrinking = 1;    // use the shrinking heuristics
    public int probability = 0; // do probability estimates

    // filenames
    private String inputFile = "";
    private String trainingOutputFile = "result.model";

    // Outputs
    private String trainingResultInfo;

    private List<Kernel> availableKernels = Arrays.asList(
            PolynomialKernel.defaultPolynomialKernel,
            RadialBasisKernel.defaultRadialBasisKernel,
            new PrecomputedKernel(),
            new SigmoidKernel(),
            new LinearKernel());

    private List<SvmType> availableSvmTypes = Arrays.asList(
            CSvc.defaultCsvc,
            EpsilonSvr.defaultEpsilonSvr,
            NuSvc.defaultNuSvc,
            NuSvr.defaultNuSvr,
            OneClassSvm.defaultOneClass);

    private SvmParameter fillSvmParam() {
        SvmParameter param = new SvmParameter();

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
