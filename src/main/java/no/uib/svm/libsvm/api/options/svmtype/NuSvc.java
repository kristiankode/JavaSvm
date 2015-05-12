package no.uib.svm.libsvm.api.options.svmtype;

import no.uib.svm.libsvm.core.libsvm.SvmParameter;

/**
 * Created by kristianhestetun on 05.05.15.
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

    private double nu;    // for NU_SVC, ONE_CLASS, and NU_SVR

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
