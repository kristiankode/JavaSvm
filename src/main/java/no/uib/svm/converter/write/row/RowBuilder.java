package no.uib.svm.converter.write.row;

import no.uib.svm.converter.domain.Genome;
import no.uib.svm.converter.write.attributes.AttributeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kristian
 *         Created 03.09.15.
 */
public class RowBuilder {

    public static final String
            KEY_VALUE_SEPARATOR = ":",
            ATTR_SEPARATOR = " ",
            LINE_SEPARATOR = "\n";
    public static final int START_INDEX = 1;

    final AttributeBuilder attributeBuilder;

    public RowBuilder(AttributeBuilder attributeBuilder) {
        this.attributeBuilder = attributeBuilder;
    }

    public String buildRow(Genome genome) {
        StringBuilder row = new StringBuilder();

        row.append(getClassification(genome)).append(ATTR_SEPARATOR);

        for (String s : getAttributeStrings(genome)) {
            row.append(s).append(LINE_SEPARATOR);
        }

        return row.toString();
    }

    String getClassification(Genome genome) {
        return String.valueOf(genome.getLibraryType().getCode());
    }

    List<String> getAttributeStrings(Genome genome) {
        return buildAttributeStrings(attributeBuilder.getAttributeValues(genome));
    }

    List<String> buildAttributeStrings(List<Double> values) {
        List<String> attrs = new ArrayList<>(values.size());
        int index = START_INDEX;

        for (Double value : values) {
            String attrString = buildAttributeString(index, value);
            if (attrString != null) {
                attrs.add(attrString);
            }
            index++;
        }

        return attrs;
    }

    String buildAttributeString(int index, Double value) {
        StringBuilder sb = new StringBuilder();

        if (!attributeIsEmpty(value)) {
            sb
                    .append(index)
                    .append(KEY_VALUE_SEPARATOR)
                    .append(value)
                    .append(ATTR_SEPARATOR);
            return sb.toString();
        }
        return null;
    }

    boolean attributeIsEmpty(Double attributeValue) {
        return attributeValue == 0;
    }
}
