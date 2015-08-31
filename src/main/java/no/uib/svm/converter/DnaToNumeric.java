package no.uib.svm.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DnaToNumeric {
    static final Logger log = LoggerFactory.getLogger(DnaToNumeric.class);
    static final String INDEX_GENETIC = "TGCARNMKYSWDH";
    private HashMap<String, Integer> dnaValues;

    private final int windowSize;

    public DnaToNumeric(int windowSize) {
        this.windowSize = windowSize;
        generateAppropriateValues();
    }

    private void generateAppropriateValues() {
        switch (windowSize) {
            case 4:
                dnaValues = generateUniqueGeneticListOfFour();
                break;
            case 3:
                dnaValues = generateUniqueGeneticListOfThree();
                break;
            case 2:
                dnaValues = generateUniqueGeneticListOfTwo();
                break;
            default:
                // do nothing
        }
    }

    public int valueOf(String str) {
        int length = str.length();

        if (length == 1) {
            return INDEX_GENETIC.indexOf(str.charAt(0));
        } else if (length > 1) {
            if (dnaValues.containsKey(str)) {
                return dnaValues.get(str);
            } else {
                return -1;
            }
        } else {
            return 0;
        }
    }

    HashMap<String, Integer> generateUniqueGeneticListOfThree() {
        HashMap<String, Integer> tripleValues = new HashMap<>();
        int valueOfSequence = 0;

        for (int i = 0; i < INDEX_GENETIC.length(); i++) {
            for (int j = 0; j < INDEX_GENETIC.length(); j++) {
                for (int k = 0; k < INDEX_GENETIC.length(); k++) {
                    valueOfSequence++;
                    StringBuilder sb = new StringBuilder();
                    String sequence = sb
                            .append(INDEX_GENETIC.charAt(i))
                            .append(INDEX_GENETIC.charAt(j))
                            .append(INDEX_GENETIC.charAt(k))
                            .toString();

                    tripleValues.put(sequence, valueOfSequence);
                }
            }
        }

        log.debug("Generated value map for windowsize = 3, map size = {}", tripleValues.keySet().size());

        return tripleValues;
    }

    HashMap<String, Integer> generateUniqueGeneticListOfFour() {
        HashMap<String, Integer> quadValues = new HashMap<>();
        int valueOfSequence = 0;

        for (int i = 0; i < DnaToNumeric.INDEX_GENETIC.length(); i++) {
            for (int j = 0; j < DnaToNumeric.INDEX_GENETIC.length(); j++) {
                for (int k = 0; k < DnaToNumeric.INDEX_GENETIC.length(); k++) {
                    for (int l = 0; l < DnaToNumeric.INDEX_GENETIC.length(); l++) {
                        valueOfSequence++;
                        StringBuilder sb = new StringBuilder();
                        String seq = sb
                                .append(INDEX_GENETIC.charAt(i))
                                .append(INDEX_GENETIC.charAt(j))
                                .append(INDEX_GENETIC.charAt(k))
                                .append(INDEX_GENETIC.charAt(l))
                                .toString();
                        quadValues.put(seq, valueOfSequence);
                    }
                }
            }
        }
        return quadValues;
    }

    HashMap<String, Integer> generateUniqueGeneticListOfTwo() {
        HashMap<String, Integer> doubleValues = new HashMap<>();
        int valueOfSequence = 0;

        for (int i = 0; i < INDEX_GENETIC.length(); i++) {
            for (int j = 0; j < INDEX_GENETIC.length(); j++) {
                valueOfSequence++;
                StringBuilder sb = new StringBuilder();
                String seq = sb
                        .append(INDEX_GENETIC.charAt(i))
                        .append(INDEX_GENETIC.charAt(j)).toString();
                doubleValues.put(seq, valueOfSequence);
            }
        }
        return doubleValues;
    }
}