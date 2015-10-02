package no.uib.svm.converter;

import no.uib.svm.converter.domain.Genome;
import no.uib.svm.converter.read.BufferedCsvReader;
import no.uib.svm.converter.read.CsvReader;
import no.uib.svm.converter.write.attributes.AttributeBuilder;
import no.uib.svm.converter.write.attributes.SubstringAsFeature;
import no.uib.svm.converter.write.attributes.SubstringOccurenceAsFeature;
import no.uib.svm.converter.write.destination.FileWriter;
import no.uib.svm.converter.write.row.RowBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Converts a CSV file to SVM-light format.
 */
public class CsvSVMLight {

    private AttributeBuilder attributeBuilder = new SubstringOccurenceAsFeature();

    /**
     * Starts the program with input and output arguments.
     *
     * @param args Path of input file,
     *             path of output file. Note: Use only existing directories, no new folders will be created.
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static void main(String[] args)
            throws FileNotFoundException, UnsupportedEncodingException {
        CsvSVMLight csvSVMLight = new CsvSVMLight();
        String inputFile = args[0];
        String outputFile = args[1];
        csvSVMLight.convertToSvmFormat(inputFile, outputFile);
    }

    /**
     * Converts a svm-file to SvmLight-format
     *
     * @param inputFilePath Path to csv file
     */
    private void convertToSvmFormat(String inputFilePath, String outputPath) {
        try {
            CsvReader csvReader = new BufferedCsvReader(inputFilePath);
            RowBuilder rowBuilder = new RowBuilder(attributeBuilder);
            FileWriter outputWriter = new FileWriter(outputPath);

            String[] rowData;
            while ((rowData = csvReader.readNextRow()) != null) {
                Genome genome = new Genome(rowData[0], rowData[1]);
                outputWriter.write(rowBuilder.buildRow(genome));
            }

            csvReader.close();
            outputWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
