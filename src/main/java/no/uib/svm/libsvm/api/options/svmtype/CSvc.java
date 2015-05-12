package no.uib.svm.libsvm.api.options.svmtype;

import no.uib.svm.libsvm.core.libsvm.SvmParameter;

/**
 * Created by kristianhestetun on 05.05.15.
 */
public class CSvc extends SvmType {

    @Override
    public int getId() {
        return SvmType.C_SVC;
    }

    @Override
    public String getName() {
        return "C-SVC";
    }

    @Override
    public void fillSvmParameter(SvmParameter param) {
        param.C = this.C;
        param.nr_weight = this.nr_weight;
        param.weight_label = this.weight_label;
        param.weight = this.weight;
    }

    private double C;    // for C_SVC, EPSILON_SVR and NU_SVR
    private int nr_weight;        // for C_SVC
    private int[] weight_label;    // for C_SVC
    private double[] weight;        // for C_SVC

    public final static CSvc defaultCsvc = new CSvc(
            DEFAULT_C,DEFAULT_NR_WEIGHT, DEFAULT_WEIGHT_LABEL, DEFAULT_WEIGHT);

    public CSvc(double c, int nr_weight, int[] weight_label, double[] weight) {
        C = c;
        this.nr_weight = nr_weight;
        this.weight_label = weight_label;
        this.weight = weight;
    }

    public double getC() {
        return C;
    }

    public void setC(double c) {
        C = c;
    }

    public int getNr_weight() {
        return nr_weight;
    }

    public void setNr_weight(int nr_weight) {
        this.nr_weight = nr_weight;
    }

    public int[] getWeight_label() {
        return weight_label;
    }

    public void setWeight_label(int[] weight_label) {
        this.weight_label = weight_label;
    }

    public double[] getWeight() {
        return weight;
    }

    public void setWeight(double[] weight) {
        this.weight = weight;
    }
}
