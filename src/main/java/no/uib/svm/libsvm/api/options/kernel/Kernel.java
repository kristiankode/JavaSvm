package no.uib.svm.libsvm.api.options.kernel;

import no.uib.svm.libsvm.core.libsvm.SvmParameter;

/**
 * Created by kristianhestetun on 05.05.15.
 */
public abstract class Kernel {
    public static int
            LINEAR = 0,
            POLYNOMIAL = 1,
            RADIAL_BASIS = 2,
            SIGMOID = 3,
            PRECOMPUTED_KERNEL = 4;

    // default values
    public final static int DEFAULT_DEGREE = 3;
    public final static double DEFAULT_GAMMA = 0;	// 1/num_features
    public final static double DEFAULT_COEF0 = 0;

    public abstract int getId();

    public abstract String getName();

    @Override
    public String toString(){
        return this.getName();
    }

    public abstract void fillSvmParameter(SvmParameter param);


}
