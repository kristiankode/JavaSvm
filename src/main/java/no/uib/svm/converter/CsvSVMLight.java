package no.uib.svm.converter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus on 21.08.2015.
 * Converter supports input file with format:
 * NtSequence,LibraryType
 * <p>
 * Output file format:
 * LibraryType : NtSequence
 * LibraryType : 0 eller 1
 * <p>
 * 0 :  Bacteria
 * 1 :  Fungi
 * Should also support 2 : Virus
 */
public class CsvSVMLight {
    static final String INDEX_GENETIC = "ACGT";

    private static final String
            KRISTIAN_GENDATA_TRAINING = "/Users/kristianhestetun/Downloads/Genbank_sample.csv",
            KRISTIAN_GENDATA_VALIDATION = "/Users/kristianhestetun/Utvikling/MachineLearning/jLibSvm/Genbank_different.csv",
            KRISTIAN_TRAINING_SET = "dna.training",
            KRISTIAN_VALIDATION_SET = "dna.validation";
    private static final String
            GENDATA_File = "D://Output/GenBank/Genbank_sample.csv",
            GENDATA_TRAINING = "D://Output/Training/svm_train.t",
            GENDATA_TEST_FILE = "D://Output/GenBank/Genbank_different.csv",
            GENDATA_TEST_DATA = "D://Output/Training/svm_test";
    public static final int NUMBER_OF_ATTRIBUTES = 600;
    public static final String CHARSET_NAME = "Unicode";
    public static final String KEY_VALUE_SEPARATOR = ":";
    public static final String ATTR_SEPARATOR = " ";


    public static void main(String[] args) {
        CsvSVMLight csvSVMLight = new CsvSVMLight();
        csvSVMLight.run();
    }

    /**
     * Setting up input_file_destination
     */
    private void run() {
        /** Reade_file param 1 and creating_training_data param 2 **/
        writeToFile(KRISTIAN_GENDATA_TRAINING, KRISTIAN_TRAINING_SET);
        writeToFile(KRISTIAN_GENDATA_VALIDATION, KRISTIAN_VALIDATION_SET);
    }

    /**
     * Creating training data and test data
     *
     * @param inputFilePath
     * @param outputFilePath
     */
    private void writeToFile(String inputFilePath, String outputFilePath) {

        /**File output**/
        File outputFile = new File(outputFilePath);
        try {
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }

            /** File input **/
            Reader reader = new InputStreamReader(new FileInputStream(inputFilePath), CHARSET_NAME);
            BufferedReader buffered_reader = new BufferedReader(reader);

            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            BufferedWriter bufferedWriter =
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath), CHARSET_NAME));

            String inputLine = "";
            /** Looping through the content of a file **/
            while ((inputLine = buffered_reader.readLine()) != null) {
                inputLine = inputLine.replace(" ", "");
                String[] inputData = inputLine.split(",");
                if (inputData.length > 1) {
                    String ntSequence = inputData[0];
                    String libraryType = inputData[1];
                    convertLine(ntSequence.trim(), libraryType.trim(), bufferedWriter);
                }
            }
            bufferedWriter.flush();
            /** close bytestream **/
            buffered_reader.close();
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void convertLine(String ntSequence, String libraryType, BufferedWriter bufferedWriter)
            throws IOException {
        int classification;
        if (libraryType.equalsIgnoreCase("Bacteria")) {
            classification = 1;
        } else if (libraryType.equalsIgnoreCase("Fungi")) {
            classification = 2;
        } else {
            classification = -1;
        }
        List<String> dna_vector = createDnaVector(ntSequence);
        StringBuilder attributes = new StringBuilder();
        for (String dna : dna_vector) {
            attributes.append(dna).append(" ");
        }
        bufferedWriter.write(classification + " " + attributes.toString() + "\n");
    }

    private List<String> createDnaVector(String ntSequence) {

        List<String> dnaVector = new ArrayList<>(ntSequence.length());
        for (int i = 0; i < NUMBER_OF_ATTRIBUTES; i++) {
            dnaVector.add(buildAttributeString(ntSequence, i));
        }
        return dnaVector;
    }

    private String buildAttributeString(String ntSequence, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append(i + 1).append(KEY_VALUE_SEPARATOR);

        if (i >= ntSequence.length()) {
            sb.append(0);
        } else {
            sb.append(findNumericValue(ntSequence.charAt(i)));
        }
        sb.append(ATTR_SEPARATOR);
        return sb.toString();
    }

    private int findNumericValue(char c) {
        return INDEX_GENETIC.indexOf(c) + 1;
    }

}
