package no.uib.svm.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DnaToNumeric {
    private final CsvSVMLight csvSVMLight;
    static final String INDEX_GENETIC = "ACGT";
    static final List<String>
            INDEX_GENETICTHREE = new ArrayList<String>(),
            INDEX_GENETICTWO = Arrays.asList("AA", "TT", "GG", "CC", "AT", "AG", "AC", "TA", "GA", "CA", "TG", "TC", "CT", "GT", "GC", "CG"),
            INDEX_GENETICFOUR = new ArrayList<String>();

    public DnaToNumeric(CsvSVMLight csvSVMLight) {
        this.csvSVMLight = csvSVMLight;
    }

    static void generateUniqueGeneticListOfThree() {
        for (int i = 0; i < INDEX_GENETIC.length(); i++) {
            for (int j = 0; j < INDEX_GENETIC.length(); j++) {
                for (int k = 0; k < INDEX_GENETIC.length(); k++) {
                    StringBuilder sb = new StringBuilder();
                    INDEX_GENETICTHREE.add(
                            sb
                                    .append(INDEX_GENETIC.charAt(i))
                                    .append(INDEX_GENETIC.charAt(j))
                                    .append(INDEX_GENETIC.charAt(k))
                                    .toString());
                }
            }
        }

        printList(INDEX_GENETICTHREE);
    }

    public static int numericValueOfSubstring(String str) {
        int length = str.length();
        if (length == 4) {
            return INDEX_GENETICFOUR.indexOf(str);
        } else if (length == 3) {
            return INDEX_GENETICTHREE.indexOf(str);
        } else { // length == 2
            return INDEX_GENETICTWO.indexOf(str);
        }
    }


    public static void generateUniqueGeneticListOfFour() {
        for (int i = 0; i < DnaToNumeric.INDEX_GENETIC.length(); i++) {
            for (int j = 0; j < DnaToNumeric.INDEX_GENETIC.length(); j++) {
                for (int k = 0; k < DnaToNumeric.INDEX_GENETIC.length(); k++) {
                    for (int l = 0; l < DnaToNumeric.INDEX_GENETIC.length(); l++) {
                        StringBuilder sb = new StringBuilder();
                        DnaToNumeric.INDEX_GENETICFOUR.add(sb.append(DnaToNumeric.INDEX_GENETIC.charAt(i)).append(DnaToNumeric.INDEX_GENETIC.charAt(j)).append(DnaToNumeric.INDEX_GENETIC.charAt(k)).append(DnaToNumeric.INDEX_GENETIC.charAt(l)).toString());
                    }
                }
            }
        }
    }

    public static int numericValueOfSingle(char c) {
        return INDEX_GENETIC.indexOf(c) + 1;
    }


    static void printList(List<String> list) {
        System.out.println("printing list with " + list.size() + " elements");
        for(String s : list) {
            System.out.println(s);
        }
    }
}