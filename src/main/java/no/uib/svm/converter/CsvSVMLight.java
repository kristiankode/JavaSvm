package no.uib.svm.converter;

import com.sun.media.sound.InvalidDataException;
import no.uib.svm.libsvm.core.settings.Settings;
import no.uib.svm.libsvm.core.settings.SettingsFactory;

import java.io.*;
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
    private static final Settings settings = SettingsFactory.getActiveSettings();
    public static final String
            KEY_VALUE_SEPARATOR = ":",
            ATTR_SEPARATOR = " ",
            LINE_SEPARATOR = "\n";

    public static void main(String[] args) {
        CsvSVMLight csvSVMLight = new CsvSVMLight();
        csvSVMLight.run();
    }

    /**
     * Setting up input_file_destination
     */
    private void run() {

        DnaToNumeric.generateUniqueGeneticListOfFour();
        DnaToNumeric.generateUniqueGeneticListOfThree();

        /** Reade_file param 1 and creating_training_data param 2 **/
        writeToFile(KRISTIAN_GENDATA_TRAINING, KRISTIAN_TRAINING_SET);

        System.out.println("File saved as " + KRISTIAN_TRAINING_SET);
    }

    /**
     * Creating training data and test data
     *
     * @param inputFilePath
     * @param outputFilePath
     */
    private void writeToFile(String inputFilePath, String outputFilePath) {
        try {
            /** File input **/
            Reader reader = new InputStreamReader(
                    new FileInputStream(inputFilePath), settings.getInputCharset());
            BufferedReader buffered_reader = new BufferedReader(reader);

            BufferedWriter bufferedWriter =
                    new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(outputFilePath), settings.getOutputCharset()));

            String inputLine = "";
            /** Looping through the content of a file **/
            while ((inputLine = buffered_reader.readLine()) != null) {
                inputLine = inputLine.replace(" ", "");
                String[] inputData = inputLine.split(",");
                if (inputData.length > 1) {
                    String ntSequence = inputData[0];
                    String libraryType = inputData[1];
                    writeLine(ntSequence.trim(), libraryType.trim(), bufferedWriter);
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


    private void writeLine(String ntSequence, String libraryType, BufferedWriter bufferedWriter)
            throws IOException {
        String classification;
        if (libraryType.equalsIgnoreCase("Bacteria")) {
            classification = "+1";
        } else if (libraryType.equalsIgnoreCase("Fungi")) {
            classification = "-1";
        } else {
            throw new InvalidDataException("Unknown library type: " + libraryType);
        }

        DnaAttributeBuilder dnaAttributeBuilder = new DnaAttributeBuilder();
        List<String> dnaVector = dnaAttributeBuilder.createDnaVector(ntSequence);
        StringBuilder attributes = new StringBuilder();
        for (String dna : dnaVector) {
            attributes.append(dna);
        }
        bufferedWriter.write(classification + ATTR_SEPARATOR + attributes.toString() + LINE_SEPARATOR);
    }
}
