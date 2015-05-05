package no.uib.svm.libsvm.api.options.svmtype;

import no.uib.svm.libsvm.core.libsvm.svm_parameter;

/**
 * Created by kristianhestetun on 05.05.15.
 */
public class OneClassSvm extends SvmType {

    @Override
    public int getId() {
        return SvmType.ONE_CLASS;
    }

    @Override
    public void fillSvmParameter(svm_parameter param) {
        param.nu = this.nu;
    }

    private double nu;    // for NU_SVC, ONE_CLASS, and NU_SVR

    public final static OneClassSvm defaultOneClass = new OneClassSvm(
            DEFAULT_NU);

    public OneClassSvm(double nu) {
        this.nu = nu;
    }

    public double getNu() {
        return nu;
    }

    public void setNu(double nu) {
        this.nu = nu;
    }
}
