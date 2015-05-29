package no.uib.svm.libsvm.api.options.kernel;

import java.util.Arrays;
import java.util.List;

/**
 * @author kristian
 *         Created 29.05.15.
 */
public class KernelFactoryImpl implements KernelFactory {

    private static List<Kernel> availableKernels = Arrays.asList(
            PolynomialKernel.defaultPolynomialKernel,
            RadialBasisKernel.defaultRadialBasisKernel,
            new PrecomputedKernel(),
            new SigmoidKernel(),
            new LinearKernel());

    @Override
    public List<Kernel> getAvailableKernels() {
        return availableKernels;
    }

    @Override
    public Kernel getDefault(){
        return new LinearKernel();
    }
}
