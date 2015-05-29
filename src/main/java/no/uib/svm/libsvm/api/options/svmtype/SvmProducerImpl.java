package no.uib.svm.libsvm.api.options.svmtype;

import java.util.Arrays;
import java.util.List;

/**
 * @author kristian
 *         Created 29.05.15.
 */
public class SvmProducerImpl implements SvmProducer {

    List<SvmType> availableTypes = Arrays.asList(
            CSvc.defaultCsvc,
            EpsilonSvr.defaultEpsilonSvr,
            NuSvc.defaultNuSvc,
            NuSvr.defaultNuSvr,
            OneClassSvm.defaultOneClass);

    public SvmType getCSVC() {
        return CSvc.defaultCsvc;
    }

    public SvmType getOneClassSvm() {
        return OneClassSvm.defaultOneClass;
    }

    public SvmType getNuSvr() {
        return NuSvr.defaultNuSvr;
    }

    public SvmType getNuSvc() {
        return NuSvc.defaultNuSvc;
    }

    public SvmType getEpsilonSvr() {
        return EpsilonSvr.defaultEpsilonSvr;
    }

    @Override
    public SvmType getDefault() {
        return getCSVC();
    }

    @Override
    public List<SvmType> getAvailableTypes() {
        return availableTypes;
    }
}
