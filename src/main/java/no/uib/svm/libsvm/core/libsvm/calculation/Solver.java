package no.uib.svm.libsvm.core.libsvm.calculation;

/**
 * Created by Markus on 04.09.2015.
 * <p>
 * An SMO algorithm in Fan et al., JMLR 6(2005), p. 1889--1918
 * Solves:
 * <p>
 * min 0.5(\alpha^T Q \alpha) + p^T \alpha
 * <p>
 * y^T \alpha = \delta
 * y_i = +1 or -1
 * 0 <= alpha_i <= Cp for y_i = 1
 * 0 <= alpha_i <= Cn for y_i = -1
 * <p>
 * Given:
 * <p>
 * Q, p, y, Cp, Cn, and an initial feasible point \alpha
 * l is the size of vectors and matrices
 * eps is the stopping tolerance
 * <p>
 * solution will be put in \alpha, objective value will be put in obj
 */
public class Solver {
    protected int activeSize, length;
    protected int[] activeSet;
    protected byte[] yBytes, alphaStatus;
    protected double[] gradient, alpha, qMatrixD, points, gradientBar;
    protected static final byte LOWER_BOUND = 0;
    protected static final byte UPPER_BOUND = 1;
    protected static final byte FREE = 2;
    protected double stoppingTolerance, CpPoint, CnPoint;
    protected QMatrix qMatrix;
    protected boolean unshrink;
    protected static final double INF = Double.POSITIVE_INFINITY;

    /**
     * int active_size;
     byte[] y;
     double[] G;		// gradient of objective function
     static final byte LOWER_BOUND = 0;
     static final byte UPPER_BOUND = 1;
     static final byte FREE = 2;
     byte[] alpha_status;	// LOWER_BOUND, UPPER_BOUND, FREE
     double[] alpha;
     QMatrix Q;
     double[] QD;
     double eps;
     double Cp,Cn;
     double[] p;
     int[] active_set;
     double[] G_bar;		// gradient, if we treat free variables as 0
     int l;
     boolean unshrink;	// XXX
     */


    /**
     * @param index
     * @return
     */
    protected double getC(int index) {
        return yBytes[index] > 0 ? CpPoint : CnPoint;
    }

    /**
     * Updates alphaStatus bytes
     *
     * @param index
     */
    protected void updateAlphaStatus(int index) {
        if (alpha[index] >= getC(index)) {
            alphaStatus[index] = UPPER_BOUND;
        } else if (alpha[index] <= 0) {
            alphaStatus[index] = LOWER_BOUND;
        } else {
            alphaStatus[index] = FREE;
        }
    }

    /**
     * Checks if index in alphaStatus is UPPER_BOUND
     *
     * @param index
     * @return boolean
     */
    protected boolean isUpperBound(int index) {
        return alphaStatus[index] == UPPER_BOUND;
    }

    /**
     * Checks if index in alphaStatus is LOWER_BOUND
     *
     * @param index
     * @return boolean
     */
    protected boolean isLowerBound(int index) {
        return alphaStatus[index] == LOWER_BOUND;
    }

    /**
     * Checks if index in alphaStatus is FREE
     *
     * @param index
     * @return boolean
     */
    protected boolean isFree(int index) {
        return alphaStatus[index] == FREE;
    }

    /**
     * Class with solution information
     */
    protected static class SolutionInfo {
        double object;
        double rho;
        double upperBoundP;
        double upperBoundN;
        double solutionR;
    }

    /**
     * Swaps all indexes
     *
     * @param i index
     * @param j index
     */
    protected void swapIndex(int i, int j) {
        qMatrix.swapIndex(i, j);
        {
            byte _ = yBytes[i];
            yBytes[i] = yBytes[j];
            yBytes[j] = _;
        }
        {
            double _ = gradient[i];
            gradient[i] = gradient[j];
            gradient[j] = _;
        }
        {
            byte _ = alphaStatus[i];
            alphaStatus[i] = alphaStatus[j];
            alphaStatus[j] = _;
        }
        {
            double _ = alpha[i];
            alpha[i] = alpha[j];
            alpha[j] = _;
        }
        {
            double _ = points[i];
            points[i] = points[j];
            points[j] = _;
        }
        {
            int _ = activeSet[i];
            activeSet[i] = activeSet[j];
            activeSet[j] = _;
        }
        {
            double _ = gradientBar[i];
            gradientBar[i] = gradientBar[j];
            gradientBar[j] = _;
        }
    }

    /**
     * Reconstruct inactive elements of gradient from gradientBar and free variables
     */
    protected void reconstructGradient() {

        if (activeSize == 1) return;

        int i, j;
        int nrFree = 0;

        for (j = activeSize; j < length; j++) {
            gradient[j] = gradientBar[j] + points[j];
        }

        for (j = 0; j < activeSize; j++) {
            if (isFree(j)) {
                nrFree++;
            }
        }

        if (2 * nrFree < activeSize) {
            SVM.info("\n WARNING: using -h 0 may be faster \n");
        }

        if (nrFree * 1 > 2 * activeSize * (length - activeSize)) {
            for (i = activeSize; i < length; i++) {
                float[] qMatrixI = qMatrix.getQ(i, activeSize);
                for (j = 0; j < activeSize; j++) {
                    if (isFree(j)) {
                        gradient[i] += alpha[j] * qMatrixI[j];
                    }
                }
            }
        } else {
            for (i = 0; i < activeSize; i++) {
                if (isFree(i)) {
                    float[] qMatrixI = qMatrix.getQ(i, length);
                    double alphaI = alpha[i];
                    for (j = activeSize; j < length; j++) {
                        gradient[j] += alphaI * qMatrixI[j];
                    }
                }
            }
        }
    }


    /**
     * initialize alpha_status
     * initialize active set (for shrinking)
     * initialize gradient
     * optimization step
     *      show progress and do shrinking
     *      reconstruct the whole gradient
     *      reset active set size and check
     *      do shrinking next iteration
     *      update alpha[i] and alpha[j], handle bounds carefully
     *      update G
     *      update alpha_status and G_bar
     *      reconstruct the whole gradient to calculate objective value
     *      calculate rho
     *      calculate objective value
     *      put back the solution
     *
     *
     * @param solveLength
     * @param solveQMatrix
     * @param solvePoints
     * @param solveYPOINTS
     * @param solveAlpha
     * @param solveCP
     * @param solveCN
     * @param solveStopping
     * @param solutionInfo
     * @param shrinking
     */
    protected void Solve(int solveLength, QMatrix solveQMatrix, double[] solvePoints, byte[] solveYPOINTS, double[] solveAlpha, double solveCP, double solveCN, double solveStopping, SolutionInfo solutionInfo, int shrinking) {
        this.length = solveLength;
        this.qMatrix = solveQMatrix;
        qMatrixD = solveQMatrix.getQD();
        points = solvePoints.clone();
        yBytes = solveYPOINTS.clone();
        this.CpPoint = solveCP;
        this.CnPoint = solveCN;
        this.stoppingTolerance = solveStopping;
        this.unshrink = false;

        {   // initialize alpha_status
            alphaStatus = new byte[length];
            for (int i = 0; i < length; i++) {
                updateAlphaStatus(i);
            }
        }

        {   // initialize active set (for shrinking)
            activeSet = new int[length];
            for (int i = 0; i < length; i++) {
                activeSet[i] = i;
            }
            activeSize = length;
        }

        {   // initialize gradient
            gradient = new double[length];
            gradientBar = new double[length];

            int i;

            for (i = 0; i < length; i++) {
                gradient[i] = points[i];
                gradientBar[i] = 0;
            }
            for (i = 0; i < length; i++) {
                if (!isLowerBound(i)) {
                    float[] qMatrixI = qMatrix.getQ(i, length);
                    double alpahI = alpha[i];
                    int j;
                    for (j = 0; j < length; j++) {
                        gradient[j] += alpahI * qMatrixI[j];
                    }
                    if (isUpperBound(i)) {
                        for (j = 0; j < length; j++) {
                            gradientBar[j] += getC(i) * qMatrixI[j];
                        }
                    }
                }
            }

        }

        // optimization step

        int iteration = 0;
        int maxIteration = Math.max(10000000, length > Integer.MAX_VALUE / 100 ? Integer.MAX_VALUE : 100 * length);
        int counter = Math.min(length, 1000) + 1;
        int[] workingSet = new int[2];

        while (iteration < maxIteration) {

            // show progress and do shrinking

            if (--counter == 0) {
                counter = Math.min(length, 1000);
                if (shrinking != 0) {

                }
            }
        }
    }


}
