package no.uib.svm.converter.domain;

/**
 * @author kristian
 *         Created 02.09.15.
 */
public enum LibraryType {

    FUNGI(-1), BACTERIA(1), VIRUS(-1);

    final int code;

    LibraryType(int i) {
        this.code = i;
    }

    public int getCode(){
        return this.code;
    }

    public static LibraryType fromString(String stringValue) {
        return LibraryType.valueOf(stringValue.toUpperCase().trim());
    }
}
