package no.uib.svm.core;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.Vector;

/**
 * Class representing the hyperplane that divides the data points.
 */
public class Hyperplane {

    private final Vector<Real> weightVector;
    private final Real bias;

    /**
     * Initializes a Hyperplane function with a weight vector and a bias
     * @param weightVector
     * @param bias
     */
    public Hyperplane(Vector<Real> weightVector, Real bias) {
        this.weightVector = weightVector;
        this.bias = bias;
    }


    /**
     * Calculates the value for a specific input vector
     * @param inputVector
     * @return
     */
    public Real getValueForInput(Vector<Real> inputVector){
        Real value = inputVector.times(weightVector);
        value.plus(bias);

        return value;

    }

    public Vector<Real> getWeightVector() {
        return weightVector;
    }

    public Real getBias() {
        return bias;
    }

}
