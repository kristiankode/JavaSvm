package no.uib.svm.libsvm.api.options.svmtype;

import no.uib.svm.libsvm.core.libsvm.SvmParameter;

/**
 * Nu Support Vector Regression
 */
public class NuSvr extends SvmType {

    @Override
    public int getId() {
        return SvmType.NU_SVR;
    }

    @Override
    public void fillSvmParameter(SvmParameter param) {
        param.C = this.C;
        param.nu = this.nu;
    }

    @Override
    public String getName() {
        return "Nu SVR";
    }

    /**
     * Cost
     * for C_SVC, EPSILON_SVR and NU_SVR
     * default: 1
     */
    private double C;

    /**
     * The parameter nu of nu-SVC, one-class SVM, and nu-SVR
     * default: 0.5
     */
    public double nu;

    public final static NuSvr defaultNuSvr = new NuSvr(
            DEFAULT_C, DEFAULT_NU);

    public NuSvr(double c, double nu) {
        C = c;
        this.nu = nu;
    }

    public double getC() {
        return C;
    }

    public void setC(double c) {
        C = c;
    }

    public double getNu() {
        return nu;
    }

    public void setNu(double nu) {
        this.nu = nu;
    }
}
