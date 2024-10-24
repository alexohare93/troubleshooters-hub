package hub.troubleshooters.soundlink.core.validation;

import java.util.List;

/**
 * An error that occurs during model validation.
 * @see ModelValidator
 */
public class ValidationError extends RuntimeException {

    /**
     * Constructs a ValidationError with a single error message.
     *
     * @param message The error message describing the validation failure.
     */
    public ValidationError(String message) {
        super(message);
    }

    /**
     * Constructs a ValidationError that combines the messages from a list of other ValidationError instances.
     *
     * <p>The messages from each individual {@link ValidationError} in the list are wrapped in curly braces and
     * combined into a single message, separated by commas.</p>
     *
     * @param errors The list of {@link ValidationError} instances to combine into a single error message.
     */
    public ValidationError(List<ValidationError> errors) {
        super(new ValidationError(errors.stream()
                .map(e -> '{' + e.getMessage() + '}')
                .reduce((msg, acc) -> msg + "," + acc)
                .orElse("")
        ));
    }
}
