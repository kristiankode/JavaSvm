package no.uib.svm.core;

/**
 * This class represents a single feature of one data instance, i.e the a value for x on the x-axis.
 */
public class Feature {

    private final int index;
    private final Double value;

    public Feature(int index, Double value){
        this.index = index;
        this.value = value;
    }
}
