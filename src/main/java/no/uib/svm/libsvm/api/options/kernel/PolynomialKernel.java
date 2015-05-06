package no.uib.svm.libsvm.api.options.kernel;

import no.uib.svm.libsvm.core.libsvm.svm_parameter;

/**
 * Created by kristianhestetun on 05.05.15.
 */
public class PolynomialKernel extends Kernel {

    @Override
    public int getId() {
        return POLYNOMIAL;
    }

    @Override
    public void fillSvmParameter(svm_parameter param) {
        param.degree = this.degree;
        param.gamma = this.gamma;
        param.coef0 = this.coef0;
    }

    private int degree;    // for poly
    private double gamma;    // for poly/rbf/sigmoid
    private double coef0;    // for poly/sigmoid

    public PolynomialKernel(int degree, double gamma, double coef0) {
        this.degree = degree;
        this.gamma = gamma;
        this.coef0 = coef0;
    }

    @Override
    public String getName(){
        return "Polynomial";
    }

    public final static PolynomialKernel defaultPolynomialKernel
            = new PolynomialKernel(DEFAULT_DEGREE, DEFAULT_GAMMA, DEFAULT_COEF0);

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
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
