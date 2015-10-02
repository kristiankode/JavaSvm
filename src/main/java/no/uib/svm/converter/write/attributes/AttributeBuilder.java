package no.uib.svm.converter.write.attributes;

import no.uib.svm.converter.domain.Genome;

import java.util.List;

/**
 * Creates a numerical representation of the attributes of a genome.
 */
public interface AttributeBuilder {

    /**
     * A list of numeric values that describes the attributes of a genome.
     * The classification is NOT included in this list.
     *
     * @param genome The genome to get attributes for.
     * @return List of attributes as numbers.
     */
    List<Double> getAttributeValues(Genome genome);
}
