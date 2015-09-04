package no.uib.svm.libsvm.core.libsvm.calculation;

import no.uib.svm.libsvm.core.libsvm.Problem;
import no.uib.svm.libsvm.core.libsvm.SvmParameter;

;

//
// Q matrices for various formulations
//
class SvcQ extends Kernel
{
	private final byte[] y;
	private final Cache cache;
	private final double[] QD;

	SvcQ(Problem prob, SvmParameter param, byte[] y_)
	{
		super(prob.l, prob.x, param);
		y = (byte[])y_.clone();
		cache = new Cache(prob.l,(long)(param.cache_size*(1<<20)));
		QD = new double[prob.l];
		for(int i=0;i<prob.l;i++)
			QD[i] = kernelFunction(i, i);
	}

	float[] getQ(int i, int len)
	{
		float[][] data = new float[1][];
		int start, j;
		if((start = cache.getData(i, data, len)) < len)
		{
			for(j=start;j<len;j++)
				data[0][j] = (float)(y[i]*y[j]*kernelFunction(i, j));
		}
		return data[0];
	}

	double[] getQD()
	{
		return QD;
	}

	void swapIndex(int i, int j)
	{
		cache.swapIndex(i,j);
		super.swapIndex(i,j);
		do {byte _=y[i]; y[i]=y[j]; y[j]=_;} while(false);
		do {double _=QD[i]; QD[i]=QD[j]; QD[j]=_;} while(false);
	}
}

