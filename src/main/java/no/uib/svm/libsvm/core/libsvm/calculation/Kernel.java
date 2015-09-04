package no.uib.svm.libsvm.core.libsvm.calculation;

import no.uib.svm.libsvm.core.libsvm.Node;
import no.uib.svm.libsvm.core.libsvm.SvmParameter;

/**
 * Created by Markus on 04.09.2015.
 */
abstract class Kernel extends QMatrix {

    private Node[][] node;

    private final double[] xSquare;

    /**
     * SVM parameter
     */
    private final int kernelType;
    private final int degree;
    private final double gamma;
    private final double coef0;

    abstract float[] getQ(int column, int length);

    abstract double[] getQD();

    Kernel(int length, Node[][] node, SvmParameter param) {
        this.kernelType = param.kernel_type;
        this.degree = param.degree;
        this.gamma = param.gamma;
        this.coef0 = param.coef0;

        this.node = node.clone();

        if (kernelType == SvmParameter.RBF) {
            xSquare = new double[length];
            for (int i = 0; i < length; i++) {
                xSquare[i] = dot(this.node[i], this.node[i]);
            }
        } else {
            xSquare = null;
        }


    }

    void swapIndex(int i, int j) {
        {
            Node[] _ = node[i];
            node[i] = node[j];
            node[j] = _;
        }
        if (xSquare != null) {
            {
                double _ = xSquare[i];
                xSquare[i] = xSquare[j];
                xSquare[j] = _;
            }
        }
    }

    private static double powI(double base, int times) {
        double count = base, ret = 1.0;
        for (int t = times; t > 0; t /= 2) {
            if (t % 2 == 1) {
                ret *= count;
            }
            count = count * count;
        }
        return ret;
    }

    private static double dot(Node[] xNode, Node[] yNode) {
        double sum = 0;
        int xLength = xNode.length;
        int yLength = yNode.length;
        int i = 0;
        int j = 0;
        while (i < xLength && j < yLength) {
            if (xNode[i].index == yNode[j].index)
                sum += xNode[i++].value * yNode[j++].value;
            else {
                if (xNode[i].index > yNode[j].index)
                    ++j;
                else
                    ++i;
            }
        }
        return sum;
    }


    protected double kernelFunction(int i, int j) {
        switch (kernelType) {
            case SvmParameter.LINEAR:
                return dot(node[i], node[j]);
            case SvmParameter.POLY:
                return powI(gamma * dot(node[i], node[j]) + coef0, degree);
            case SvmParameter.RBF:
                return Math.exp(-gamma * (xSquare[i] + xSquare[j] - 2 * dot(node[i], node[j])));
            case SvmParameter.SIGMOID:
                return Math.tanh(gamma * dot(node[i], node[j]) + coef0);
            case SvmParameter.PRECOMPUTED:
                return node[i][(int) (node[j][0].value)].value;
            default:
                return 0;    // java
        }
    }

    protected static double kFunction(Node[] xNode, Node[] yNode, SvmParameter param) {
        switch (param.kernel_type) {
            case SvmParameter.LINEAR:
                return dot(xNode, yNode);
            case SvmParameter.POLY:
                return powI(param.gamma * dot(xNode, yNode) + param.coef0, param.degree);
            case SvmParameter.RBF: {
                return Math.exp(-param.gamma * calculateRBF(xNode, yNode));
            }
            case SvmParameter.SIGMOID:
                return Math.tanh(param.gamma * dot(xNode, yNode) + param.coef0);
            case SvmParameter.PRECOMPUTED:
                return xNode[(int) (yNode[0].value)].value;
            default:
                return 0;    // java
        }
    }

    private static double calculateRBF(Node[] xNode, Node[] yNode) {
        double sum = 0;
        int xLength = xNode.length;
        int yLength = yNode.length;
        int i = 0;
        int j = 0;
        while (i < xLength && j < yLength) {
            if (xNode[i].index == yNode[j].index) {
                double diff = xNode[i++].value - yNode[j++].value;
                sum += diff * diff;
            } else if (xNode[i].index > yNode[j].index) {
                sum += yNode[j].value * yNode[j].value;
                ++j;
            } else {
                sum += xNode[i].value * xNode[i].value;
                ++i;
            }
        }

        while (i < xLength) {
            sum += xNode[i].value * xNode[i].value;
            ++i;
        }

        while (j < yLength) {
            sum += yNode[j].value * yNode[j].value;
            ++j;
        }
        return sum;
    }
}
