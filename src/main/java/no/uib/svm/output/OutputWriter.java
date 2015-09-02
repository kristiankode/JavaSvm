package no.uib.svm.output;

/**
 * @author kristian
 *         Created 02.09.15.
 */
public interface OutputWriter {

    void write(String data);

    void close();
}
