package no.uib.svm.gui.utils;

public class StringToArray {

    private static final String SEPARATOR = ",";

    public static double[] convertToDoubleArray(String text) {
        String[] stringArray = text.split(SEPARATOR);
        double[] array = new double[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            array[i] = Double.parseDouble(stringArray[i].trim());
        }
        return array;
    }

    public static int[] convertToIntArray(String text) {
        String[] stringArray = text.split(SEPARATOR);
        int[] array = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            array[i] = Integer.parseInt(stringArray[i].trim());
        }
        return array;
    }

}