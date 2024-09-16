package hub.troubleshooters.soundlink.core.validation;

import java.util.List;

/**
 * An error that occurs during model validation.
 * @see ModelValidator
 */
public class ValidationError extends RuntimeException {
    public ValidationError(String message) {
        super(message);
    }

    public ValidationError(List<ValidationError> errors) {
        super(new ValidationError(errors.stream()
                .map(e -> '{' + e.getMessage() + '}')
                .reduce((msg, acc) -> msg + "," + acc)
                .orElse("")
        ));
    }
}
