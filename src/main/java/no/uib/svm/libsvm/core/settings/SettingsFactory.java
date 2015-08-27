package no.uib.svm.libsvm.core.settings;

/**
 * @author kristian
 *         Created 26.08.2015.
 */
public class SettingsFactory {

    static Settings defaultSettings = new DefaultSettings();

    public static Settings getActiveSettings(){
        return defaultSettings;
    }
}
