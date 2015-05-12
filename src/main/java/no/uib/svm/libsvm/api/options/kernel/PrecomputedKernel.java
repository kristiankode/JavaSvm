package no.uib.svm.libsvm.api.options.kernel;

import no.uib.svm.libsvm.core.libsvm.SvmParameter;

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
    public void fillSvmParameter(SvmParameter param) {

    }

    public PrecomputedKernel(){
    }
}
