package no.uib.svm.libsvm.api.options;

import no.uib.svm.libsvm.api.options.testing.SvmTester;
import no.uib.svm.libsvm.api.options.testing.TestingWrapper;
import no.uib.svm.libsvm.api.options.training.SvmTrainer;
import no.uib.svm.libsvm.api.options.training.TrainingWrapper;

/**
 * @author kristian
 *         Created 30.05.15.
 */
public class SvmFactoryImpl {

    public static SvmTester getTester() {
        return new TestingWrapper();
    }

    public static SvmTrainer getTrainer() {
        return new TrainingWrapper();
    }
}
