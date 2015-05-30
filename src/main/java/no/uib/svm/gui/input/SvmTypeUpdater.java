package no.uib.svm.gui.input;

import no.uib.svm.gui.utils.StringToArray;
import no.uib.svm.libsvm.api.options.svmtype.*;

/**
 * Class responsible for updating SvmType object with values from text input
 */
public class SvmTypeUpdater {
    private final no.uib.svm.gui.SUI SUI;

    public SvmTypeUpdater(no.uib.svm.gui.SUI SUI) {
        this.SUI = SUI;
    }

    public void updateSVMTypeParams() {
        if (SUI.getSelectedSvmType() instanceof CSvc) {
            updateCSVCType((CSvc) SUI.getSelectedSvmType());
        } else if (SUI.getSelectedSvmType() instanceof EpsilonSvr) {
            updateEpsilonSVRType((EpsilonSvr) SUI.getSelectedSvmType());
        } else if (SUI.getSelectedSvmType() instanceof NuSvc) {
            updateNUSVCType((NuSvc) SUI.getSelectedSvmType());
        } else if (SUI.getSelectedSvmType() instanceof NuSvr) {
            updateNUSVRType((NuSvr) SUI.getSelectedSvmType());
        } else if (SUI.getSelectedSvmType() instanceof OneClassSvm) {
            updateOneClassSVM((OneClassSvm) SUI.getSelectedSvmType());
        }
    }

    private void updateOneClassSVM(OneClassSvm svm) {
        svm.setNu(Double.parseDouble(SUI.getParamNu().getText()));
    }

    private void updateNUSVRType(NuSvr svm) {
        svm.setNu(Double.parseDouble(SUI.getParamNu().getText()));
        svm.setC(Double.parseDouble(SUI.getParamC().getText()));
    }

    private void updateNUSVCType(NuSvc svm) {
        svm.setNu(Double.parseDouble(SUI.getParamNu().getText()));
    }

    private void updateEpsilonSVRType(EpsilonSvr svm) {
        svm.setC(Double.parseDouble(SUI.getParamC().getText()));
        svm.setP(Double.parseDouble(SUI.getParamP().getText()));
    }

    private void updateCSVCType(CSvc svm) {
        svm.setC(Double.parseDouble(SUI.getParamC().getText()));
        svm.setNr_weight(Integer.parseInt(SUI.getParamNrWeight().getText()));
        svm.setWeight(StringToArray.convertToDoubleArray(SUI.getParamWeight().getText()));
        svm.setWeight_label(StringToArray.convertToIntArray(SUI.getParamWeightLabel().getText()));
    }
}