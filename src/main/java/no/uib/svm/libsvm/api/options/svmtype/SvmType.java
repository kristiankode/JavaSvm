package no.uib.svm.libsvm.api.options.svmtype;

import no.uib.svm.libsvm.core.libsvm.SvmParameter;

/**
 * Created by kristianhestetun on 05.05.15.
 */
public abstract class SvmType {
    /* svm_type */
    public static final int C_SVC = 0;
    public static final int NU_SVC = 1;
    public static final int ONE_CLASS = 2;
    public static final int EPSILON_SVR = 3;
    public static final int NU_SVR = 4;

    // default values
    protected final static double DEFAULT_C = 1;
    protected final static double DEFAULT_NU = 0.5;
    protected final static int DEFAULT_NR_WEIGHT = 0;
    protected final static int[] DEFAULT_WEIGHT_LABEL = new int[0];
    protected final static double[] DEFAULT_WEIGHT = new double[0];

    public abstract int getId();
    public abstract String getName();

    @Override
    public String toString(){
        return this.getName();
    }

    public abstract void fillSvmParameter(SvmParameter param);
}
