package no.uib.svm.converter.write.attributes.sequences;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * @author kristian
 *         Created 03.09.15.
 */
public class SequenceGeneratorImplTest {

    SequenceGeneratorImpl instance = new SequenceGeneratorImpl();

    @Test
    public void allPossible_withLength1_shouldBeEqualToNumberOfCharacters() {

        HashSet<String> result = instance.getAllPossibleCombinationsOfLength(1);
        int expected = SequenceGeneratorImpl.DNA_CHARACTERS.length(),
                actual = result.size();

        assertEquals(expected, actual);
    }

    @Test
    public void allPossible_withLength2_shouldBeEqualToDnaCharsToThePowerOf2() {

        HashSet<String> result = instance.getAllPossibleCombinationsOfLength(2);
        int expected = powerOf(SequenceGeneratorImpl.DNA_CHARACTERS.length(), 2),
                actual = result.size();

        assertEquals(expected, actual);
    }

    @Test
    public void allPossible_withLength3_shouldBeEqualToDnaCharsToThePowerOf3() {

        HashSet<String> result = instance.getAllPossibleCombinationsOfLength(3);
        int expected = powerOf(SequenceGeneratorImpl.DNA_CHARACTERS.length(), 3),
                actual = result.size();

        assertEquals(expected, actual);
    }

    private int powerOf(int number, int raisedTo) {
        return BigDecimal.valueOf(number).pow(raisedTo).intValue();
    }

}