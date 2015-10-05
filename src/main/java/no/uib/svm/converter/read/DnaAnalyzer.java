package no.uib.svm.converter.read;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.*;

import static java.math.BigDecimal.*;

/**
 * Program for evaluating the Virus.csv-file.
 */
public class DnaAnalyzer {
    private final Logger log = LoggerFactory.getLogger(DnaAnalyzer.class);
    private final static String virusFileEncoding = "UTF-8";

    int
            specieIndex = 1,
            subspecieIndex = 2;

    private CsvReader reader;

    /**
     * Analyses the file, prints result to console.
     *
     * @param args Path to csv file.
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static void main(String... args)
            throws FileNotFoundException, UnsupportedEncodingException {
        String filePath = args[0];

        AnalysisResult result = new DnaAnalyzer(filePath).analyze();
        result.print();
    }

    public DnaAnalyzer(String filePath) throws FileNotFoundException, UnsupportedEncodingException {
        reader = new BufferedCsvReader(filePath, virusFileEncoding);
        log.debug("Found file at {}, reading with encoding {}", filePath, virusFileEncoding);
    }

    private AnalysisResult analyze() {
        DnaCounter counter = new DnaCounter();

        String row[];
        log.debug("Analyzing file");
        while ((row = reader.readNextRow()) != null) {
            String
                    specie = row[specieIndex],
                    subspecie = row[subspecieIndex],
                    ntSequence = row[row.length - 1];

            counter.countSpecie(specie + ": " + subspecie);
            counter.countRow();
            counter.countSequenceLength(ntSequence.length());
        }

        return counter.getResult();
    }

    /**
     * Class keeping track of the numbers.
     */
    private class DnaCounter {
        HashMap<String, Integer> specieCount = new HashMap<>();
        List<Integer> sequenceLengths = new ArrayList<>();
        int rows = 0;

        int minSeqLength = Integer.MAX_VALUE, maxSeqLength = 0;
        private int highestSpecieCount;
        private String mostCommonSpecie;

        public void countRow() {
            rows++;
        }

        public void countSequenceLength(int length) {
            sequenceLengths.add(length);

            if (length <= minSeqLength) {
                minSeqLength = length;
            }
            if (length >= maxSeqLength) {
                maxSeqLength = length;
            }
        }

        private Double calcAvgSeqLength() {
            Long sum = 0l;
            int numberOfSequences = sequenceLengths.size();

            if (numberOfSequences == 0) {
                return 0.0; // avoid division by zero
            }

            for (Integer length : sequenceLengths) {
                sum += length;
            }

            return valueOf(sum).divide(valueOf(numberOfSequences), 10, ROUND_HALF_UP).doubleValue();
        }

        private void calculateMostCommon() {
            highestSpecieCount = 0;
            mostCommonSpecie = "";
            for (String specie : specieCount.keySet()) {
                int count = specieCount.get(specie);
                if (count >= highestSpecieCount) {
                    highestSpecieCount = count;
                    mostCommonSpecie = specie;
                }
            }
        }

        private Double calculateAvgNumberOfSamples() {
            return valueOf(rows).divide(valueOf(specieCount.size()), 10, ROUND_HALF_UP).doubleValue();
        }

        private Integer calcMeanNumberOfSamples() {
            List<Integer> counts = new ArrayList<>(specieCount.values());
            Collections.sort(counts);

            Integer meanIndex = valueOf(counts.size()).divide(valueOf(2), 4, ROUND_FLOOR).intValue();

            return counts.get(meanIndex);
        }

        public void countSpecie(String specie) {
            int newCount;
            if (specieCount.containsKey(specie)) {
                newCount = specieCount.get(specie) + 1;
            } else {
                newCount = 1;
            }
            specieCount.put(specie, newCount);
        }

        public AnalysisResult getResult() {
            AnalysisResult result = new AnalysisResult();

            calculateMostCommon();

            result.numberOfRows = rows;
            result.numberOfSpecies = specieCount.size();
            result.avgSampleSize = calculateAvgNumberOfSamples();
            result.meanSampleSize = calcMeanNumberOfSamples();
            result.minSequenceLength = minSeqLength;
            result.maxSequenceLength = maxSeqLength;
            result.avgSequenceLength = calcAvgSeqLength();
            result.maxNumberOfOneSpecie = highestSpecieCount;
            result.mostCommonSpecie = mostCommonSpecie;

            return result;
        }
    }

    /**
     * Class representing the results.
     */
    private class AnalysisResult {
        int
                numberOfRows,
                numberOfSpecies,
                minSequenceLength,
                maxSequenceLength,
                maxNumberOfOneSpecie;

        Double avgSequenceLength, avgSampleSize;
        String mostCommonSpecie;
        public Integer meanSampleSize;


        public void print() {
            print("-------------- Analysis result -------------");
            print("Number of rows:                      " + readable(numberOfRows));
            print("Number of species:                   " + readable(numberOfSpecies));
            print("Avg number of samples pr specie:     " + readable(avgSampleSize) +" (mean: " + meanSampleSize + ")");
            print("Most common specie:                  " + mostCommonSpecie + " (" + readable(maxNumberOfOneSpecie) + " samples)");
            print("Minimum seq. length:                 " + readable(minSequenceLength));
            print("Maximum seq. length:                 " + readable(maxSequenceLength));
            print("Average. seq. length:                " + readable(avgSequenceLength));
        }

        private String readable(Number number) {
            return NumberFormat.getNumberInstance(Locale.US).format(number);
        }

        private void print(String s) {
            System.out.println(s);
        }

    }
}
