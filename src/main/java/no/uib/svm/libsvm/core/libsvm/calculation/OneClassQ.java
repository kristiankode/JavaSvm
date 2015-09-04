package no.uib.svm.libsvm.core.libsvm.calculation;

import no.uib.svm.libsvm.core.libsvm.Problem;
import no.uib.svm.libsvm.core.libsvm.SvmParameter;

class OneClassQ extends Kernel {
    private final Cache cache;
    private final double[] QD;

    OneClassQ(Problem prob, SvmParameter param) {
        super(prob.l, prob.x, param);
        cache = new Cache(prob.l, (long) (param.cache_size * (1 << 20)));
        QD = new double[prob.l];
        for (int i = 0; i < prob.l; i++)
            QD[i] = kernelFunction(i, i);
    }

    float[] getQ(int i, int len) {
        float[][] data = new float[1][];
        int start, j;
        if ((start = cache.getData(i, data, len)) < len) {
            for (j = start; j < len; j++)
                data[0][j] = (float) kernelFunction(i, j);
        }
        return data[0];
    }

    double[] getQD() {
        return QD;
    }

    void swapIndex(int i, int j) {
        cache.swapIndex(i, j);
        super.swapIndex(i, j);
        double _ = QD[i];
        QD[i] = QD[j];
        QD[j] = _;
    }
}

