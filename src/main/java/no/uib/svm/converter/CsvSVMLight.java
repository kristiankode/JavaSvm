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
            KRISTIAN_GENDATA_TRAINING = "/Users/kristianhestetun/Utvikling/MachineLearning/JavaSVM/200k.csv",
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
            BACTERIA_LIBRARY = "Bacteria",
            FUNGI_LIBRARY = "Fungi",
            FUNGI_CODE = "-1",
            BACTERIA_CODE = "+1";

    final DnaAttributeBuilder attributeBuilder = new DnaAttributeBuilder();
    public static final String
            BLANK_SPACE_BABY = " ",
            COMMA_CHAMELEON = ",",
            EMPTY_STRING = "",
            KEY_VALUE_SEPARATOR = ":",
            TARGET = " ",
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
                    new FileInputStream(inputFilePath), settings.getCsvCharset());
            BufferedReader buffered_reader = new BufferedReader(reader);

            BufferedWriter bufferedWriter =
                    new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(outputFilePath), settings.getSvmCharset()));

            String inputLine = "";
            /** Looping through the content of a file **/
            while ((inputLine = buffered_reader.readLine()) != null) {
                inputLine = inputLine.replace(BLANK_SPACE_BABY, EMPTY_STRING);
                String[] inputData = inputLine.split(COMMA_CHAMELEON);
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
        if (libraryType.equalsIgnoreCase(BACTERIA_LIBRARY)) {
            classification = BACTERIA_CODE;
        } else if (libraryType.equalsIgnoreCase(FUNGI_LIBRARY)) {
            classification = FUNGI_CODE;
        } else {
            throw new InvalidDataException("Unknown library type: " + libraryType);
        }

        List<String> dnaVector = attributeBuilder.createDnaVector(ntSequence);
        StringBuilder attributes = new StringBuilder();
        for (String dna : dnaVector) {
            attributes.append(dna);
        }
        bufferedWriter.write(classification + ATTR_SEPARATOR + attributes.toString() + LINE_SEPARATOR);
    }
}
