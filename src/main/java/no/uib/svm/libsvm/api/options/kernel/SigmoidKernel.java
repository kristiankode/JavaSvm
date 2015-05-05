package no.uib.svm.libsvm.api.options.kernel;

import no.uib.svm.libsvm.core.libsvm.svm_parameter;

/**
 * Created by kristianhestetun on 05.05.15.
 */
public class SigmoidKernel extends Kernel {

    @Override
    public int getId() {
        return Kernel.SIGMOID;
    }

    @Override
    public void fillSvmParameter(svm_parameter param) {
        param.gamma = this.gamma;
        param.coef0 = this.coef0;
    }

    private double gamma;	// for poly/rbf/sigmoid
    private double coef0;	// for poly/sigmoid

    public SigmoidKernel(double gamma, double coef0){
        this.gamma = gamma;
        this.coef0 = coef0;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public double getCoef0() {
        return coef0;
    }

    public void setCoef0(double coef0) {
        this.coef0 = coef0;
    }
}
