package no.uib.svm.libsvm.core.libsvm.calculation;

import no.uib.svm.libsvm.api.options.logging.Messages;
import no.uib.svm.libsvm.core.libsvm.*;

import java.io.*;
import java.util.Random;
import java.util.StringTokenizer;

public class Svm {
    //
    // construct and solve various formulations
    //
    public static final int LIBSVM_VERSION=320;
    public static final Random rand = new Random();

    private static PrintInterface svm_print_stdout = new Messages();
    private static PrintInterface svm_print_string = svm_print_stdout;

    public static void setPrintInterface(PrintInterface printInterface){
        svm_print_stdout = printInterface;
        svm_print_string = svm_print_stdout;
    }

    public PrintInterface getPrintInterface(){
        return svm_print_stdout;
    }

    static void info(String s)
    {
        svm_print_string.print(s);
    }

    private static void solve_c_svc(Problem prob, SvmParameter param,
                                    double[] alpha, Solver.SolutionInfo si,
                                    double Cp, double Cn)
    {
        int l = prob.l;
        double[] minus_ones = new double[l];
        byte[] y = new byte[l];

        int i;

        for(i=0;i<l;i++)
        {
            alpha[i] = 0;
            minus_ones[i] = -1;
            if(prob.y[i] > 0) y[i] = +1; else y[i] = -1;
        }

        Solver s = new Solver();
        s.Solve(l, new SvcQ(prob,param,y), minus_ones, y,
                alpha, Cp, Cn, param.eps, si, param.shrinking);

        double sum_alpha=0;
        for(i=0;i<l;i++)
            sum_alpha += alpha[i];

        if (Cp==Cn)
            Svm.info("nu = " + sum_alpha / (Cp * prob.l) + "\n");

        for(i=0;i<l;i++)
            alpha[i] *= y[i];
    }

    private static void solve_nu_svc(Problem prob, SvmParameter param,
                                     double[] alpha, Solver.SolutionInfo si)
    {
        int i;
        int l = prob.l;
        double nu = param.nu;

        byte[] y = new byte[l];

        for(i=0;i<l;i++)
            if(prob.y[i]>0)
                y[i] = +1;
            else
                y[i] = -1;

        double sum_pos = nu*l/2;
        double sum_neg = nu*l/2;

        for(i=0;i<l;i++)
            if(y[i] == +1)
            {
                alpha[i] = Math.min(1.0,sum_pos);
                sum_pos -= alpha[i];
            }
            else
            {
                alpha[i] = Math.min(1.0,sum_neg);
                sum_neg -= alpha[i];
            }

        double[] zeros = new double[l];

        for(i=0;i<l;i++)
            zeros[i] = 0;

        SolverNU s = new SolverNU();
        s.Solve(l, new SvcQ(prob,param,y), zeros, y,
                alpha, 1.0, 1.0, param.eps, si, param.shrinking);
        double r = si.solutionR;

        Svm.info("C = " + 1 / r + "\n");

        for(i=0;i<l;i++)
            alpha[i] *= y[i]/r;

        si.rho /= r;
        si.object /= (r*r);
        si.upperBoundP = 1/r;
        si.upperBoundN = 1/r;
    }

    private static void solve_one_class(Problem prob, SvmParameter param,
                                        double[] alpha, Solver.SolutionInfo si)
    {
        int l = prob.l;
        double[] zeros = new double[l];
        byte[] ones = new byte[l];
        int i;

        int n = (int)(param.nu*prob.l);	// # of alpha's at upper bound

        for(i=0;i<n;i++)
            alpha[i] = 1;
        if(n<prob.l)
            alpha[n] = param.nu * prob.l - n;
        for(i=n+1;i<l;i++)
            alpha[i] = 0;

        for(i=0;i<l;i++)
        {
            zeros[i] = 0;
            ones[i] = 1;
        }

        Solver s = new Solver();
        s.Solve(l, new OneClassQ(prob,param), zeros, ones,
                alpha, 1.0, 1.0, param.eps, si, param.shrinking);
    }

    private static void solve_epsilon_svr(Problem prob, SvmParameter param,
                                          double[] alpha, Solver.SolutionInfo si)
    {
        int l = prob.l;
        double[] alpha2 = new double[2*l];
        double[] linear_term = new double[2*l];
        byte[] y = new byte[2*l];
        int i;

        for(i=0;i<l;i++)
        {
            alpha2[i] = 0;
            linear_term[i] = param.p - prob.y[i];
            y[i] = 1;

            alpha2[i+l] = 0;
            linear_term[i+l] = param.p + prob.y[i];
            y[i+l] = -1;
        }

        Solver s = new Solver();
        s.Solve(2*l, new SvrQ(prob,param), linear_term, y,
                alpha2, param.C, param.C, param.eps, si, param.shrinking);

        double sum_alpha = 0;
        for(i=0;i<l;i++)
        {
            alpha[i] = alpha2[i] - alpha2[i+l];
            sum_alpha += Math.abs(alpha[i]);
        }
        Svm.info("nu = " + sum_alpha / (param.C * l) + "\n");
    }

    private static void solve_nu_svr(Problem prob, SvmParameter param,
                                     double[] alpha, Solver.SolutionInfo si)
    {
        int l = prob.l;
        double C = param.C;
        double[] alpha2 = new double[2*l];
        double[] linear_term = new double[2*l];
        byte[] y = new byte[2*l];
        int i;

        double sum = C * param.nu * l / 2;
        for(i=0;i<l;i++)
        {
            alpha2[i] = alpha2[i+l] = Math.min(sum,C);
            sum -= alpha2[i];

            linear_term[i] = - prob.y[i];
            y[i] = 1;

            linear_term[i+l] = prob.y[i];
            y[i+l] = -1;
        }

        SolverNU s = new SolverNU();
        s.Solve(2*l, new SvrQ(prob,param), linear_term, y,
                alpha2, C, C, param.eps, si, param.shrinking);

        Svm.info("epsilon = " + (-si.solutionR) + "\n");

        for(i=0;i<l;i++)
            alpha[i] = alpha2[i] - alpha2[i+l];
    }

    //
    // decision_function
    //
    static class decision_function
    {
        double[] alpha;
        double rho;
    };

    static decision_function svm_train_one(
            Problem prob, SvmParameter param,
            double Cp, double Cn)
    {
        double[] alpha = new double[prob.l];
        Solver.SolutionInfo si = new Solver.SolutionInfo();
        switch(param.svm_type)
        {
            case SvmParameter.C_SVC:
                solve_c_svc(prob,param,alpha,si,Cp,Cn);
                break;
            case SvmParameter.NU_SVC:
                solve_nu_svc(prob,param,alpha,si);
                break;
            case SvmParameter.ONE_CLASS:
                solve_one_class(prob,param,alpha,si);
                break;
            case SvmParameter.EPSILON_SVR:
                solve_epsilon_svr(prob,param,alpha,si);
                break;
            case SvmParameter.NU_SVR:
                solve_nu_svr(prob,param,alpha,si);
                break;
        }

        Svm.info("obj = " + si.object + ", rho = " + si.rho + "\n");

        // output SVs

        int nSV = 0;
        int nBSV = 0;
        for(int i=0;i<prob.l;i++)
        {
            if(Math.abs(alpha[i]) > 0)
            {
                ++nSV;
                if(prob.y[i] > 0)
                {
                    if(Math.abs(alpha[i]) >= si.upperBoundP)
                        ++nBSV;
                }
                else
                {
                    if(Math.abs(alpha[i]) >= si.upperBoundN)
                        ++nBSV;
                }
            }
        }

        Svm.info("nSV = " + nSV + ", nBSV = " + nBSV + "\n");

        decision_function f = new decision_function();
        f.alpha = alpha;
        f.rho = si.rho;
        return f;
    }

    // Platt's binary SVM Probablistic Output: an improvement from Lin et al.
    private static void sigmoid_train(int l, double[] dec_values, double[] labels,
                                      double[] probAB)
    {
        double A, B;
        double prior1=0, prior0 = 0;
        int i;

        for (i=0;i<l;i++)
            if (labels[i] > 0) prior1+=1;
            else prior0+=1;

        int max_iter=100;	// Maximal number of iterations
        double min_step=1e-10;	// Minimal step taken in line search
        double sigma=1e-12;	// For numerically strict PD of Hessian
        double eps=1e-5;
        double hiTarget=(prior1+1.0)/(prior1+2.0);
        double loTarget=1/(prior0+2.0);
        double[] t= new double[l];
        double fApB,p,q,h11,h22,h21,g1,g2,det,dA,dB,gd,stepsize;
        double newA,newB,newf,d1,d2;
        int iter;

        // Initial Point and Initial Fun Value
        A=0.0; B=Math.log((prior0+1.0)/(prior1+1.0));
        double fval = 0.0;

        for (i=0;i<l;i++)
        {
            if (labels[i]>0) t[i]=hiTarget;
            else t[i]=loTarget;
            fApB = dec_values[i]*A+B;
            if (fApB>=0)
                fval += t[i]*fApB + Math.log(1+Math.exp(-fApB));
            else
                fval += (t[i] - 1)*fApB +Math.log(1+Math.exp(fApB));
        }
        for (iter=0;iter<max_iter;iter++)
        {
            // Update Gradient and Hessian (use H' = H + sigma I)
            h11=sigma; // numerically ensures strict PD
            h22=sigma;
            h21=0.0;g1=0.0;g2=0.0;
            for (i=0;i<l;i++)
            {
                fApB = dec_values[i]*A+B;
                if (fApB >= 0)
                {
                    p=Math.exp(-fApB)/(1.0+Math.exp(-fApB));
                    q=1.0/(1.0+Math.exp(-fApB));
                }
                else
                {
                    p=1.0/(1.0+Math.exp(fApB));
                    q=Math.exp(fApB)/(1.0+Math.exp(fApB));
                }
                d2=p*q;
                h11+=dec_values[i]*dec_values[i]*d2;
                h22+=d2;
                h21+=dec_values[i]*d2;
                d1=t[i]-p;
                g1+=dec_values[i]*d1;
                g2+=d1;
            }

            // Stopping Criteria
            if (Math.abs(g1)<eps && Math.abs(g2)<eps)
                break;

            // Finding Newton direction: -inv(H') * g
            det=h11*h22-h21*h21;
            dA=-(h22*g1 - h21 * g2) / det;
            dB=-(-h21*g1+ h11 * g2) / det;
            gd=g1*dA+g2*dB;


            stepsize = 1;		// Line Search
            while (stepsize >= min_step)
            {
                newA = A + stepsize * dA;
                newB = B + stepsize * dB;

                // New function value
                newf = 0.0;
                for (i=0;i<l;i++)
                {
                    fApB = dec_values[i]*newA+newB;
                    if (fApB >= 0)
                        newf += t[i]*fApB + Math.log(1+Math.exp(-fApB));
                    else
                        newf += (t[i] - 1)*fApB +Math.log(1+Math.exp(fApB));
                }
                // Check sufficient decrease
                if (newf<fval+0.0001*stepsize*gd)
                {
                    A=newA;B=newB;fval=newf;
                    break;
                }
                else
                    stepsize = stepsize / 2.0;
            }

            if (stepsize < min_step)
            {
                Svm.info("Line search fails in two-class probability estimates\n");
                break;
            }
        }

        if (iter>=max_iter)
            Svm.info("Reaching maximal iterations in two-class probability estimates\n");
        probAB[0]=A;probAB[1]=B;
    }

    private static double sigmoid_predict(double decision_value, double A, double B)
    {
        double fApB = decision_value*A+B;
        if (fApB >= 0)
            return Math.exp(-fApB)/(1.0+Math.exp(-fApB));
        else
            return 1.0/(1+Math.exp(fApB)) ;
    }

    // Method 2 from the multiclass_prob paper by Wu, Lin, and Weng
    private static void multiclass_probability(int k, double[][] r, double[] p)
    {
        int t,j;
        int iter = 0, max_iter=Math.max(100,k);
        double[][] Q=new double[k][k];
        double[] Qp=new double[k];
        double pQp, eps=0.005/k;

        for (t=0;t<k;t++)
        {
            p[t]=1.0/k;  // Valid if k = 1
            Q[t][t]=0;
            for (j=0;j<t;j++)
            {
                Q[t][t]+=r[j][t]*r[j][t];
                Q[t][j]=Q[j][t];
            }
            for (j=t+1;j<k;j++)
            {
                Q[t][t]+=r[j][t]*r[j][t];
                Q[t][j]=-r[j][t]*r[t][j];
            }
        }
        for (iter=0;iter<max_iter;iter++)
        {
            // stopping condition, recalculate QP,pQP for numerical accuracy
            pQp=0;
            for (t=0;t<k;t++)
            {
                Qp[t]=0;
                for (j=0;j<k;j++)
                    Qp[t]+=Q[t][j]*p[j];
                pQp+=p[t]*Qp[t];
            }
            double max_error=0;
            for (t=0;t<k;t++)
            {
                double error=Math.abs(Qp[t]-pQp);
                if (error>max_error)
                    max_error=error;
            }
            if (max_error<eps) break;

            for (t=0;t<k;t++)
            {
                double diff=(-Qp[t]+pQp)/Q[t][t];
                p[t]+=diff;
                pQp=(pQp+diff*(diff*Q[t][t]+2*Qp[t]))/(1+diff)/(1+diff);
                for (j=0;j<k;j++)
                {
                    Qp[j]=(Qp[j]+diff*Q[t][j])/(1+diff);
                    p[j]/=(1+diff);
                }
            }
        }
        if (iter>=max_iter)
            Svm.info("Exceeds max_iter in multiclass_prob\n");
    }

    // Cross-validation decision values for probability estimates
    private static void svm_binary_svc_probability(Problem prob, SvmParameter param, double Cp, double Cn, double[] probAB)
    {
        int i;
        int nr_fold = 5;
        int[] perm = new int[prob.l];
        double[] dec_values = new double[prob.l];

        // random shuffle
        for(i=0;i<prob.l;i++) perm[i]=i;
        for(i=0;i<prob.l;i++)
        {
            int j = i+rand.nextInt(prob.l-i);
            do {int _=perm[i]; perm[i]=perm[j]; perm[j]=_;} while(false);
        }
        for(i=0;i<nr_fold;i++)
        {
            int begin = i*prob.l/nr_fold;
            int end = (i+1)*prob.l/nr_fold;
            int j,k;
            Problem subprob = new Problem();

            subprob.l = prob.l-(end-begin);
            subprob.x = new Node[subprob.l][];
            subprob.y = new double[subprob.l];

            k=0;
            for(j=0;j<begin;j++)
            {
                subprob.x[k] = prob.x[perm[j]];
                subprob.y[k] = prob.y[perm[j]];
                ++k;
            }
            for(j=end;j<prob.l;j++)
            {
                subprob.x[k] = prob.x[perm[j]];
                subprob.y[k] = prob.y[perm[j]];
                ++k;
            }
            int p_count=0,n_count=0;
            for(j=0;j<k;j++)
                if(subprob.y[j]>0)
                    p_count++;
                else
                    n_count++;

            if(p_count==0 && n_count==0)
                for(j=begin;j<end;j++)
                    dec_values[perm[j]] = 0;
            else if(p_count > 0 && n_count == 0)
                for(j=begin;j<end;j++)
                    dec_values[perm[j]] = 1;
            else if(p_count == 0 && n_count > 0)
                for(j=begin;j<end;j++)
                    dec_values[perm[j]] = -1;
            else
            {
                SvmParameter subparam = (SvmParameter)param.clone();
                subparam.probability=0;
                subparam.C=1.0;
                subparam.nr_weight=2;
                subparam.weight_label = new int[2];
                subparam.weight = new double[2];
                subparam.weight_label[0]=+1;
                subparam.weight_label[1]=-1;
                subparam.weight[0]=Cp;
                subparam.weight[1]=Cn;
                SvmModel submodel = svm_train(subprob,subparam);
                for(j=begin;j<end;j++)
                {
                    double[] dec_value=new double[1];
                    svm_predict_values(submodel,prob.x[perm[j]],dec_value);
                    dec_values[perm[j]]=dec_value[0];
                    // ensure +1 -1 order; reason not using CV subroutine
                    dec_values[perm[j]] *= submodel.label[0];
                }
            }
        }
        sigmoid_train(prob.l,dec_values,prob.y,probAB);
    }

    // Return parameter of a Laplace distribution
    private static double svm_svr_probability(Problem prob, SvmParameter param)
    {
        int i;
        int nr_fold = 5;
        double[] ymv = new double[prob.l];
        double mae = 0;

        SvmParameter newparam = (SvmParameter)param.clone();
        newparam.probability = 0;
        svm_cross_validation(prob,newparam,nr_fold,ymv);
        for(i=0;i<prob.l;i++)
        {
            ymv[i]=prob.y[i]-ymv[i];
            mae += Math.abs(ymv[i]);
        }
        mae /= prob.l;
        double std=Math.sqrt(2*mae*mae);
        int count=0;
        mae=0;
        for(i=0;i<prob.l;i++)
            if (Math.abs(ymv[i]) > 5*std)
                count=count+1;
            else
                mae+=Math.abs(ymv[i]);
        mae /= (prob.l-count);
        Svm.info("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma=" + mae + "\n");
        return mae;
    }

    // label: label name, start: begin of each class, count: #data of classes, perm: indices to the original data
    // perm, length l, must be allocated before calling this subroutine
    private static void svm_group_classes(Problem prob, int[] nr_class_ret, int[][] label_ret, int[][] start_ret, int[][] count_ret, int[] perm)
    {
        int l = prob.l;
        int max_nr_class = 16;
        int nr_class = 0;
        int[] label = new int[max_nr_class];
        int[] count = new int[max_nr_class];
        int[] data_label = new int[l];
        int i;

        for(i=0;i<l;i++)
        {
            int this_label = (int)(prob.y[i]);
            int j;
            for(j=0;j<nr_class;j++)
            {
                if(this_label == label[j])
                {
                    ++count[j];
                    break;
                }
            }
            data_label[i] = j;
            if(j == nr_class)
            {
                if(nr_class == max_nr_class)
                {
                    max_nr_class *= 2;
                    int[] new_data = new int[max_nr_class];
                    System.arraycopy(label,0,new_data,0,label.length);
                    label = new_data;
                    new_data = new int[max_nr_class];
                    System.arraycopy(count,0,new_data,0,count.length);
                    count = new_data;
                }
                label[nr_class] = this_label;
                count[nr_class] = 1;
                ++nr_class;
            }
        }

        //
        // Labels are ordered by their first occurrence in the training set.
        // However, for two-class sets with -1/+1 labels and -1 appears first,
        // we swap labels to ensure that internally the binary SVM has positive data corresponding to the +1 instances.
        //
        if (nr_class == 2 && label[0] == -1 && label[1] == +1)
        {
            do {int _=label[0]; label[0]=label[1]; label[1]=_;} while(false);
            do {int _=count[0]; count[0]=count[1]; count[1]=_;} while(false);
            for(i=0;i<l;i++)
            {
                if(data_label[i] == 0)
                    data_label[i] = 1;
                else
                    data_label[i] = 0;
            }
        }

        int[] start = new int[nr_class];
        start[0] = 0;
        for(i=1;i<nr_class;i++)
            start[i] = start[i-1]+count[i-1];
        for(i=0;i<l;i++)
        {
            perm[start[data_label[i]]] = i;
            ++start[data_label[i]];
        }
        start[0] = 0;
        for(i=1;i<nr_class;i++)
            start[i] = start[i-1]+count[i-1];

        nr_class_ret[0] = nr_class;
        label_ret[0] = label;
        start_ret[0] = start;
        count_ret[0] = count;
    }

    //
    // Interface functions
    //
    public static SvmModel svm_train(Problem prob, SvmParameter param)
    {
        SvmModel model = new SvmModel();
        model.param = param;

        if(param.svm_type == SvmParameter.ONE_CLASS ||
                param.svm_type == SvmParameter.EPSILON_SVR ||
                param.svm_type == SvmParameter.NU_SVR)
        {
            // regression or one-class-svm
            model.nr_class = 2;
            model.label = null;
            model.nSV = null;
            model.probA = null; model.probB = null;
            model.sv_coef = new double[1][];

            if(param.probability == 1 &&
                    (param.svm_type == SvmParameter.EPSILON_SVR ||
                            param.svm_type == SvmParameter.NU_SVR))
            {
                model.probA = new double[1];
                model.probA[0] = svm_svr_probability(prob,param);
            }

            decision_function f = svm_train_one(prob,param,0,0);
            model.rho = new double[1];
            model.rho[0] = f.rho;

            int nSV = 0;
            int i;
            for(i=0;i<prob.l;i++)
                if(Math.abs(f.alpha[i]) > 0) ++nSV;
            model.l = nSV;
            model.SV = new Node[nSV][];
            model.sv_coef[0] = new double[nSV];
            model.sv_indices = new int[nSV];
            int j = 0;
            for(i=0;i<prob.l;i++)
                if(Math.abs(f.alpha[i]) > 0)
                {
                    model.SV[j] = prob.x[i];
                    model.sv_coef[0][j] = f.alpha[i];
                    model.sv_indices[j] = i+1;
                    ++j;
                }
        }
        else
        {
            // classification
            int l = prob.l;
            int[] tmp_nr_class = new int[1];
            int[][] tmp_label = new int[1][];
            int[][] tmp_start = new int[1][];
            int[][] tmp_count = new int[1][];
            int[] perm = new int[l];

            // group training data of the same class
            svm_group_classes(prob,tmp_nr_class,tmp_label,tmp_start,tmp_count,perm);
            int nr_class = tmp_nr_class[0];
            int[] label = tmp_label[0];
            int[] start = tmp_start[0];
            int[] count = tmp_count[0];

            if(nr_class == 1)
                Svm.info("WARNING: training data in only one class. See README for details.\n");

            Node[][] x = new Node[l][];
            int i;
            for(i=0;i<l;i++)
                x[i] = prob.x[perm[i]];

            // calculate weighted C

            double[] weighted_C = new double[nr_class];
            for(i=0;i<nr_class;i++)
                weighted_C[i] = param.C;
            for(i=0;i<param.nr_weight;i++)
            {
                int j;
                for(j=0;j<nr_class;j++)
                    if(param.weight_label[i] == label[j])
                        break;
                if(j == nr_class)
                    System.err.print("WARNING: class label "+param.weight_label[i]+" specified in weight is not found\n");
                else
                    weighted_C[j] *= param.weight[i];
            }

            // train k*(k-1)/2 models

            boolean[] nonzero = new boolean[l];
            for(i=0;i<l;i++)
                nonzero[i] = false;
            decision_function[] f = new decision_function[nr_class*(nr_class-1)/2];

            double[] probA=null,probB=null;
            if (param.probability == 1)
            {
                probA=new double[nr_class*(nr_class-1)/2];
                probB=new double[nr_class*(nr_class-1)/2];
            }

            int p = 0;
            for(i=0;i<nr_class;i++)
                for(int j=i+1;j<nr_class;j++)
                {
                    Problem sub_prob = new Problem();
                    int si = start[i], sj = start[j];
                    int ci = count[i], cj = count[j];
                    sub_prob.l = ci+cj;
                    sub_prob.x = new Node[sub_prob.l][];
                    sub_prob.y = new double[sub_prob.l];
                    int k;
                    for(k=0;k<ci;k++)
                    {
                        sub_prob.x[k] = x[si+k];
                        sub_prob.y[k] = +1;
                    }
                    for(k=0;k<cj;k++)
                    {
                        sub_prob.x[ci+k] = x[sj+k];
                        sub_prob.y[ci+k] = -1;
                    }

                    if(param.probability == 1)
                    {
                        double[] probAB=new double[2];
                        svm_binary_svc_probability(sub_prob,param,weighted_C[i],weighted_C[j],probAB);
                        probA[p]=probAB[0];
                        probB[p]=probAB[1];
                    }

                    f[p] = svm_train_one(sub_prob,param,weighted_C[i],weighted_C[j]);
                    for(k=0;k<ci;k++)
                        if(!nonzero[si+k] && Math.abs(f[p].alpha[k]) > 0)
                            nonzero[si+k] = true;
                    for(k=0;k<cj;k++)
                        if(!nonzero[sj+k] && Math.abs(f[p].alpha[ci+k]) > 0)
                            nonzero[sj+k] = true;
                    ++p;
                }

            // build output

            model.nr_class = nr_class;

            model.label = new int[nr_class];
            for(i=0;i<nr_class;i++)
                model.label[i] = label[i];

            model.rho = new double[nr_class*(nr_class-1)/2];
            for(i=0;i<nr_class*(nr_class-1)/2;i++)
                model.rho[i] = f[i].rho;

            if(param.probability == 1)
            {
                model.probA = new double[nr_class*(nr_class-1)/2];
                model.probB = new double[nr_class*(nr_class-1)/2];
                for(i=0;i<nr_class*(nr_class-1)/2;i++)
                {
                    model.probA[i] = probA[i];
                    model.probB[i] = probB[i];
                }
            }
            else
            {
                model.probA=null;
                model.probB=null;
            }

            int nnz = 0;
            int[] nz_count = new int[nr_class];
            model.nSV = new int[nr_class];
            for(i=0;i<nr_class;i++)
            {
                int nSV = 0;
                for(int j=0;j<count[i];j++)
                    if(nonzero[start[i]+j])
                    {
                        ++nSV;
                        ++nnz;
                    }
                model.nSV[i] = nSV;
                nz_count[i] = nSV;
            }

            Svm.info("Total nSV = " + nnz + "\n");

            model.l = nnz;
            model.SV = new Node[nnz][];
            model.sv_indices = new int[nnz];
            p = 0;
            for(i=0;i<l;i++)
                if(nonzero[i])
                {
                    model.SV[p] = x[i];
                    model.sv_indices[p++] = perm[i] + 1;
                }

            int[] nz_start = new int[nr_class];
            nz_start[0] = 0;
            for(i=1;i<nr_class;i++)
                nz_start[i] = nz_start[i-1]+nz_count[i-1];

            model.sv_coef = new double[nr_class-1][];
            for(i=0;i<nr_class-1;i++)
                model.sv_coef[i] = new double[nnz];

            p = 0;
            for(i=0;i<nr_class;i++)
                for(int j=i+1;j<nr_class;j++)
                {
                    // classifier (i,j): coefficients with
                    // i are in sv_coef[j-1][nz_start[i]...],
                    // j are in sv_coef[i][nz_start[j]...]

                    int si = start[i];
                    int sj = start[j];
                    int ci = count[i];
                    int cj = count[j];

                    int q = nz_start[i];
                    int k;
                    for(k=0;k<ci;k++)
                        if(nonzero[si+k])
                            model.sv_coef[j-1][q++] = f[p].alpha[k];
                    q = nz_start[j];
                    for(k=0;k<cj;k++)
                        if(nonzero[sj+k])
                            model.sv_coef[i][q++] = f[p].alpha[ci+k];
                    ++p;
                }
        }
        return model;
    }

    // Stratified cross validation
    public static void svm_cross_validation(Problem prob, SvmParameter param, int nr_fold, double[] target)
    {
        int i;
        int[] fold_start = new int[nr_fold+1];
        int l = prob.l;
        int[] perm = new int[l];

        // stratified cv may not give leave-one-out rate
        // Each class to l folds -> some folds may have zero elements
        if((param.svm_type == SvmParameter.C_SVC ||
                param.svm_type == SvmParameter.NU_SVC) && nr_fold < l)
        {
            int[] tmp_nr_class = new int[1];
            int[][] tmp_label = new int[1][];
            int[][] tmp_start = new int[1][];
            int[][] tmp_count = new int[1][];

            svm_group_classes(prob,tmp_nr_class,tmp_label,tmp_start,tmp_count,perm);

            int nr_class = tmp_nr_class[0];
            int[] start = tmp_start[0];
            int[] count = tmp_count[0];

            // random shuffle and then data grouped by fold using the array perm
            int[] fold_count = new int[nr_fold];
            int c;
            int[] index = new int[l];
            for(i=0;i<l;i++)
                index[i]=perm[i];
            for (c=0; c<nr_class; c++)
                for(i=0;i<count[c];i++)
                {
                    int j = i+rand.nextInt(count[c]-i);
                    do {int _=index[start[c]+j]; index[start[c]+j]=index[start[c]+i]; index[start[c]+i]=_;} while(false);
                }
            for(i=0;i<nr_fold;i++)
            {
                fold_count[i] = 0;
                for (c=0; c<nr_class;c++)
                    fold_count[i]+=(i+1)*count[c]/nr_fold-i*count[c]/nr_fold;
            }
            fold_start[0]=0;
            for (i=1;i<=nr_fold;i++)
                fold_start[i] = fold_start[i-1]+fold_count[i-1];
            for (c=0; c<nr_class;c++)
                for(i=0;i<nr_fold;i++)
                {
                    int begin = start[c]+i*count[c]/nr_fold;
                    int end = start[c]+(i+1)*count[c]/nr_fold;
                    for(int j=begin;j<end;j++)
                    {
                        perm[fold_start[i]] = index[j];
                        fold_start[i]++;
                    }
                }
            fold_start[0]=0;
            for (i=1;i<=nr_fold;i++)
                fold_start[i] = fold_start[i-1]+fold_count[i-1];
        }
        else
        {
            for(i=0;i<l;i++) perm[i]=i;
            for(i=0;i<l;i++)
            {
                int j = i+rand.nextInt(l-i);
                do {int _=perm[i]; perm[i]=perm[j]; perm[j]=_;} while(false);
            }
            for(i=0;i<=nr_fold;i++)
                fold_start[i]=i*l/nr_fold;
        }

        for(i=0;i<nr_fold;i++)
        {
            int begin = fold_start[i];
            int end = fold_start[i+1];
            int j,k;
            Problem subprob = new Problem();

            subprob.l = l-(end-begin);
            subprob.x = new Node[subprob.l][];
            subprob.y = new double[subprob.l];

            k=0;
            for(j=0;j<begin;j++)
            {
                subprob.x[k] = prob.x[perm[j]];
                subprob.y[k] = prob.y[perm[j]];
                ++k;
            }
            for(j=end;j<l;j++)
            {
                subprob.x[k] = prob.x[perm[j]];
                subprob.y[k] = prob.y[perm[j]];
                ++k;
            }
            SvmModel submodel = svm_train(subprob,param);
            if(param.probability==1 &&
                    (param.svm_type == SvmParameter.C_SVC ||
                            param.svm_type == SvmParameter.NU_SVC))
            {
                double[] prob_estimates= new double[svm_get_nr_class(submodel)];
                for(j=begin;j<end;j++)
                    target[perm[j]] = svm_predict_probability(submodel,prob.x[perm[j]],prob_estimates);
            }
            else
                for(j=begin;j<end;j++)
                    target[perm[j]] = svm_predict(submodel,prob.x[perm[j]]);
        }
    }

    public static int svm_get_svm_type(Model model)
    {
        return model.getParam().svm_type;
    }

    public static int svm_get_nr_class(Model model)
    {
        return model.getNr_class();
    }

    public static void svm_get_labels(Model model, int[] label)
    {
        if (model.getLabel() != null)
            for(int i=0;i<model.getNr_class();i++)
                label[i] = model.getLabel()[i];
    }

    public static void svm_get_sv_indices(SvmModel model, int[] indices)
    {
        if (model.sv_indices != null)
            for(int i=0;i<model.l;i++)
                indices[i] = model.sv_indices[i];
    }

    public static int svm_get_nr_sv(SvmModel model)
    {
        return model.l;
    }

    public static double svm_get_svr_probability(Model model)
    {
        if ((model.getParam().svm_type == SvmParameter.EPSILON_SVR || model.getParam().svm_type == SvmParameter.NU_SVR) &&
                model.getProbA()!=null)
            return model.getProbA()[0];
        else
        {
            System.err.print("Model doesn't contain information for SVR probability inference\n");
            return 0;
        }
    }

    public static double svm_predict_values(Model model, Node[] x, double[] dec_values)
    {
        int i;
        if(model.getParam().svm_type == SvmParameter.ONE_CLASS ||
                model.getParam().svm_type == SvmParameter.EPSILON_SVR ||
                model.getParam().svm_type == SvmParameter.NU_SVR)
        {
            double[] sv_coef = model.getSv_coef()[0];
            double sum = 0;
            for(i=0;i<model.getL();i++)
                sum += sv_coef[i] * Kernel.kFunction(x, model.getSV()[i], model.getParam());
            sum -= model.getRho()[0];
            dec_values[0] = sum;

            if(model.getParam().svm_type == SvmParameter.ONE_CLASS)
                return (sum>0)?1:-1;
            else
                return sum;
        }
        else
        {
            int nr_class = model.getNr_class();
            int l = model.getL();

            double[] kvalue = new double[l];
            for(i=0;i<l;i++)
                kvalue[i] = Kernel.kFunction(x, model.getSV()[i], model.getParam());

            int[] start = new int[nr_class];
            start[0] = 0;
            for(i=1;i<nr_class;i++)
                start[i] = start[i-1]+model.getnSV()[i-1];

            int[] vote = new int[nr_class];
            for(i=0;i<nr_class;i++)
                vote[i] = 0;

            int p=0;
            for(i=0;i<nr_class;i++)
                for(int j=i+1;j<nr_class;j++)
                {
                    double sum = 0;
                    int si = start[i];
                    int sj = start[j];
                    int ci = model.getnSV()[i];
                    int cj = model.getnSV()[j];

                    int k;
                    double[] coef1 = model.getSv_coef()[j-1];
                    double[] coef2 = model.getSv_coef()[i];
                    for(k=0;k<ci;k++)
                        sum += coef1[si+k] * kvalue[si+k];
                    for(k=0;k<cj;k++)
                        sum += coef2[sj+k] * kvalue[sj+k];
                    sum -= model.getRho()[p];
                    dec_values[p] = sum;

                    if(dec_values[p] > 0)
                        ++vote[i];
                    else
                        ++vote[j];
                    p++;
                }

            int vote_max_idx = 0;
            for(i=1;i<nr_class;i++)
                if(vote[i] > vote[vote_max_idx])
                    vote_max_idx = i;

            return model.getLabel()[vote_max_idx];
        }
    }

    public static double svm_predict(Model model, Node[] x)
    {
        int nr_class = model.getNr_class();
        double[] dec_values;
        if(model.getParam().svm_type == SvmParameter.ONE_CLASS ||
                model.getParam().svm_type == SvmParameter.EPSILON_SVR ||
                model.getParam().svm_type == SvmParameter.NU_SVR)
            dec_values = new double[1];
        else
            dec_values = new double[nr_class*(nr_class-1)/2];
        double pred_result = svm_predict_values(model, x, dec_values);
        return pred_result;
    }

    public static double svm_predict_probability(Model model, Node[] x, double[] prob_estimates)
    {
        if ((model.getParam().svm_type == SvmParameter.C_SVC || model.getParam().svm_type == SvmParameter.NU_SVC) &&
                model.getProbA() !=null && model.getProbB() !=null)
        {
            int i;
            int nr_class = model.getNr_class();
            double[] dec_values = new double[nr_class*(nr_class-1)/2];
            svm_predict_values(model, x, dec_values);

            double min_prob=1e-7;
            double[][] pairwise_prob=new double[nr_class][nr_class];

            int k=0;
            for(i=0;i<nr_class;i++)
                for(int j=i+1;j<nr_class;j++)
                {
                    pairwise_prob[i][j]=Math.min(Math.max(sigmoid_predict(dec_values[k],model.getProbA()[k],model.getProbB()[k]),min_prob),1-min_prob);
                    pairwise_prob[j][i]=1-pairwise_prob[i][j];
                    k++;
                }
            multiclass_probability(nr_class,pairwise_prob,prob_estimates);

            int prob_max_idx = 0;
            for(i=1;i<nr_class;i++)
                if(prob_estimates[i] > prob_estimates[prob_max_idx])
                    prob_max_idx = i;
            return model.getLabel()[prob_max_idx];
        }
        else
            return svm_predict(model, x);
    }

    static final String svm_type_table[] =
            {
                    "c_svc","nu_svc","one_class","epsilon_svr","nu_svr",
            };

    static final String kernel_type_table[]=
            {
                    "linear","polynomial","rbf","sigmoid","precomputed"
            };

    public static void svm_save_model(String model_file_name, SvmModel model) throws IOException
    {
        DataOutputStream fp = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(model_file_name)));

        SvmParameter param = model.param;

        fp.writeBytes("svm_type "+svm_type_table[param.svm_type]+"\n");
        fp.writeBytes("kernel_type "+kernel_type_table[param.kernel_type]+"\n");

        if(param.kernel_type == SvmParameter.POLY)
            fp.writeBytes("degree "+param.degree+"\n");

        if(param.kernel_type == SvmParameter.POLY ||
                param.kernel_type == SvmParameter.RBF ||
                param.kernel_type == SvmParameter.SIGMOID)
            fp.writeBytes("gamma "+param.gamma+"\n");

        if(param.kernel_type == SvmParameter.POLY ||
                param.kernel_type == SvmParameter.SIGMOID)
            fp.writeBytes("coef0 "+param.coef0+"\n");

        int nr_class = model.nr_class;
        int l = model.l;
        fp.writeBytes("nr_class "+nr_class+"\n");
        fp.writeBytes("total_sv "+l+"\n");

        {
            fp.writeBytes("rho");
            for(int i=0;i<nr_class*(nr_class-1)/2;i++)
                fp.writeBytes(" "+model.rho[i]);
            fp.writeBytes("\n");
        }

        if(model.label != null)
        {
            fp.writeBytes("label");
            for(int i=0;i<nr_class;i++)
                fp.writeBytes(" "+model.label[i]);
            fp.writeBytes("\n");
        }

        if(model.probA != null) // regression has probA only
        {
            fp.writeBytes("probA");
            for(int i=0;i<nr_class*(nr_class-1)/2;i++)
                fp.writeBytes(" "+model.probA[i]);
            fp.writeBytes("\n");
        }
        if(model.probB != null)
        {
            fp.writeBytes("probB");
            for(int i=0;i<nr_class*(nr_class-1)/2;i++)
                fp.writeBytes(" "+model.probB[i]);
            fp.writeBytes("\n");
        }

        if(model.nSV != null)
        {
            fp.writeBytes("nr_sv");
            for(int i=0;i<nr_class;i++)
                fp.writeBytes(" "+model.nSV[i]);
            fp.writeBytes("\n");
        }

        fp.writeBytes("SV\n");
        double[][] sv_coef = model.sv_coef;
        Node[][] SV = model.SV;

        for(int i=0;i<l;i++)
        {
            for(int j=0;j<nr_class-1;j++)
                fp.writeBytes(sv_coef[j][i]+" ");

            Node[] p = SV[i];
            if(param.kernel_type == SvmParameter.PRECOMPUTED)
                fp.writeBytes("0:"+(int)(p[0].value));
            else
                for(int j=0;j<p.length;j++)
                    fp.writeBytes(p[j].index+":"+p[j].value+" ");
            fp.writeBytes("\n");
        }

        fp.close();
    }

    private static double atof(String s)
    {
        return Double.valueOf(s).doubleValue();
    }

    private static int atoi(String s)
    {
        return Integer.parseInt(s);
    }

    private static boolean read_model_header(BufferedReader fp, SvmModel model)
    {
        SvmParameter param = new SvmParameter();
        model.param = param;
        try
        {
            while(true)
            {
                String cmd = fp.readLine();
                String arg = cmd.substring(cmd.indexOf(' ')+1);

                if(cmd.startsWith("svm_type"))
                {
                    int i;
                    for(i=0;i<svm_type_table.length;i++)
                    {
                        if(arg.indexOf(svm_type_table[i])!=-1)
                        {
                            param.svm_type=i;
                            break;
                        }
                    }
                    if(i == svm_type_table.length)
                    {
                        System.err.print("unknown svm type.\n");
                        return false;
                    }
                }
                else if(cmd.startsWith("kernel_type"))
                {
                    int i;
                    for(i=0;i<kernel_type_table.length;i++)
                    {
                        if(arg.indexOf(kernel_type_table[i])!=-1)
                        {
                            param.kernel_type=i;
                            break;
                        }
                    }
                    if(i == kernel_type_table.length)
                    {
                        System.err.print("unknown kernel function.\n");
                        return false;
                    }
                }
                else if(cmd.startsWith("degree"))
                    param.degree = atoi(arg);
                else if(cmd.startsWith("gamma"))
                    param.gamma = atof(arg);
                else if(cmd.startsWith("coef0"))
                    param.coef0 = atof(arg);
                else if(cmd.startsWith("nr_class"))
                    model.nr_class = atoi(arg);
                else if(cmd.startsWith("total_sv"))
                    model.l = atoi(arg);
                else if(cmd.startsWith("rho"))
                {
                    int n = model.nr_class * (model.nr_class-1)/2;
                    model.rho = new double[n];
                    StringTokenizer st = new StringTokenizer(arg);
                    for(int i=0;i<n;i++)
                        model.rho[i] = atof(st.nextToken());
                }
                else if(cmd.startsWith("label"))
                {
                    int n = model.nr_class;
                    model.label = new int[n];
                    StringTokenizer st = new StringTokenizer(arg);
                    for(int i=0;i<n;i++)
                        model.label[i] = atoi(st.nextToken());
                }
                else if(cmd.startsWith("probA"))
                {
                    int n = model.nr_class*(model.nr_class-1)/2;
                    model.probA = new double[n];
                    StringTokenizer st = new StringTokenizer(arg);
                    for(int i=0;i<n;i++)
                        model.probA[i] = atof(st.nextToken());
                }
                else if(cmd.startsWith("probB"))
                {
                    int n = model.nr_class*(model.nr_class-1)/2;
                    model.probB = new double[n];
                    StringTokenizer st = new StringTokenizer(arg);
                    for(int i=0;i<n;i++)
                        model.probB[i] = atof(st.nextToken());
                }
                else if(cmd.startsWith("nr_sv"))
                {
                    int n = model.nr_class;
                    model.nSV = new int[n];
                    StringTokenizer st = new StringTokenizer(arg);
                    for(int i=0;i<n;i++)
                        model.nSV[i] = atoi(st.nextToken());
                }
                else if(cmd.startsWith("SV"))
                {
                    break;
                }
                else
                {
                    System.err.print("unknown text in model file: ["+cmd+"]\n");
                    return false;
                }
            }
        }
        catch(Exception e)
        {
            return false;
        }
        return true;
    }

    public static SvmModel svm_load_model(String model_file_name) throws IOException
    {
        return svm_load_model(new BufferedReader(new FileReader(model_file_name)));
    }

    public static SvmModel svm_load_model(BufferedReader fp) throws IOException
    {
        // read parameters

        SvmModel model = new SvmModel();
        model.rho = null;
        model.probA = null;
        model.probB = null;
        model.label = null;
        model.nSV = null;

        if (read_model_header(fp, model) == false)
        {
            System.err.print("ERROR: failed to read model\n");
            return null;
        }

        // read sv_coef and SV

        int m = model.nr_class - 1;
        int l = model.l;
        model.sv_coef = new double[m][l];
        model.SV = new Node[l][];

        for(int i=0;i<l;i++)
        {
            String line = fp.readLine();
            StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");

            for(int k=0;k<m;k++)
                model.sv_coef[k][i] = atof(st.nextToken());
            int n = st.countTokens()/2;
            model.SV[i] = new Node[n];
            for(int j=0;j<n;j++)
            {
                model.SV[i][j] = new Node();
                model.SV[i][j].index = atoi(st.nextToken());
                model.SV[i][j].value = atof(st.nextToken());
            }
        }

        fp.close();
        return model;
    }

    public static String svm_check_parameter(Problem prob, SvmParameter param)
    {
        // svm_type

        int svm_type = param.svm_type;
        if(svm_type != SvmParameter.C_SVC &&
                svm_type != SvmParameter.NU_SVC &&
                svm_type != SvmParameter.ONE_CLASS &&
                svm_type != SvmParameter.EPSILON_SVR &&
                svm_type != SvmParameter.NU_SVR)
            return "unknown svm type";

        // kernel_type, degree

        int kernel_type = param.kernel_type;
        if(kernel_type != SvmParameter.LINEAR &&
                kernel_type != SvmParameter.POLY &&
                kernel_type != SvmParameter.RBF &&
                kernel_type != SvmParameter.SIGMOID &&
                kernel_type != SvmParameter.PRECOMPUTED)
            return "unknown kernel type";

        if(param.gamma < 0)
            return "gamma < 0";

        if(param.degree < 0)
            return "degree of polynomial kernel < 0";

        // cache_size,eps,C,nu,p,shrinking

        if(param.cache_size <= 0)
            return "cache_size <= 0";

        if(param.eps <= 0)
            return "eps <= 0";

        if(svm_type == SvmParameter.C_SVC ||
                svm_type == SvmParameter.EPSILON_SVR ||
                svm_type == SvmParameter.NU_SVR)
            if(param.C <= 0)
                return "C <= 0";

        if(svm_type == SvmParameter.NU_SVC ||
                svm_type == SvmParameter.ONE_CLASS ||
                svm_type == SvmParameter.NU_SVR)
            if(param.nu <= 0 || param.nu > 1)
                return "nu <= 0 or nu > 1";

        if(svm_type == SvmParameter.EPSILON_SVR)
            if(param.p < 0)
                return "p < 0";

        if(param.shrinking != 0 &&
                param.shrinking != 1)
            return "shrinking != 0 and shrinking != 1";

        if(param.probability != 0 &&
                param.probability != 1)
            return "probability != 0 and probability != 1";

        if(param.probability == 1 &&
                svm_type == SvmParameter.ONE_CLASS)
            return "one-class SVM probability output not supported yet";

        // check whether nu-svc is feasible

        if(svm_type == SvmParameter.NU_SVC)
        {
            int l = prob.l;
            int max_nr_class = 16;
            int nr_class = 0;
            int[] label = new int[max_nr_class];
            int[] count = new int[max_nr_class];

            int i;
            for(i=0;i<l;i++)
            {
                int this_label = (int)prob.y[i];
                int j;
                for(j=0;j<nr_class;j++)
                    if(this_label == label[j])
                    {
                        ++count[j];
                        break;
                    }

                if(j == nr_class)
                {
                    if(nr_class == max_nr_class)
                    {
                        max_nr_class *= 2;
                        int[] new_data = new int[max_nr_class];
                        System.arraycopy(label,0,new_data,0,label.length);
                        label = new_data;

                        new_data = new int[max_nr_class];
                        System.arraycopy(count,0,new_data,0,count.length);
                        count = new_data;
                    }
                    label[nr_class] = this_label;
                    count[nr_class] = 1;
                    ++nr_class;
                }
            }

            for(i=0;i<nr_class;i++)
            {
                int n1 = count[i];
                for(int j=i+1;j<nr_class;j++)
                {
                    int n2 = count[j];
                    if(param.nu*(n1+n2)/2 > Math.min(n1,n2))
                        return "specified nu is infeasible";
                }
            }
        }

        return null;
    }

    public static int svm_check_probability_model(SvmModel model)
    {
        if (((model.param.svm_type == SvmParameter.C_SVC || model.param.svm_type == SvmParameter.NU_SVC) &&
                model.probA!=null && model.probB!=null) ||
                ((model.param.svm_type == SvmParameter.EPSILON_SVR || model.param.svm_type == SvmParameter.NU_SVR) &&
                        model.probA!=null))
            return 1;
        else
            return 0;
    }

    public static void svm_set_print_string_function(PrintInterface print_func)
    {
        if (print_func == null)
            svm_print_string = svm_print_stdout;
        else
            svm_print_string = print_func;
    }
}