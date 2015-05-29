package no.uib.svm.libsvm.api.options;

import no.uib.svm.libsvm.api.options.kernel.Kernel;
import no.uib.svm.libsvm.api.options.svmtype.SvmType;
import no.uib.svm.libsvm.core.libsvm.SvmParameter;

/**
 * @author kristian
 *         Created 29.05.15.
 */
public class Configurator {

    public double cacheSize = 100; // in MB
    public double eps = 1e-3;    // stopping criteria
    public boolean useShrinking = true;    // use the useShrinking heuristics
    public boolean doProbabilityEstimates = false; // do doProbabilityEstimates estimates

    /**
     * Creates an SvmParameter with values corresponding to class fields.
     *
     * @return SvmParams with same values as this class.
     */
    public SvmParameter getConfiguration(SvmType type, Kernel kernel) {
        SvmParameter param = new SvmParameter();

        param.cache_size = this.cacheSize;
        param.eps = this.eps;
        param.shrinking = booleanToInt(this.useShrinking);
        param.probability = booleanToInt(doProbabilityEstimates);

        type.fillSvmParameter(param);
        kernel.fillSvmParameter(param);

        return param;
    }

    private int booleanToInt(boolean bool) {
        return bool ? 1 : 0;
    }
}
