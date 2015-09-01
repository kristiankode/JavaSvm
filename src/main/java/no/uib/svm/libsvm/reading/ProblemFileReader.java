package no.uib.svm.libsvm.reading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author kristian
 *         Created 31.08.15.
 */
public class ProblemFileReader {
    final static Logger log = LoggerFactory.getLogger(ProblemFileReader.class);

    final String filePath;
    final String encoding;
    final File file;

    BufferedReader fp;
    FileInputStream inputStream;
    int longestLineLength = 0;

    public ProblemFileReader(String filePath, String encoding)
            throws IOException {
        this.filePath = filePath;
        this.encoding = encoding;

        this.file = new File(filePath);
        log.debug("Loaded file {}", filePath);

        this.fp = new BufferedReader(new FileReader(filePath));
    }

    private void initInputStream() throws IOException {
        this.inputStream = new FileInputStream(file);
        log.debug("Total size to read: {} MB",
                convertToMegabytes(inputStream.available()));
    }

    private double convertToMegabytes(int bytes) {
        return bytes / 1048576;
    }


    public String readLine() throws IOException {
        String line = fp.readLine();
        saveLength(line);
        return fp.readLine();
    }

    private void saveLength(String line) {
        if (line == null) {
            return;
        }
        int length = line.length();
        if (length > longestLineLength) {
            longestLineLength = length;
            try {
                log.debug("Longest line was {}, that is {} MB",
                        longestLineLength,
                        convertToMegabytes(line.getBytes("utf-8").length));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() throws IOException {
        fp.close();
        log.debug("Closed reader");
    }
}
