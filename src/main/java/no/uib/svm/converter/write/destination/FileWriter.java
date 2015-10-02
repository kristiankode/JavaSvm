package no.uib.svm.converter.write.destination;

import no.uib.svm.libsvm.core.settings.Settings;
import no.uib.svm.libsvm.core.settings.SettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static java.lang.System.currentTimeMillis;

/**
 * Writes output to file.
 */
public class FileWriter {

    final static Logger log = LoggerFactory.getLogger(FileWriter.class);
    final BufferedWriter bufferedWriter;
    final Settings settings = SettingsFactory.getActiveSettings();
    final String filePath;
    int counter = 0;
    long startupTime = currentTimeMillis();

    final static String
            CONVERT_MSG = "Converted {} dna sequences in {} millis";


    public FileWriter(String filePath) throws FileNotFoundException, UnsupportedEncodingException {
        this.filePath = filePath;
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), settings.getSvmCharset()));
    }

    public void write(String data) {
        try {
            bufferedWriter.write(data);
            counter++;
            printInfo();
        } catch (IOException e) {
            log.error("Error while trying to write using reader {}, data was {}", this, data);
        }
    }

    public void close() {
        try {
            bufferedWriter.close();
            log.info("File saved as {}", filePath);
        } catch (IOException e) {
            log.error("Unable to close writer {}", bufferedWriter);
        }
    }

    void printInfo() {
        if (counter % 500 == 0) {
            log.debug(
                    CONVERT_MSG,
                    counter,
                    currentTimeMillis() - startupTime);
        }
    }
}
