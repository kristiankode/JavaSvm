package no.uib.svm.libsvm.core.settings;

/**
 * @author kristian
 *         Created 26.08.2015.
 */
public class DefaultSettings implements Settings {

    private static final String
            CSV_CHARSET = "Unicode",
            SVM_CHARSET = "UTF-8";

    private static final int WINDOW_SIZE = 1;

    @Override
    public String getCsvCharset(){
        return CSV_CHARSET;
    }

    @Override
    public int getWindowSize() {
        return WINDOW_SIZE;
    }

    @Override
    public String getSvmCharset(){
        return SVM_CHARSET;
    }
}
