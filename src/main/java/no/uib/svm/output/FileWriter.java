package no.uib.svm.output;

import no.uib.svm.libsvm.core.settings.Settings;
import no.uib.svm.libsvm.core.settings.SettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author kristian
 *         Created 02.09.15.
 */
public class FileWriter implements OutputWriter {

    private final static String
            KRISTIAN_TRAINING_SET = "dna.training",
            KRISTIAN_VALIDATION_SET = "dna.validation";

    final static Logger log = LoggerFactory.getLogger(FileWriter.class);
    final BufferedWriter bufferedWriter;
    final Settings settings = SettingsFactory.getActiveSettings();
    final String filePath;

    public FileWriter(String filePath) throws FileNotFoundException, UnsupportedEncodingException {
        this.filePath = filePath;
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), settings.getSvmCharset()));
    }

    @Override
    public void write(String data) {
        try {
            bufferedWriter.write(data);
        } catch (IOException e) {
            log.error("Error while trying to write using reader {}, data was {}", this, data);
        }
    }

    @Override
    public void close() {
        try {
            bufferedWriter.close();
            log.info("File saved as {}", filePath);
        } catch (IOException e) {
            log.error("Unable to close writer {}", bufferedWriter);
        }
    }
}
