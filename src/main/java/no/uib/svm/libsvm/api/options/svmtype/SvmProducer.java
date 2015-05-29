package no.uib.svm.libsvm.api.options.svmtype;

import java.util.List;

/**
 * @author kristian
 *         Created 29.05.15.
 */
public interface SvmProducer {
    SvmType getDefault();

    List<SvmType> getAvailableTypes();
}
