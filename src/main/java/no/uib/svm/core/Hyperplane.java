package no.uib.svm.core;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.Vector;

/**
 * Class representing the hyperplane that divides the data points.
 */
public class Hyperplane {

    // input fields
    private final Vector<Real> weightVector;
    private final Vector<Real> inputVector;
    private final Real bias;

    // output
    Real value;

    @SuppressWarnings("unchecked")
    public Hyperplane(Vector<Real> inputVector, Vector<Real> weightVector, Real bias) {
        this.inputVector = inputVector;
        this.weightVector = weightVector;
        this.bias = bias;

        Real dotProduct = inputVector.times(weightVector);
        this.value = dotProduct.plus(bias);
    }

    public Vector<Real> getWeightVector() {
        return weightVector;
    }

    public Vector<Real> getInputVector() {
        return inputVector;
    }

    public Real getBias() {
        return bias;
    }

    public Real getValue() {
        return value;
    }
}
