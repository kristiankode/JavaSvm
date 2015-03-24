package no.uib.svm.core;

/**
 * This class represents a single feature of a data point, i.e the x-axis.
 */
public class Feature {

    private final Double value;
    private final int index;

    public Feature(int index, Double value){
        this.index = index;
        this.value = value;
    }
}
