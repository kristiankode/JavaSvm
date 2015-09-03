package no.uib.svm.converter.write.attributes;

import no.uib.svm.converter.domain.Genome;
import no.uib.svm.converter.write.attributes.sequences.DnaToNumeric;
import no.uib.svm.libsvm.core.settings.Settings;
import no.uib.svm.libsvm.core.settings.SettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.currentTimeMillis;

public class SubstringAsFeature implements AttributeBuilder {
    private static Logger log = LoggerFactory.getLogger(SubstringAsFeature.class);

    public static final Settings settings = SettingsFactory.getActiveSettings();
    private static final DnaToNumeric dnaToNumeric = new DnaToNumeric(settings.getWindowSize());
    long counter = 0; // number of processed entries
    final long startupTime = currentTimeMillis();
    int longestDnaSequenceLength = 0;

    // debug messages
    final static String
            CONVERT_MSG = "Converted {} dna sequences in {} millis," +
            "longest sequence: {}",
            UNKNOWN_SUBSTRING = "Unable to find index for {}";

    @Override
    public List<Double> getAttributeValues(Genome genome) {
        List<Double> values = new ArrayList<>();

        for (int i = 0; i < genome.getNtSequence().length(); i++) {
            values.add((double) getAttributeValue(genome.getNtSequence(), i));
        }

        return values;
    }

    void saveMax(int length) {
        if (length > longestDnaSequenceLength) {
            longestDnaSequenceLength = length;
        }
    }

    void printInfo() {
        if (counter % 500 == 0) {
            log.debug(
                    CONVERT_MSG,
                    counter,
                    currentTimeMillis() - startupTime,
                    longestDnaSequenceLength);
        }
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