package no.uib.svm.converter;

import no.uib.svm.libsvm.core.settings.Settings;
import no.uib.svm.libsvm.core.settings.SettingsFactory;

import java.util.ArrayList;
import java.util.List;

import static no.uib.svm.converter.DnaToNumeric.numericValueOfSingle;
import static no.uib.svm.converter.DnaToNumeric.numericValueOfSubstring;

public class DnaAttributeBuilder {
    public static final int NUMBER_OF_ATTRIBUTES = 1200;
    public static final Settings settings = SettingsFactory.getActiveSettings();

    public List<String> createDnaVector(String ntSequence) {

        List<String> dnaVector = new ArrayList<>(ntSequence.length());
        for (int i = 0; i < NUMBER_OF_ATTRIBUTES; i++) {
            dnaVector.add(buildAttributeString(ntSequence, i));
        }
        return dnaVector;
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
        } else if (settings.getWindowSize() == 1) {
            return numericValueOfSingle(ntSequence.charAt(i));
        } else if(i + settings.getWindowSize() < ntSequence.length()) {
            String substring = ntSequence.substring(i, i + settings.getWindowSize());
            int value = numericValueOfSubstring(substring);
            if(value == -1) {
                System.out.println("Unable to find index for '" + substring + "'");
            }
            return value;
        } else {
            return 0;
        }
    }
}