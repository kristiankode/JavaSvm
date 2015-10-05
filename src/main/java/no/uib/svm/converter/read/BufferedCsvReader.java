package no.uib.svm.converter.read;

import no.uib.svm.libsvm.core.settings.SettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Reads a csv file using a buffered reader. Takes input path as argument, produces a reader that can readNextLine().
 */
public class BufferedCsvReader implements CsvReader {
    private final static Logger log = LoggerFactory.getLogger(BufferedCsvReader.class);

    final String inputFilePath;
    final BufferedReader bufferedReader;

    public static final String
            BLANK_SPACE_BABY = " ",
            COMMA_CHAMELEON = ",",
            EMPTY_STRING = "";

    /**
     * Takes input path as argument, produces a reader that can readNextLine().
     * Uses default encoding (from settings).
     *
     * @param inputFilePath The file path of the CSV file.
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public BufferedCsvReader(String inputFilePath)
            throws FileNotFoundException, UnsupportedEncodingException {
        this(inputFilePath, SettingsFactory.getActiveSettings().getCsvCharset());
    }

    /**
     * Takes input path as argument, produces a reader that can readNextLine().
     *
     * @param inputFilePath The file path of the CSV file.
     * @param encoding      Encoding of the file to read.
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public BufferedCsvReader(String inputFilePath, String encoding)
            throws FileNotFoundException, UnsupportedEncodingException {
        this.inputFilePath = inputFilePath;

        Reader reader = new InputStreamReader(
                new FileInputStream(inputFilePath), encoding);
        bufferedReader = new BufferedReader(reader);
    }

    @Override
    public String[] readNextRow() {
        String[] row = null;
        try {
            String line = bufferedReader.readLine();
            if (line != null) {
                line = sanitizeLine(line);
                row = convertToArray(line);
            }
        } catch (IOException e) {
            log.error("Error while trying to read line: {}", e.getMessage());
        }
        return row;
    }

    String[] convertToArray(String line) {
        String[] row = line.split(COMMA_CHAMELEON);
        return row.length > 1 ? row : null;
    }

    String sanitizeLine(String line) {
        return line.replace(BLANK_SPACE_BABY, EMPTY_STRING);
    }

    @Override
    public void close() {
        try {
            bufferedReader.close();
        } catch (IOException e) {
            log.error("Unable to close reader: {}", e.getMessage());
        }
    }
}
