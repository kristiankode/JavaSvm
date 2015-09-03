package no.uib.svm.converter;

import no.uib.svm.converter.Genbank.Genome;
import no.uib.svm.converter.read.BufferedCsvReader;
import no.uib.svm.converter.read.CsvReader;
import no.uib.svm.libsvm.core.settings.Settings;
import no.uib.svm.libsvm.core.settings.SettingsFactory;
import no.uib.svm.output.OutputWriter;
import no.uib.svm.output.FileWriter;
import no.uib.svm.output.WriterFactory;

import java.io.*;

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
            KRISTIAN_GENDATA_VALIDATION = "/Users/kristianhestetun/Utvikling/MachineLearning/jLibSvm/Genbank_different.csv";

    private static final Settings settings = SettingsFactory.getActiveSettings();

    public static final String
            BLANK_SPACE_BABY = " ",
            COMMA_CHAMELEON = ",",
            EMPTY_STRING = "",
            KEY_VALUE_SEPARATOR = ":",
            TARGET = " ",
            ATTR_SEPARATOR = " ",
            LINE_SEPARATOR = "\n";

    private SubstringAsFeatureWriter substringAsFeatureWriter;
    private OutputWriter outputWriter;

    public CsvSVMLight()
            throws FileNotFoundException, UnsupportedEncodingException {
        outputWriter = WriterFactory.getWriter();
        substringAsFeatureWriter = new SubstringAsFeatureWriter(outputWriter);
    }

    public static void main(String[] args)
            throws FileNotFoundException, UnsupportedEncodingException {
        CsvSVMLight csvSVMLight = new CsvSVMLight();
        csvSVMLight.run();
    }

    /**
     * Setting up input_file_destination
     */
    private void run() {

        /** Reade_file param 1 and creating_training_data param 2 **/
        writeToFile(KRISTIAN_GENDATA_TRAINING);
    }

    /**
     * Creating training data and test data
     *
     * @param inputFilePath
     */
    private void writeToFile(String inputFilePath) {
        try {
            /** File input **/
            CsvReader csvReader = new BufferedCsvReader(inputFilePath);

            String[] rowData = null;
            /** Looping through the content of a file **/
            while ((rowData = csvReader.readNextRow()) != null) {
                Genome genome = new Genome(rowData[0], rowData[1]);
                substringAsFeatureWriter.writeLine(genome);
            }
            /** close bytestream **/
            csvReader.close();
            outputWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
