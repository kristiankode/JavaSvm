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
            Svm.info("\n WARNING: using -h 0 may be faster \n");
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
                    doShrinking();
                }
                Svm.info(".");
            }

            if(selectWorkingSet(workingSet) != 0){
                reconstructGradient();
                activeSize = 1;

                Svm.info("*");

                if(selectWorkingSet(workingSet) != 0){
                    break;
                }else {
                    counter = 1; // do shrinking next iteration
                }
            }

            int i = workingSet[0];
            int j = workingSet[1];

            ++iteration;

            float[] Qi = qMatrix.getQ(i, activeSize);
            float[] Qj = qMatrix.getQ(j, activeSize);

            double Ci = getC(i);
            double Cj = getC(j);

            double oldAlphaI = alpha[i];
            double oldAlhpaJ = alpha[j];

            if(yBytes[i] != yBytes[j]){
                double quadCoef = qMatrixD[i] + qMatrixD[j] + 2*Qi[j];
                if(quadCoef <= 0){
                    quadCoef = 1e-12;
                }
                double delta = (-gradient[i]-gradient[j])/quadCoef;
                double diff = alpha[i] - alpha[j];
                alpha[i] += delta;
                alpha[j] += delta;

                if(diff > 0)
                {
                    if(alpha[j] < 0)
                    {
                        alpha[j] = 0;
                        alpha[i] = diff;
                    }
                }
                else
                {
                    if(alpha[i] < 0)
                    {
                        alpha[i] = 0;
                        alpha[j] = -diff;
                    }
                }
                if(diff > Ci - Cj)
                {
                    if(alpha[i] > Ci)
                    {
                        alpha[i] = Ci;
                        alpha[j] = Ci - diff;
                    }
                }
                else
                {
                    if(alpha[j] > Cj)
                    {
                        alpha[j] = Cj;
                        alpha[i] = Cj + diff;
                    }
                }
            }
            else
            {
                double quad_coef = qMatrixD[i]+qMatrixD[j]-2*Qi[j];
                if (quad_coef <= 0)
                    quad_coef = 1e-12;
                double delta = (gradient[i]-gradient[j])/quad_coef;
                double sum = alpha[i] + alpha[j];
                alpha[i] -= delta;
                alpha[j] += delta;

                if(sum > Ci)
                {
                    if(alpha[i] > Ci)
                    {
                        alpha[i] = Ci;
                        alpha[j] = sum - Ci;
                    }
                }
                else
                {
                    if(alpha[j] < 0)
                    {
                        alpha[j] = 0;
                        alpha[i] = sum;
                    }
                }
                if(sum > Cj)
                {
                    if(alpha[j] > Cj)
                    {
                        alpha[j] = Cj;
                        alpha[i] = sum - Cj;
                    }
                }
                else
                {
                    if(alpha[i] < 0)
                    {
                        alpha[i] = 0;
                        alpha[j] = sum;
                    }
                }
            }

            // update G

            double deltaAlphaI = alpha[i] - oldAlphaI;
            double deltaAlphaJ = alpha[j] - oldAlhpaJ;

            for(int k=0;k<activeSize;k++)
            {
                gradient[k] += Qi[k]*deltaAlphaI + Qj[k]*deltaAlphaJ;
            }

            // update alpha_status and G_bar

            {
                boolean ui = isUpperBound(i);
                boolean uj = isUpperBound(j);
                updateAlphaStatus(i);
                updateAlphaStatus(j);
                int k;
                if(ui != isUpperBound(i))
                {
                    Qi = qMatrix.getQ(i,length);
                    if(ui)
                        for(k=0;k<length;k++)
                            gradientBar[k] -= Ci * Qi[k];
                    else
                        for(k=0;k<length;k++)
                            gradientBar[k] += Ci * Qi[k];
                }

                if(uj != isUpperBound(j))
                {
                    Qj = qMatrix.getQ(j,length);
                    if(uj)
                        for(k=0;k<length;k++)
                            gradientBar[k] -= Cj * Qj[k];
                    else
                        for(k=0;k<length;k++)
                            gradientBar[k] += Cj * Qj[k];
                }
            }

        }

        if(iteration >= maxIteration)
        {
            if(activeSize < length)
            {
                // reconstruct the whole gradient to calculate objective value
                reconstructGradient();
                activeSize = length;
                Svm.info("*");
            }
            System.err.print("\nWARNING: reaching max number of iterations\n");
        }

        // calculate rho

        solutionInfo.rho = calculateRho();

        // calculate objective value
        {
            double v = 0;
            int i;
            for(i=0;i<length;i++)
                v += alpha[i] * (gradient[i] + points[i]);

            solutionInfo.object = v/2;
        }

        // put back the solution
        {
            for(int i=0;i<length;i++)
                solveAlpha[activeSet[i]] = alpha[i];
        }

        solutionInfo.upperBoundP = CpPoint;
        solutionInfo.upperBoundN = CnPoint;

        Svm.info("\noptimization finished, #iter = "+iteration+"\n");
    }

    /**
     * return 1 if already optimal, return 0 otherwise
     * return i,j such that
     * i: maximizes -y_i * grad(f)_i, i in I_up(\alpha)
     * j: mimimizes the decrease of obj value
     *  (if quadratic coefficeint <= 0, replace it with tau)
     *  -y_j*grad(f)_j < -y_i*grad(f)_i, j in I_low(\alpha)
     *
     * @param working_set
     * @return
     */
    protected int selectWorkingSet(int[] working_set){
        double Gmax = -INF;
        double Gmax2 = -INF;
        int Gmax_idx = -1;
        int Gmin_idx = -1;
        double obj_diff_min = INF;

        for(int t=0;t<activeSize;t++)
            if(yBytes[t]==+1)
            {
                if(!isUpperBound(t))
                    if(-gradient[t] >= Gmax)
                    {
                        Gmax = -gradient[t];
                        Gmax_idx = t;
                    }
            }
            else
            {
                if(!isLowerBound(t))
                    if(gradient[t] >= Gmax)
                    {
                        Gmax = gradient[t];
                        Gmax_idx = t;
                    }
            }

        int i = Gmax_idx;
        float[] Q_i = null;
        if(i != -1) // null Q_i not accessed: Gmax=-INF if i=-1
            Q_i = qMatrix.getQ(i,activeSize);

        for(int j=0;j<activeSize;j++)
        {
            if(yBytes[j]==+1)
            {
                if (!isLowerBound(j))
                {
                    double grad_diff=Gmax+gradient[j];
                    if (gradient[j] >= Gmax2)
                        Gmax2 = gradient[j];
                    if (grad_diff > 0)
                    {
                        double obj_diff;
                        double quad_coef = qMatrixD[i]+qMatrixD[j]-2.0*yBytes[i]*Q_i[j];
                        if (quad_coef > 0)
                            obj_diff = -(grad_diff*grad_diff)/quad_coef;
                        else
                            obj_diff = -(grad_diff*grad_diff)/1e-12;

                        if (obj_diff <= obj_diff_min)
                        {
                            Gmin_idx=j;
                            obj_diff_min = obj_diff;
                        }
                    }
                }
            }
            else
            {
                if (!isUpperBound(j))
                {
                    double grad_diff= Gmax-gradient[j];
                    if (-gradient[j] >= Gmax2)
                        Gmax2 = -gradient[j];
                    if (grad_diff > 0)
                    {
                        double obj_diff;
                        double quad_coef = qMatrixD[i]+qMatrixD[j]+2.0*yBytes[i]*Q_i[j];
                        if (quad_coef > 0)
                            obj_diff = -(grad_diff*grad_diff)/quad_coef;
                        else
                            obj_diff = -(grad_diff*grad_diff)/1e-12;

                        if (obj_diff <= obj_diff_min)
                        {
                            Gmin_idx=j;
                            obj_diff_min = obj_diff;
                        }
                    }
                }
            }
        }

        if(Gmax+Gmax2 < stoppingTolerance)
            return 1;

        working_set[0] = Gmax_idx;
        working_set[1] = Gmin_idx;
        return 0;
    }

    private boolean beShrunk(int i, double Gmax1, double Gmax2)
    {
        if(isUpperBound(i))
        {
            if(yBytes[i]==+1)
                return(-gradient[i] > Gmax1);
            else
                return(-gradient[i] > Gmax2);
        }
        else if(isLowerBound(i))
        {
            if(yBytes[i]==+1)
                return(gradient[i] > Gmax2);
            else
                return(gradient[i] > Gmax1);
        }
        else
            return(false);
    }

    protected void doShrinking()
    {
        int i;
        double Gmax1 = -INF;		// max { -y_i * grad(f)_i | i in I_up(\alpha) }
        double Gmax2 = -INF;		// max { y_i * grad(f)_i | i in I_low(\alpha) }

        // find maximal violating pair first
        for(i=0;i<activeSize;i++)
        {
            if(yBytes[i]==+1)
            {
                if(!isUpperBound(i))
                {
                    if(-gradient[i] >= Gmax1)
                        Gmax1 = -gradient[i];
                }
                if(!isLowerBound(i))
                {
                    if(gradient[i] >= Gmax2)
                        Gmax2 = gradient[i];
                }
            }
            else
            {
                if(!isUpperBound(i))
                {
                    if(-gradient[i] >= Gmax2)
                        Gmax2 = -gradient[i];
                }
                if(!isLowerBound(i))
                {
                    if(gradient[i] >= Gmax1)
                        Gmax1 = gradient[i];
                }
            }
        }

        if(unshrink == false && Gmax1 + Gmax2 <= stoppingTolerance*10)
        {
            unshrink = true;
            reconstructGradient();
            activeSize = length;
        }

        for(i=0;i<activeSize;i++)
            if (beShrunk(i, Gmax1, Gmax2))
            {
                activeSize--;
                while (activeSize > i)
                {
                    if (!beShrunk(activeSize, Gmax1, Gmax2))
                    {
                        swapIndex(i, activeSize);
                        break;
                    }
                    activeSize--;
                }
            }
    }

    double calculateRho()
    {
        double r;
        int nr_free = 0;
        double ub = INF, lb = -INF, sum_free = 0;
        for(int i=0;i<activeSize;i++)
        {
            double yG = yBytes[i]*gradient[i];

            if(isLowerBound(i))
            {
                if(yBytes[i] > 0)
                    ub = Math.min(ub,yG);
                else
                    lb = Math.max(lb,yG);
            }
            else if(isUpperBound(i))
            {
                if(yBytes[i] < 0)
                    ub = Math.min(ub,yG);
                else
                    lb = Math.max(lb,yG);
            }
            else
            {
                ++nr_free;
                sum_free += yG;
            }
        }

        if(nr_free>0)
            r = sum_free/nr_free;
        else
            r = (ub+lb)/2;

        return r;
    }



}
