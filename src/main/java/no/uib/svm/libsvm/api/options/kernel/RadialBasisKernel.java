package no.uib.svm.libsvm.api.options.kernel;

import no.uib.svm.libsvm.core.libsvm.svm_parameter;

/**
 * Created by kristianhestetun on 05.05.15.
 */
public class RadialBasisKernel extends Kernel {

    @Override
    public int getId() {
        return Kernel.RADIAL_BASIS;
    }

    @Override
    public void fillSvmParameter(svm_parameter param) {
        param.gamma = this.gamma;
    }

    private double gamma;    // for poly/rbf/sigmoid

    public RadialBasisKernel(double gamma) {
        this.gamma = gamma;
    }

    public final static RadialBasisKernel defaultRadialBasisKernel =
            new RadialBasisKernel(DEFAULT_GAMMA);

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }
}
