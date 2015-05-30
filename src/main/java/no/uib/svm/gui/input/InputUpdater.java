package no.uib.svm.gui.input;

import javafx.scene.control.TextField;

/**
 * Class responsible for updating input-fields when kernel or svm type is selected
 */
public class InputUpdater {
    public static void clearInput(TextField input) {
        input.setText("");
        input.setDisable(true);
    }

    public static void updateInput(TextField input, Object value) {
        input.setDisable(false);
        input.setText(String.valueOf(value));
    }
}