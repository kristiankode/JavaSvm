package no.uib.svm.libsvm.api.options.svmtype;

import no.uib.svm.libsvm.core.libsvm.SvmParameter;

/**
 * Created by kristianhestetun on 05.05.15.
 */
public class EpsilonSvr extends SvmType {

    @Override
    public int getId() {
        return EPSILON_SVR;
    }

    @Override
    public void fillSvmParameter(SvmParameter param) {
        param.C = this.C;
        param.p = this.p;
    }

    @Override
    public String getName() {
        return "Epsilon SVR";
    }

    private double C;    // for C_SVC, EPSILON_SVR and NU_SVR
    public double p;    // for EPSILON_SVR

    private final static double DEFAULT_C = 1;
    private final static double DEFAULT_P = 0.1;

    public final static EpsilonSvr defaultEpsilonSvr = new EpsilonSvr(
            DEFAULT_C, DEFAULT_P);

    public EpsilonSvr(double c, double p) {
        C = c;
        this.p = p;
    }

    public double getC() {
        return C;
    }

    public void setC(double c) {
        C = c;
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }
}
