package no.uib.svm.libsvm.core.settings;

/**
 * @author kristian
 *         Created 26.08.2015.
 */
public class DefaultSettings implements Settings {

    private static final String
            INPUT_CHARSET = "Unicode",
            OUTPUT_CHARSET = "UTF-8";

    private static final int WINDOW_SIZE = 1;

    @Override
    public String getInputCharset(){
        return INPUT_CHARSET;
    }

    @Override
    public int getWindowSize() {
        return WINDOW_SIZE;
    }

    @Override
    public String getOutputCharset(){
        return OUTPUT_CHARSET;
    }
}
