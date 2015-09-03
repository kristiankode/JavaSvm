package no.uib.svm.converter.write.attributes.sequences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

public class SequenceGeneratorImpl implements SequenceGenerator {
    static final Logger log = LoggerFactory.getLogger(SequenceGeneratorImpl.class);
    static final String DNA_CHARACTERS = "ACGTRYKMSWBDHVN";
    static HashSet<String> combos = null;

    @Override
    public HashSet<String> getAllPossibleCombinationsOfLength(int length) {

        if (!isSetGenerated()) {
            generateSet(length);
        }

        return combos;
    }

    private boolean isSetGenerated() {
        return combos != null;
    }

    private void generateSet(int length) {
        log.debug("Generating set with length {}", length);
        combos = new HashSet<>(DNA_CHARACTERS.length());

        // first iteration: insert all letters in set
        insertAllLetters(combos);

        // loop through set and append letters
        for (int i = 1; i < length; i++) {
            combos = extendByOne(combos);
        }
    }

    /**
     * Creates a set with all characters (length = 1)
     *
     * @param set
     * @return
     */
    HashSet<String> insertAllLetters(HashSet<String> set) {
        for (int letterIndex = 0; letterIndex < DNA_CHARACTERS.length(); letterIndex++) {
            set.add(String.valueOf(DNA_CHARACTERS.charAt(letterIndex)));
        }

        return set;
    }

    /**
     * Extends an existing set, new character lengths becomes old + 1.
     *
     * @param oldSet
     * @return
     */
    HashSet<String> extendByOne(HashSet<String> oldSet) {
        HashSet<String> newSet = new HashSet<>();

        for (String currentValue : oldSet) {
            for (int letterIndex = 0; letterIndex < DNA_CHARACTERS.length(); letterIndex++) {
                newSet.add(currentValue + DNA_CHARACTERS.charAt(letterIndex));
            }
        }

        return newSet;
    }
}