package no.uib.svm.libsvm.api.options.svmtype;

import no.uib.svm.libsvm.core.libsvm.SvmParameter;

/**
 * Created by kristianhestetun on 05.05.15.
 */
public class OneClassSvm extends SvmType {

    @Override
    public int getId() {
        return SvmType.ONE_CLASS;
    }

    @Override
    public void fillSvmParameter(SvmParameter param) {
        param.nu = this.nu;
    }

    @Override
    public String getName() {
        return "One class";
    }

    /**
     * The parameter nu of nu-SVC, one-class SVM, and nu-SVR
     * default: 0.5
     */
    private double nu;

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
