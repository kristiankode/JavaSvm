package no.uib.svm.gui;

import javafx.scene.control.TextField;
import no.uib.svm.libsvm.api.options.kernel.RadialBasisKernel;
import no.uib.svm.libsvm.api.options.kernel.SigmoidKernel;
import no.uib.svm.libsvm.api.options.svmtype.*;

/**
 * Class responsible for updating input-fields when kernel or svm type is selected
 */
public class InputUpdater {
    private final no.uib.svm.gui.SUI SUI;

    public InputUpdater(no.uib.svm.gui.SUI SUI) {
        this.SUI = SUI;
    }

    void handleRadialBasisKernelSelected(RadialBasisKernel kernel) {
        updateInput(SUI.getGammaInput(), kernel.getGamma());
    }

    void handleSigmoidKernelSelected(SigmoidKernel kernel) {
        updateInput(SUI.getGammaInput(), kernel.getGamma());
        updateInput(SUI.getCoef0Input(), kernel.getCoef0());
    }

    void handleCsvcSelected(CSvc svm) {
        updateInput(SUI.getParamC(), svm.getC());
        updateInput(SUI.getParamNrWeight(), svm.getNr_weight());
        updateInput(SUI.getParamWeight(), svm.getWeight());
        updateInput(SUI.getParamWeightLabel(), svm.getWeight_label());
    }

    void handleNuSvcSelected(NuSvc svm) {
        updateInput(SUI.getParamNu(), svm.getNu());
    }

    void handleOneClassSvmSelected(OneClassSvm svm) {
        updateInput(SUI.getParamNu(), svm.getNu());
    }

    void handleNuSvrSelected(NuSvr svm) {
        updateInput(SUI.getParamNu(), svm.getNu());
        updateInput(SUI.getParamC(), svm.getC());
    }

    void handleEpsilonSvrSelected(EpsilonSvr svm) {
        updateInput(SUI.getParamP(), svm.getP());
        updateInput(SUI.getParamC(), svm.getC());
    }

    void resetSvmParams() {
        clearInput(SUI.getParamC());
        clearInput(SUI.getParamNrWeight());
        clearInput(SUI.getParamWeight());
        clearInput(SUI.getParamWeightLabel());
        clearInput(SUI.getParamNu());
        clearInput(SUI.getParamP());
    }

    void resetKernelParams() {
        clearInput(SUI.getGammaInput());
        clearInput(SUI.getCoef0Input());
        clearInput(SUI.getDegreeInput());
    }

    void clearInput(TextField input) {
        input.setText("");
        input.setDisable(true);
    }

    void updateInput(TextField input, Object value) {
        input.setDisable(false);
        input.setText(String.valueOf(value));
    }
}