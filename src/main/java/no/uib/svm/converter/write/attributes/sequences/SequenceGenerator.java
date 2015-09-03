package no.uib.svm.converter.write.attributes.sequences;

import java.util.HashSet;

/**
 * @author kristian
 *         Created 03.09.15.
 */
public interface SequenceGenerator {

    HashSet<String> getAllPossibleCombinationsOfLength(int length);
}
