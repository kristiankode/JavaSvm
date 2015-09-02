package no.uib.svm.converter;

import no.uib.svm.converter.Genbank.Genome;
import no.uib.svm.output.OutputWriter;

import java.io.*;
import java.util.List;

public class SubstringAsFeatureWriter {

    final DnaAttributeBuilder attributeBuilder = new DnaAttributeBuilder();
    final OutputWriter outputWriter;

    public SubstringAsFeatureWriter(OutputWriter outputWriter) {
        this.outputWriter = outputWriter;
    }

    public void writeLine(Genome genome)
            throws IOException {

        List<String> dnaVector = attributeBuilder.createDnaVector(genome.getNtSequence());
        StringBuilder attributes = new StringBuilder();
        dnaVector.forEach(attributes::append);

        int classification = genome.getLibraryType().getCode();
        outputWriter.write(classification + CsvSVMLight.ATTR_SEPARATOR + attributes.toString() + CsvSVMLight.LINE_SEPARATOR);
    }
}