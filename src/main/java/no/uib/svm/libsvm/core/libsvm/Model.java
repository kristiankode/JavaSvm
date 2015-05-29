package no.uib.svm.libsvm.core.libsvm;

/**
 * @author kristian
 *         Created 30.05.15.
 */
public interface Model {


    SvmParameter getParam();

    int getNr_class();

    int getL();

    Node[][] getSV();

    double[][] getSv_coef();

    double[] getRho();

    double[] getProbA();

    double[] getProbB();

    int[] getLabel();

    int[] getnSV();
}
