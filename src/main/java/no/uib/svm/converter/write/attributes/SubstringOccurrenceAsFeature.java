package no.uib.svm.converter.write.attributes;

import no.uib.svm.converter.domain.Genome;
import no.uib.svm.converter.write.attributes.sequences.SequenceGenerator;
import no.uib.svm.libsvm.core.settings.Settings;
import no.uib.svm.libsvm.core.settings.SettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.valueOf;
import static org.apache.commons.lang.StringUtils.countMatches;

/**
 * Counts the number of times a substring occurs in a string.
 */
public class SubstringOccurrenceAsFeature implements AttributeBuilder {

    public static final int INITIAL_COUNT_VALUE = 0;
    private static Logger log = LoggerFactory.getLogger(SubstringOccurrenceAsFeature.class);
    final Settings settings = SettingsFactory.getActiveSettings();

    SequenceGenerator seqGen = new SequenceGenerator();
    HashSet<String> allPossibleCombos = seqGen.getAllPossibleCombinationsOfLength(settings.getWindowSize());
    HashMap<String, Integer> buckets;

    public SubstringOccurrenceAsFeature() {
        initBuckets();
    }

    @Override
    public List<Double> getAttributeValues(Genome genome) {
        long startTime = currentTimeMillis();
        int totalLength = genome.getNtSequence().length();
        initBuckets();
        fillBuckets(genome);

        List<Double> result = buckets.keySet().stream().map(subseq ->
                normalize(buckets.get(subseq), totalLength)).collect(Collectors.toList());
        emptyBuckets(); // save some memory

        long exectime = currentTimeMillis() - startTime;
        if (exectime > 3000) {
            log.debug("Analysis of seq.length {} finished in {} millis",
                    genome.getNtSequence().length(),
                    exectime);
        }
        return result;
    }

    Double normalize(int count, int totalLength) {
        return valueOf(count).divide(valueOf(totalLength), 4, ROUND_HALF_UP).doubleValue();
    }

    public void fillBuckets(Genome genome) {
        for (String subsequence : buckets.keySet()) {
            buckets.put(subsequence, countMatches(genome.getNtSequence(), subsequence));
        }
    }

    void emptyBuckets() {
        this.buckets = null;
    }

    void initBuckets() {
        buckets = new HashMap<>();
        for (String combo : allPossibleCombos) {
            buckets.put(combo, INITIAL_COUNT_VALUE);
        }
    }


}