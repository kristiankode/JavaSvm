package no.uib.svm.gui.input;

import no.uib.svm.gui.SUI;
import no.uib.svm.libsvm.api.options.kernel.PolynomialKernel;
import no.uib.svm.libsvm.api.options.kernel.RadialBasisKernel;
import no.uib.svm.libsvm.api.options.kernel.SigmoidKernel;

import static no.uib.svm.gui.input.InputUpdater.clearInput;
import static no.uib.svm.gui.input.InputUpdater.updateInput;

public class KernelInputUpdater {
    private final SUI sui;

    public KernelInputUpdater(SUI sui) {
        this.sui = sui;
    }

    private void handlePolynomialKernelSelected(PolynomialKernel kernel) {
        updateInput(sui.getGammaInput(), kernel.getGamma());
        updateInput(sui.getCoef0Input(), kernel.getCoef0());
        updateInput(sui.getDegreeInput(), kernel.getDegree());
    }

    private void handleRadialBasisKernelSelected(RadialBasisKernel kernel) {
        updateInput(sui.getGammaInput(), kernel.getGamma());
    }

    private void handleSigmoidKernelSelected(SigmoidKernel kernel) {
        updateInput(sui.getGammaInput(), kernel.getGamma());
        updateInput(sui.getCoef0Input(), kernel.getCoef0());
    }

    public void updateKernelInput() {
        resetKernelParams();

        if (sui.getSelectedKernel() instanceof PolynomialKernel) {
            handlePolynomialKernelSelected((PolynomialKernel) sui.getSelectedKernel());
        } else if (sui.getSelectedKernel() instanceof SigmoidKernel) {
            handleSigmoidKernelSelected((SigmoidKernel) sui.getSelectedKernel());
        } else if (sui.getSelectedKernel() instanceof RadialBasisKernel) {
            handleRadialBasisKernelSelected((RadialBasisKernel) sui.getSelectedKernel());
        }
    }

    private void resetKernelParams() {
        clearInput(sui.getGammaInput());
        clearInput(sui.getCoef0Input());
        clearInput(sui.getDegreeInput());
    }
}