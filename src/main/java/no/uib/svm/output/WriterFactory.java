package no.uib.svm.output;

/**
 * @author kristian
 *         Created 02.09.15.
 */
public class WriterFactory {

    private static OutputWriter defaultWriter = new LogWriter();

    public static OutputWriter getWriter(){
        return defaultWriter;
    }
}
