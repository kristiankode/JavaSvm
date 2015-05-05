package no.uib.svm.libsvm.api.options.kernel;

import no.uib.svm.libsvm.core.libsvm.svm_parameter;

/**
 * Created by kristianhestetun on 05.05.15.
 */
public class LinearKernel extends Kernel {

    @Override
    public int getId() {
        return Kernel.LINEAR;
    }

    @Override
    public void fillSvmParameter(svm_parameter param) {
        // nothing to fill
    }
}
