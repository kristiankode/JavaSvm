package no.uib.svm.libsvm.api.options.kernel;

import java.util.List;

/**
 * @author kristian
 *         Created 29.05.15.
 */
public interface KernelFactory {
    List<Kernel> getAvailableKernels();

    Kernel getDefault();
}
