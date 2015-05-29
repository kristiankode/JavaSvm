//
// svm_model
//
package no.uib.svm.libsvm.core.libsvm;
public class SvmModel implements java.io.Serializable, Model {
	public SvmParameter param;	// parameter
	public int nr_class;		// number of classes, = 2 in regression/one class svm
	public int l;			// total #SV
	public Node[][] SV;	// SVs (SV[l])
	public double[][] sv_coef;	// coefficients for SVs in decision functions (sv_coef[k-1][l])
	public double[] rho;		// constants in decision functions (rho[k*(k-1)/2])
	public double[] probA;         // pariwise probability information
	public double[] probB;
	public int[] sv_indices;       // sv_indices[0,...,nSV-1] are values in [1,...,num_traning_data] to indicate SVs in the training set

	// for classification only
	public int[] label;		// label of each class (label[k])
	public int[] nSV;		// number of SVs for each class (nSV[k])
				// nSV[0] + nSV[1] + ... + nSV[k-1] = l

	@Override
	public SvmParameter getParam() {
		return param;
	}

	@Override
	public int getNr_class() {
		return nr_class;
	}

	@Override
	public int getL() {
		return l;
	}

	@Override
	public Node[][] getSV() {
		return SV;
	}

	@Override
	public double[][] getSv_coef() {
		return sv_coef;
	}

	@Override
	public double[] getRho() {
		return rho;
	}

	@Override
	public double[] getProbA() {
		return probA;
	}

	@Override
	public double[] getProbB() {
		return probB;
	}

	public int[] getSv_indices() {
		return sv_indices;
	}

	@Override
	public int[] getLabel() {
		return label;
	}

	@Override
	public int[] getnSV() {
		return nSV;
	}
}
