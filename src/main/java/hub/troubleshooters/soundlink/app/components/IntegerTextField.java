package hub.troubleshooters.soundlink.app.components;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

/**
 * A TextField that only accepts digits [0-9]*. It does not allow non-integer values or numbers less than 0.
 */
public class IntegerTextField extends TextField {

    /**
     * Constructs an {@code IntegerTextField}.
     */
    public IntegerTextField() {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String input = change.getText();
            if (input.matches("[0-9]*")) {
                return change;
            }
            return null;
        };

        setTextFormatter(new TextFormatter<String>(integerFilter));
    }

    /**
     * Gets the {@code int} value of the text field.
     * @return The value in the text field, 0 if the field is empty.
     */
    public int getValue() {
        if (getText().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(getText());
    }
}
