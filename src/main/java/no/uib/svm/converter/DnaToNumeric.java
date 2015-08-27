package no.uib.svm.converter;

import no.uib.svm.libsvm.core.settings.SettingsFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DnaToNumeric {
    static final String INDEX_GENETIC = "TGCARNMKYSWDH";
    private List<String> dnaValues;

    public DnaToNumeric() {
        generateAppropriateValues();
    }

    private void generateAppropriateValues() {
        switch (SettingsFactory.getActiveSettings().getWindowSize()) {
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

        if(length > 1) {
            return dnaValues.indexOf(str);
        } else if (length == 1){
            return INDEX_GENETIC.indexOf(str.charAt(0));
        } else {
            return 0;
        }
    }

    List<String> generateUniqueGeneticListOfThree() {
        List<String> tripleValues = new ArrayList<>();
        for (int i = 0; i < INDEX_GENETIC.length(); i++) {
            for (int j = 0; j < INDEX_GENETIC.length(); j++) {
                for (int k = 0; k < INDEX_GENETIC.length(); k++) {
                    StringBuilder sb = new StringBuilder();
                    tripleValues.add(
                            sb
                                    .append(INDEX_GENETIC.charAt(i))
                                    .append(INDEX_GENETIC.charAt(j))
                                    .append(INDEX_GENETIC.charAt(k))
                                    .toString());
                }
            }
        }

        printList(tripleValues);
        return tripleValues;
    }

    List<String> generateUniqueGeneticListOfFour() {
        List<String> quadValues = new ArrayList<>();
        for (int i = 0; i < DnaToNumeric.INDEX_GENETIC.length(); i++) {
            for (int j = 0; j < DnaToNumeric.INDEX_GENETIC.length(); j++) {
                for (int k = 0; k < DnaToNumeric.INDEX_GENETIC.length(); k++) {
                    for (int l = 0; l < DnaToNumeric.INDEX_GENETIC.length(); l++) {
                        StringBuilder sb = new StringBuilder();
                        quadValues.add(sb.append(
                                INDEX_GENETIC.charAt(i))
                                .append(INDEX_GENETIC.charAt(j))
                                .append(INDEX_GENETIC.charAt(k))
                                .append(INDEX_GENETIC.charAt(l))
                                .toString());
                    }
                }
            }
        }
        return quadValues;
    }

    static void printList(List<String> list) {
        System.out.println("printing list with " + list.size() + " elements");
        list.forEach(System.out::println);
    }

    List<String> generateUniqueGeneticListOfTwo() {
        List<String> doubleValues = new ArrayList<>();
        for (int i = 0; i < INDEX_GENETIC.length(); i++) {
            for (int j = 0; j < INDEX_GENETIC.length(); j++) {
                StringBuilder sb = new StringBuilder();
                doubleValues.add(sb.append(INDEX_GENETIC.charAt(i))
                        .append(INDEX_GENETIC.charAt(j)).toString());
            }
        }
        return doubleValues;
    }
}