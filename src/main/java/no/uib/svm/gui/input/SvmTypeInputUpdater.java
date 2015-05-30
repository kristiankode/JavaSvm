package no.uib.svm.gui.input;

import no.uib.svm.gui.SUI;
import no.uib.svm.libsvm.api.options.svmtype.*;

import static no.uib.svm.gui.input.InputUpdater.clearInput;
import static no.uib.svm.gui.input.InputUpdater.updateInput;

/**
 * @author kristian
 *         Created 30.05.15.
 */
public class SvmTypeInputUpdater {
    private final SUI sui;

    public SvmTypeInputUpdater(SUI sui) {
        this.sui = sui;
    }

    public void updateSvmInput() {
        resetSvmParams();

        if (sui.getSelectedSvmType() instanceof CSvc) {
            handleCsvcSelected((CSvc) sui.getSelectedSvmType());
        } else if (sui.getSelectedSvmType() instanceof NuSvc) {
            handleNuSvcSelected((NuSvc) sui.getSelectedSvmType());
        } else if (sui.getSelectedSvmType() instanceof NuSvr) {
            handleNuSvrSelected((NuSvr) sui.getSelectedSvmType());
        } else if (sui.getSelectedSvmType() instanceof EpsilonSvr) {
            handleEpsilonSvrSelected((EpsilonSvr) sui.getSelectedSvmType());
        } else if (sui.getSelectedSvmType() instanceof OneClassSvm) {
            handleOneClassSvmSelected((OneClassSvm) sui.getSelectedSvmType());
        }
    }

    private void handleCsvcSelected(CSvc svm) {
        updateInput(sui.getParamC(), svm.getC());
        updateInput(sui.getParamNrWeight(), svm.getNr_weight());
        updateInput(sui.getParamWeight(), svm.getWeight());
        updateInput(sui.getParamWeightLabel(), svm.getWeight_label());
    }

    private void handleNuSvcSelected(NuSvc svm) {
        updateInput(sui.getParamNu(), svm.getNu());
    }

    private void handleOneClassSvmSelected(OneClassSvm svm) {
        updateInput(sui.getParamNu(), svm.getNu());
    }

    private void handleNuSvrSelected(NuSvr svm) {
        updateInput(sui.getParamNu(), svm.getNu());
        updateInput(sui.getParamC(), svm.getC());
    }

    private void handleEpsilonSvrSelected(EpsilonSvr svm) {
        updateInput(sui.getParamP(), svm.getP());
        updateInput(sui.getParamC(), svm.getC());
    }

    private void resetSvmParams() {
        clearInput(sui.getParamC());
        clearInput(sui.getParamNrWeight());
        clearInput(sui.getParamWeight());
        clearInput(sui.getParamWeightLabel());
        clearInput(sui.getParamNu());
        clearInput(sui.getParamP());
    }
}
