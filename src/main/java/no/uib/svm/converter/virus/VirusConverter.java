package no.uib.svm.converter.virus;

import no.uib.svm.converter.read.BufferedCsvReader;

import java.io.*;

/**
 * Created by Markus on 06.10.2015.
 */
public class VirusConverter {

    private static int numberOfVirus;

    private final static String virusFileEncoding = "UTF-8";




    public static void main(String... args){
        String filename = args[0];
        numberOfVirus = Integer.parseInt(args[1]);
        String filepath = args[2];
        VirusConverter vc = new VirusConverter();
        vc.createVirusFile(filename, filepath);
    }

    private void createVirusFile(String filename, String filepath) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filepath + numberOfVirus + "Virus.csv"));
            BufferedCsvReader reader = new BufferedCsvReader(filename, virusFileEncoding);
            String[] line;
            int counter = 0;
            while((line = reader.readNextRow()) != null && counter++ < numberOfVirus){
                String ntSequence = line[line.length-1];
                writer.write(ntSequence + ",Virus" + "\n");
                writer.flush();
            }
            writer.close();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


}
