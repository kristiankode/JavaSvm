package no.uib.svm.converter.domain;

/**
 * Class representing the genome data to convert.
 */
public class Genome {

    private final String ntSequence;
    private final LibraryType libraryType;

    public Genome(String ntSequence, String libraryType) {
        this.ntSequence = ntSequence;
        this.libraryType = LibraryType.fromString(libraryType);
    }

    public String getNtSequence() {
        return ntSequence;
    }

    public LibraryType getLibraryType() {
        return libraryType;
    }
}
