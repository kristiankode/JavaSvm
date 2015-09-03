package no.uib.svm.converter.write.attributes;

import no.uib.svm.converter.domain.Genome;

import java.util.List;

/**
 * @author kristian
 *         Created 03.09.15.
 */
public interface AttributeBuilder {
    List<Double> getAttributeValues(Genome genome);
}
