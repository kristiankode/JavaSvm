package no.uib.svm.libsvm.api.options.svmtype;

import no.uib.svm.libsvm.core.libsvm.svm_parameter;

/**
 * Created by kristianhestetun on 05.05.15.
 */
public class NU_SVR extends SvmType {

    @Override
    public int getId() {
        return SvmType.NU_SVR;
    }

    @Override
    public void fillSvmParameter(svm_parameter param) {
        param.C = this.C;
        param.nu = this.nu;
    }

    private double C;    // for C_SVC, EPSILON_SVR and NU_SVR
    public double nu;    // for NU_SVC, ONE_CLASS, and NU_SVR

    public final static NU_SVR defaultNuSvr = new NU_SVR(
            DEFAULT_C, DEFAULT_NU);

    public NU_SVR(double c, double nu) {
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
