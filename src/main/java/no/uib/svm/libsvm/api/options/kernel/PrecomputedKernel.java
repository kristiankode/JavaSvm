package no.uib.svm.libsvm.api.options.kernel;

import no.uib.svm.libsvm.core.libsvm.svm_parameter;

/**
 * Created by kristianhestetun on 05.05.15.
 */
public class PrecomputedKernel extends Kernel {

    @Override
    public int getId() {
        return PRECOMPUTED_KERNEL;
    }

    @Override
    public String getName() {
        return "Precomputed";
    }

    @Override
    public void fillSvmParameter(svm_parameter param) {

    }

    public PrecomputedKernel(){
    }
}
