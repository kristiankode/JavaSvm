package no.uib.svm.converter;

import no.uib.svm.libsvm.core.settings.Settings;
import no.uib.svm.libsvm.core.settings.SettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.currentTimeMillis;

public class DnaAttributeBuilder {
    private static Logger log = LoggerFactory.getLogger(DnaAttributeBuilder.class);

    public static final Settings settings = SettingsFactory.getActiveSettings();
    private static final DnaToNumeric dnaToNumeric = new DnaToNumeric(settings.getWindowSize());
    long counter = 0; // number of processed entries
    final long startupTime = currentTimeMillis();

    // debug messages
    final static String
            CONVERT_MSG = "Converted {} dna sequences in {} millis",
            UNKNOWN_SUBSTRING = "Unable to find index for {}";

    public List<String> createDnaVector(String ntSequence) {

        List<String> dnaVector = new ArrayList<>(ntSequence.length());
        for (int i = 0; i < ntSequence.length(); i++) {
            dnaVector.add(buildAttributeString(ntSequence, i));
        }
        counter++;
        printInfo();
        return dnaVector;
    }

    void printInfo(){
        if(counter % 1000 == 0) {
            log.debug(CONVERT_MSG, counter, currentTimeMillis() - startupTime);
        }
    }

    String buildAttributeString(String ntSequence, int i) {
        int attributeValue = this.getAttributeValue(ntSequence, i);
        StringBuilder sb = new StringBuilder();

        if (!this.attributeIsEmpty(attributeValue)) {
            sb.append(i + 1).append(CsvSVMLight.KEY_VALUE_SEPARATOR);
            sb.append(attributeValue);
            sb.append(CsvSVMLight.ATTR_SEPARATOR);
        }

        return sb.toString();
    }

    boolean attributeIsEmpty(int attributeValue) {
        return attributeValue == 0;
    }

    int getAttributeValue(String ntSequence, int i) {
        if (i >= ntSequence.length() - settings.getWindowSize()) {
            return 0;
        } else if (i + settings.getWindowSize() < ntSequence.length()) {
            String substring = ntSequence.substring(i, i + settings.getWindowSize());
            int value = dnaToNumeric.valueOf(substring);
            if (value == -1) {
                log.debug(UNKNOWN_SUBSTRING, substring);
            }
            return value;
        } else {
            return 0;
        }
    }
}