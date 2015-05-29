package no.uib.svm.libsvm.api.options.logging;

import no.uib.svm.libsvm.core.libsvm.PrintInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kristian
 *         Created 29.05.15.
 */
public class Messages implements PrintInterface {

    private static int LIMIT = 500;

    List<String> messages = new ArrayList<String>();

    @Override
    public void print(String s) {
        System.out.println(s);
        messages.add(s);
        limitNumberOfMessagesTo(LIMIT);
    }

    private void limitNumberOfMessagesTo(int limit) {
        while (messages.size() > limit) {
            messages.remove(0);
        }
    }
}
