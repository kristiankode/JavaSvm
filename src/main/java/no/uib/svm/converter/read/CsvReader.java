package no.uib.svm.converter.read;

/**
 * @author kristian
 *         Created 03.09.15.
 */
public interface CsvReader {

    String[] readNextRow();

    void close();
}
