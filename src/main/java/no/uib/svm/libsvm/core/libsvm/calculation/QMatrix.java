package no.uib.svm.libsvm.core.libsvm.calculation;

/**
 * Created by Markus on 04.09.2015.
 *
 * Kernel evaluation
 *
 * the static method k_function is for doing single kernel evaluation
 * the constructor of Kernel prepares to calculate the l*l kernel matrix
 * the member function get_Q is for getting one column from the Q Matrix
 */
abstract class QMatrix {
    abstract float[] getQ(int column, int length);
    abstract double[] getQD();
    abstract void swapIndex(int i, int j);
}
