package no.uib.svm.gui.input;

import no.uib.svm.libsvm.api.options.kernel.PolynomialKernel;
import no.uib.svm.libsvm.api.options.kernel.RadialBasisKernel;
import no.uib.svm.libsvm.api.options.kernel.SigmoidKernel;

/**
 * Class responsible for updating kernel object with values from text input
 */
public class KernelUpdater {
    private final no.uib.svm.gui.SUI SUI;

    public KernelUpdater(no.uib.svm.gui.SUI SUI) {
        this.SUI = SUI;
    }

    /**
     * Takes data from the text input and stores it in a kernel object
     */
    public void updateKernelParams() {
        if (SUI.getSelectedKernel() instanceof PolynomialKernel) {
            updatePolynomialKernel((PolynomialKernel) SUI.getSelectedKernel());
        } else if (SUI.getSelectedKernel() instanceof RadialBasisKernel) {
            updateRadialBasisKernel((RadialBasisKernel) SUI.getSelectedKernel());
        } else if (SUI.getSelectedKernel() instanceof SigmoidKernel) {
            updateSigmoidKernel((SigmoidKernel) SUI.getSelectedKernel());
        }
    }

    /**
     * Takes data from the text inputs and stores it in a Sigmoid kernel Object
     *
     * @param kernel
     */
    private void updateSigmoidKernel(SigmoidKernel kernel) {
        kernel.setGamma(Double.parseDouble(SUI.getGammaInput().getText()));
        kernel.setCoef0(Double.parseDouble(SUI.getCoef0Input().getText()));
    }

    /**
     * Takes data from the text input and stores it in a Radial Basis Kernel object
     *
     * @param kernel
     */
    private void updateRadialBasisKernel(RadialBasisKernel kernel) {
        kernel.setGamma(Double.parseDouble(SUI.getGammaInput().getText()));
    }

    /**
     * Takes data from the text inputs and stores it in a polynomial kernel object
     *
     * @param kernel
     */
    private void updatePolynomialKernel(PolynomialKernel kernel) {
        kernel.setCoef0(Double.parseDouble(SUI.getCoef0Input().getText()));
        kernel.setGamma(Double.parseDouble(SUI.getGammaInput().getText()));
        kernel.setDegree(kernel.getDegree());
    }
}