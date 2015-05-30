package no.uib.svm.gui.input;

import javafx.scene.control.TextField;
import no.uib.svm.libsvm.api.options.kernel.PolynomialKernel;
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

    protected void clearInput(TextField input) {
        input.setText("");
        input.setDisable(true);
    }

    protected void updateInput(TextField input, Object value) {
        input.setDisable(false);
        input.setText(String.valueOf(value));
    }

    public void updateSvmInput() {
        resetSvmParams();

        if (SUI.getSelectedSvmType() instanceof CSvc) {
            handleCsvcSelected((CSvc) SUI.getSelectedSvmType());
        } else if (SUI.getSelectedSvmType() instanceof NuSvc) {
            handleNuSvcSelected((NuSvc) SUI.getSelectedSvmType());
        } else if (SUI.getSelectedSvmType() instanceof NuSvr) {
            handleNuSvrSelected((NuSvr) SUI.getSelectedSvmType());
        } else if (SUI.getSelectedSvmType() instanceof EpsilonSvr) {
            handleEpsilonSvrSelected((EpsilonSvr) SUI.getSelectedSvmType());
        } else if (SUI.getSelectedSvmType() instanceof OneClassSvm) {
            handleOneClassSvmSelected((OneClassSvm) SUI.getSelectedSvmType());
        }
    }

    private void handlePolynomialKernelSelected(PolynomialKernel kernel) {
        updateInput(SUI.getGammaInput(), kernel.getGamma());
        updateInput(SUI.getCoef0Input(), kernel.getCoef0());
        updateInput(SUI.getDegreeInput(), kernel.getDegree());
    }

    private void handleRadialBasisKernelSelected(RadialBasisKernel kernel) {
        updateInput(SUI.getGammaInput(), kernel.getGamma());
    }

    private void handleSigmoidKernelSelected(SigmoidKernel kernel) {
        updateInput(SUI.getGammaInput(), kernel.getGamma());
        updateInput(SUI.getCoef0Input(), kernel.getCoef0());
    }

    private void handleCsvcSelected(CSvc svm) {
        updateInput(SUI.getParamC(), svm.getC());
        updateInput(SUI.getParamNrWeight(), svm.getNr_weight());
        updateInput(SUI.getParamWeight(), svm.getWeight());
        updateInput(SUI.getParamWeightLabel(), svm.getWeight_label());
    }

    private void handleNuSvcSelected(NuSvc svm) {
        updateInput(SUI.getParamNu(), svm.getNu());
    }

    private void handleOneClassSvmSelected(OneClassSvm svm) {
        updateInput(SUI.getParamNu(), svm.getNu());
    }

    private void handleNuSvrSelected(NuSvr svm) {
        updateInput(SUI.getParamNu(), svm.getNu());
        updateInput(SUI.getParamC(), svm.getC());
    }

    private void handleEpsilonSvrSelected(EpsilonSvr svm) {
        updateInput(SUI.getParamP(), svm.getP());
        updateInput(SUI.getParamC(), svm.getC());
    }

    private void resetSvmParams() {
        clearInput(SUI.getParamC());
        clearInput(SUI.getParamNrWeight());
        clearInput(SUI.getParamWeight());
        clearInput(SUI.getParamWeightLabel());
        clearInput(SUI.getParamNu());
        clearInput(SUI.getParamP());
    }

    public void updateKernelInput() {
        resetKernelParams();

        if (SUI.getSelectedKernel() instanceof PolynomialKernel) {
            handlePolynomialKernelSelected((PolynomialKernel) SUI.getSelectedKernel());
        } else if (SUI.getSelectedKernel() instanceof SigmoidKernel) {
            handleSigmoidKernelSelected((SigmoidKernel) SUI.getSelectedKernel());
        } else if (SUI.getSelectedKernel() instanceof RadialBasisKernel) {
            handleRadialBasisKernelSelected((RadialBasisKernel) SUI.getSelectedKernel());
        }
    }


    private void resetKernelParams() {
        clearInput(SUI.getGammaInput());
        clearInput(SUI.getCoef0Input());
        clearInput(SUI.getDegreeInput());
    }
}