package no.uib.svm.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kristian
 *         Created 02.09.15.
 */
public class LogWriter implements OutputWriter {
    final static Logger log = LoggerFactory.getLogger(LogWriter.class);

    public void write(String data) {
        log.info(data);
    }

    @Override
    public void close() {
        // do nothing
        log.info("Writing complete");
    }
}
