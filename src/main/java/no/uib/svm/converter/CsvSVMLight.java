package no.uib.svm.converter;

import no.uib.svm.converter.domain.Genome;
import no.uib.svm.converter.read.BufferedCsvReader;
import no.uib.svm.converter.read.CsvReader;
import no.uib.svm.converter.write.attributes.AttributeBuilder;
import no.uib.svm.converter.write.attributes.SubstringAsFeature;
import no.uib.svm.converter.write.attributes.SubstringOccurenceAsFeature;
import no.uib.svm.converter.write.destination.OutputWriter;
import no.uib.svm.converter.write.destination.WriterFactory;
import no.uib.svm.converter.write.row.RowBuilder;
import no.uib.svm.libsvm.core.settings.Settings;
import no.uib.svm.libsvm.core.settings.SettingsFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class CsvSVMLight {

    private static final String
            INPUT_FILE = "/Users/kristianhestetun/Utvikling/MachineLearning/JavaSVM/200k.csv";

    private OutputWriter outputWriter = WriterFactory.getWriter();
    private AttributeBuilder attributeBuilder = new SubstringAsFeature();

    public CsvSVMLight()
            throws FileNotFoundException, UnsupportedEncodingException {
    }

    public static void main(String[] args)
            throws FileNotFoundException, UnsupportedEncodingException {
        CsvSVMLight csvSVMLight = new CsvSVMLight();
        csvSVMLight.run();
    }

    private void run() {
        convertToSvmFormat(INPUT_FILE);
    }

    /**
     * Converts a svm-file to SvmLight-format
     *
     * @param inputFilePath Path to csv file
     */
    private void convertToSvmFormat(String inputFilePath) {
        try {
            CsvReader csvReader = new BufferedCsvReader(inputFilePath);
            RowBuilder rowBuilder = new RowBuilder(attributeBuilder);

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
