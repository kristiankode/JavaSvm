package no.uib.svm.libsvm.api.options.svmtype;

import no.uib.svm.libsvm.core.libsvm.SvmParameter;

/**
 * Nu Support Vector Classification
 */
public class NuSvc extends SvmType {

    @Override
    public int getId() {
        return SvmType.NU_SVC;
    }

    @Override
    public void fillSvmParameter(SvmParameter param) {
        param.nu = this.nu;
    }

    @Override
    public String getName() {
        return "Nu SVC";
    }

    /**
     * The parameter nu of nu-SVC, one-class SVM, and nu-SVR
     * default: 0.5
     */
    private double nu;

    public final static NuSvc defaultNuSvc = new NuSvc(
            DEFAULT_NU);

    public NuSvc(double nu) {
        this.nu = nu;
    }

    public double getNu() {
        return nu;
    }

    public void setNu(double nu) {
        this.nu = nu;
    }

}
