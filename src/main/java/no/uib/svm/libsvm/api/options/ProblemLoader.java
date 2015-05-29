package no.uib.svm.libsvm.api.options;

import no.uib.svm.libsvm.core.libsvm.Node;
import no.uib.svm.libsvm.core.libsvm.Problem;
import no.uib.svm.libsvm.core.libsvm.SvmParameter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author kristian
 *         Created 29.05.15.
 */
public class ProblemLoader {

    /**
     * Read in a problem (in svmlight format)
     *
     * @throws IOException
     * @param fileName
     * @param param
     */
    public static Problem loadProblemFromFile(String fileName, SvmParameter param) throws IOException {
        BufferedReader fp = new BufferedReader(new FileReader(fileName));
        Vector<Double> vy = new Vector<Double>();
        Vector<Node[]> vx = new Vector<Node[]>();
        int max_index = 0;

        while (true) {
            String line = fp.readLine();
            if (line == null) break;

            StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

            vy.addElement(stringToDouble(st.nextToken()));
            int m = st.countTokens() / 2;
            Node[] x = new Node[m];
            for (int j = 0; j < m; j++) {
                x[j] = new Node();
                x[j].index = stringToInt(st.nextToken());
                x[j].value = stringToDouble(st.nextToken());
            }
            if (m > 0) max_index = Math.max(max_index, x[m - 1].index);
            vx.addElement(x);
        }

        Problem prob = new Problem();
        prob.l = vy.size();
        prob.x = new Node[prob.l][];
        for (int i = 0; i < prob.l; i++)
            prob.x[i] = vx.elementAt(i);
        prob.y = new double[prob.l];
        for (int i = 0; i < prob.l; i++)
            prob.y[i] = vy.elementAt(i);

        if (param.gamma == 0 && max_index > 0)
            param.gamma = 1.0 / max_index;

        if (param.kernel_type == SvmParameter.PRECOMPUTED)
            for (int i = 0; i < prob.l; i++) {
                if (prob.x[i][0].index != 0) {
                    System.err.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
                    System.exit(1);
                }
                if ((int) prob.x[i][0].value <= 0 || (int) prob.x[i][0].value > max_index) {
                    System.err.print("Wrong input format: sample_serial_number out of range\n");
                    System.exit(1);
                }
            }

        fp.close();

        return prob;
    }

    /**
     * Converts a string to a double
     * @param s The string to convert
     * @return A double
     */
    private static double stringToDouble(String s) {
        double d = Double.valueOf(s);
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            System.err.print("NaN or Infinity in input\n");
            System.exit(1);
        }
        return (d);
    }

    /**
     * Converts a String to an Integer
     * @param s The string to convert
     * @return An integer
     */
    private static int stringToInt(String s) {
        return Integer.parseInt(s);
    }
}
