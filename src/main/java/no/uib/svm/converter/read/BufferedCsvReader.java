package no.uib.svm.converter.read;

import no.uib.svm.libsvm.core.settings.Settings;
import no.uib.svm.libsvm.core.settings.SettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author kristian
 *         Created 03.09.15.
 */
public class BufferedCsvReader implements CsvReader {
    private final static Logger log = LoggerFactory.getLogger(BufferedCsvReader.class);

    final String inputFilePath;
    final Settings settings = SettingsFactory.getActiveSettings();
    final BufferedReader bufferedReader;

    public static final String
            BLANK_SPACE_BABY = " ",
            COMMA_CHAMELEON = ",",
            EMPTY_STRING = "";

    public BufferedCsvReader(String inputFilePath)
            throws FileNotFoundException, UnsupportedEncodingException {
        this.inputFilePath = inputFilePath;

        Reader reader = new InputStreamReader(
                new FileInputStream(inputFilePath), settings.getCsvCharset());
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
