package no.uib.svm.converter.write.destination;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * @author kristian
 *         Created 02.09.15.
 */
public class WriterFactory {

    public static OutputWriter getWriter()
            throws FileNotFoundException, UnsupportedEncodingException {
        return new FileWriter();
    }
}
