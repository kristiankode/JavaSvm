package no.uib.svm.libsvm.api.options.kernel;

import no.uib.svm.libsvm.core.libsvm.SvmParameter;

/**
 * Created by kristianhestetun on 05.05.15.
 */
public class LinearKernel extends Kernel {

    @Override
    public int getId() {
        return Kernel.LINEAR;
    }

    @Override
    public void fillSvmParameter(SvmParameter param) {
        // nothing to fill
    }

    @Override
    public String getName(){
        return "Linear";
    }
}
