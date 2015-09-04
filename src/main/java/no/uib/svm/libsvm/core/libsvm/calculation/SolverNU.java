package no.uib.svm.libsvm.core.libsvm.calculation;

/**
 * Created by Markus on 04.09.2015.
 *
 * additional constraint: e^T \alpha = constant
 *
 * Solver for nu-svm classification and regression
 */
final class SolverNU extends Solver {

    private SolutionInfo solutionInfo;

    protected void Solve(int l, QMatrix solveQMatrix, double[] solvePoints, byte[] solveYPOINTS,
                         double[] solveAlpha, double solveCP, double solveCN, double solveStopping,
                         SolutionInfo solutionInfo, int shrinking)
    {
        this.solutionInfo = solutionInfo;
        super.Solve(l,solveQMatrix,solvePoints,solveYPOINTS,solveAlpha,solveCP,solveCN,solveStopping,solutionInfo,shrinking);
    }



    /**
     *  return 1 if already optimal, return 0 otherwise
     *  return i,j such that y_i = y_j and
     * i: maximizes -y_i * grad(f)_i, i in I_up(\alpha)
     * j: minimizes the decrease of obj value
     *    (if quadratic coefficeint <= 0, replace it with tau)
     *    -y_j*grad(f)_j < -y_i*grad(f)_i, j in I_low(\alpha)
     *
     * @param workingSet
     * @return int 1 or 0
     */
    protected int selectWorkingSet(int[] workingSet)
    {


        double gmaxp = -INF;
        double gmaxp2 = -INF;
        int gmaxpIdx = -1;

        double gmaxn = -INF;
        double gmaxn2 = -INF;
        int gmaxnIdx = -1;

        int gminIdx = -1;
        double objDiffMin = INF;

        for(int t=0;t<activeSize;t++)
            if(yBytes[t]==+1)
            {
                if(!isUpperBound(t))
                    if(-gradient[t] >= gmaxp)
                    {
                        gmaxp = -gradient[t];
                        gmaxpIdx = t;
                    }
            }
            else
            {
                if(!isLowerBound(t))
                    if(gradient[t] >= gmaxn)
                    {
                        gmaxn = gradient[t];
                        gmaxnIdx = t;
                    }
            }

        int ip = gmaxpIdx;
        int in = gmaxnIdx;
        float[] qIp = null;
        float[] qIn = null;
        if(ip != -1) // null Q_ip not accessed: Gmaxp=-INF if ip=-1
            qIp = qMatrix.getQ(ip, activeSize);
        if(in != -1)
            qIn = qMatrix.getQ(in, activeSize);

        for(int j=0;j<activeSize;j++)
        {
            if(yBytes[j]==+1)
            {
                if (!isLowerBound(j))
                {
                    double gradDiff=gmaxp+gradient[j];
                    if (gradient[j] >= gmaxp2)
                        gmaxp2 = gradient[j];
                    if (gradDiff > 0)
                    {
                        double objDiff;
                        double quadCoef = qMatrixD[ip]+qMatrixD[j]-2*qIp[j];
                        if (quadCoef > 0)
                            objDiff = -(gradDiff*gradDiff)/quadCoef;
                        else
                            objDiff = -(gradDiff*gradDiff)/1e-12;

                        if (objDiff <= objDiffMin)
                        {
                            gminIdx=j;
                            objDiffMin = objDiff;
                        }
                    }
                }
            }
            else
            {
                if (!isUpperBound(j))
                {
                    double gradDiff=gmaxn-gradient[j];
                    if (-gradient[j] >= gmaxn2)
                        gmaxn2 = -gradient[j];
                    if (gradDiff > 0)
                    {
                        double objDiff;
                        double quadCoef = qMatrixD[in]+qMatrixD[j]-2*qIn[j];
                        if (quadCoef > 0)
                            objDiff = -(gradDiff*gradDiff)/quadCoef;
                        else
                            objDiff = -(gradDiff*gradDiff)/1e-12;

                        if (objDiff <= objDiffMin)
                        {
                            gminIdx=j;
                            objDiffMin = objDiff;
                        }
                    }
                }
            }
        }

        if(Math.max(gmaxp+gmaxp2,gmaxn+gmaxn2) < stoppingTolerance)
            return 1;

        if(yBytes[gminIdx] == +1)
            workingSet[0] = gmaxpIdx;
        else
            workingSet[0] = gmaxnIdx;
        workingSet[1] = gminIdx;

        return 0;
    }

    protected boolean beShrunk(int i, double gmax1, double gmax2, double gmax3, double gmax4)
    {
        if(isUpperBound(i))
        {
            if(yBytes[i]==+1)
                return(-gradient[i] > gmax1);
            else
                return(-gradient[i] > gmax4);
        }
        else if(isLowerBound(i))
        {
            if(yBytes[i]==+1)
                return (gradient[i] > gmax2);
            else
                return(gradient[i] > gmax3);
        }
        else
            return(false);
    }

    /**
     * find maximal violating pair first
     */
    protected void doShrinking()
    {
        double gmax1 = -INF;	// max { -y_i * grad(f)_i | y_i = +1, i in I_up(\alpha) }
        double gmax2 = -INF;	// max { y_i * grad(f)_i | y_i = +1, i in I_low(\alpha) }
        double gmax3 = -INF;	// max { -y_i * grad(f)_i | y_i = -1, i in I_up(\alpha) }
        double gmax4 = -INF;	// max { y_i * grad(f)_i | y_i = -1, i in I_low(\alpha) }

        int i;
        for(i=0;i<activeSize;i++)
        {
            if(!isUpperBound(i))
            {
                if(yBytes[i]==+1)
                {
                    if(-gradient[i] > gmax1) gmax1 = -gradient[i];
                }
                else	if(-gradient[i] > gmax4) gmax4 = -gradient[i];
            }
            if(!isLowerBound(i))
            {
                if(yBytes[i]==+1)
                {
                    if(gradient[i] > gmax2) gmax2 = gradient[i];
                }
                else	if(gradient[i] > gmax3) gmax3 = gradient[i];
            }
        }

        if(unshrink == false && Math.max(gmax1+gmax2,gmax3+gmax4) <= stoppingTolerance*10)
        {
            unshrink = true;
            reconstructGradient();
            activeSize = length;
        }

        for(i=0;i<activeSize;i++)
            if (beShrunk(i, gmax1, gmax2, gmax3, gmax4))
            {
                activeSize--;
                while (activeSize > i)
                {
                    if (!beShrunk(activeSize, gmax1, gmax2, gmax3, gmax4))
                    {
                        swapIndex(i, activeSize);
                        break;
                    }
                    activeSize--;
                }
            }
    }

    protected double calculateRho()
    {
        int nrFree1 = 0,nrFree2 = 0;
        double ub1 = INF, ub2 = INF;
        double lb1 = -INF, lb2 = -INF;
        double sumFree1 = 0, sumFree2 = 0;

        for(int i=0;i<activeSize;i++)
        {
            if(yBytes[i]==+1)
            {
                if(isLowerBound(i))
                    ub1 = Math.min(ub1, gradient[i]);
                else if(isUpperBound(i))
                    lb1 = Math.max(lb1,gradient[i]);
                else
                {
                    ++nrFree1;
                    sumFree1 += gradient[i];
                }
            }
            else
            {
                if(isLowerBound(i))
                    ub2 = Math.min(ub2, gradient[i]);
                else if(isUpperBound(i))
                    lb2 = Math.max(lb2,gradient[i]);
                else
                {
                    ++nrFree2;
                    sumFree2 += gradient[i];
                }
            }
        }

        double r1,r2;
        if(nrFree1 > 0)
            r1 = sumFree1/nrFree1;
        else
            r1 = (ub1+lb1)/2;

        if(nrFree2 > 0)
            r2 = sumFree2/nrFree2;
        else
            r2 = (ub2+lb2)/2;

        solutionInfo.rho = (r1+r2)/2;
        return (r1-r2)/2;
    }
}
