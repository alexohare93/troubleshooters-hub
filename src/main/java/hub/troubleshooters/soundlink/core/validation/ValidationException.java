package hub.troubleshooters.soundlink.core.validation;

import java.util.List;

/**
 * An exception that occurs during model validation.
 * @see ModelValidator
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(List<ValidationException> errors) {
        super(new ValidationException(errors.stream()
                .map(e -> '{' + e.getMessage() + '}')
                .reduce((msg, acc) -> msg + "," + acc)
                .orElse("")
        ));
    }
}
