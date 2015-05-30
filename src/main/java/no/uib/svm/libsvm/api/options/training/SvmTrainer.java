package no.uib.svm.libsvm.api.options.training;

import no.uib.svm.libsvm.api.options.kernel.Kernel;
import no.uib.svm.libsvm.api.options.svmtype.SvmType;
import no.uib.svm.libsvm.core.libsvm.Model;

import java.io.IOException;
import java.util.List;

/**
 * @author kristian
 *         Created 30.05.15.
 */
public interface SvmTrainer {
    void loadTrainingDataFromFile() throws IOException;

    Model train() throws IOException;

    String getInputFile();

    void setInputFile(String inputFile);

    List<Kernel> getAvailableKernels();

    List<SvmType> getAvailableSvmTypes();

    SvmType getSelectedSvmType();

    void setSelectedSvmType(SvmType selectedSvmType);

    Kernel getSelectedKernel();

    void setSelectedKernel(Kernel selectedKernel);
}
