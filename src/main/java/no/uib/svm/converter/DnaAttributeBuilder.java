package no.uib.svm.converter;

import no.uib.svm.libsvm.core.settings.Settings;
import no.uib.svm.libsvm.core.settings.SettingsFactory;

import java.util.ArrayList;
import java.util.List;

public class DnaAttributeBuilder {
    public static final Settings settings = SettingsFactory.getActiveSettings();
    private final DnaToNumeric dnaToNumeric = new DnaToNumeric();

    public List<String> createDnaVector(String ntSequence) {

        List<String> dnaVector = new ArrayList<>(ntSequence.length());
        for (int i = 0; i < ntSequence.length(); i++) {
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
        } else if (i + settings.getWindowSize() < ntSequence.length()) {
            String substring = ntSequence.substring(i, i + settings.getWindowSize());
            int value = dnaToNumeric.valueOf(substring);
            if (value == -1) {
                System.out.println("Unable to find index for '" + substring + "'");
            }
            return value;
        } else {
            return 0;
        }
    }
}