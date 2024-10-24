package hub.troubleshooters.soundlink.core.validation;

import hub.troubleshooters.soundlink.core.CoreResult;

import java.util.*;

/**
 * Represents the result of a validation operation, which either contains no errors (indicating success)
 * or a list of {@link ValidationError} objects (indicating failure).
 *
 * <p>This class extends {@link CoreResult} where the result type is {@code Void} and the error type is {@link ValidationError}.
 * It is used to encapsulate the outcome of validation processes, collecting any validation errors that occurred during the process.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     ValidationResult result = validate(userInput);
 *     if (result.isSuccess()) {
 *         System.out.println("Validation passed");
 *     } else {
 *         for (ValidationError error : result.getErrors()) {
 *             System.err.println("Validation error: " + error.getMessage());
 *         }
 *     }
 * </pre>
 *
 * <p>There are several constructors to handle different cases: single error, multiple errors, or no errors (indicating success).</p>
 */
public class ValidationResult extends CoreResult<Void, ValidationError> {

    // Holds a list of validation errors, if any exist.
    private List<ValidationError> errors = new ArrayList<>();

    /**
     * Constructs a ValidationResult representing a failure with a single validation error.
     *
     * @param error The single validation error.
     */
    public ValidationResult(ValidationError error) {
        super(error);  // Call the superclass constructor with the error.
        errors = Collections.singletonList(error);  // Store the error in the errors list.
    }

    /**
     * Constructs a ValidationResult representing a failure with multiple validation errors.
     *
     * @param errors The list of validation errors.
     */
    public ValidationResult(List<ValidationError> errors) {
        super(errors.isEmpty() ? null : new ValidationError(errors));  // Pass a combined error to the superclass if errors exist.
        this.errors = errors;  // Store the list of errors.
    }

    /**
     * Constructs a ValidationResult representing success (i.e., no validation errors).
     */
    public ValidationResult() {
        super((Void) null);  // No error, indicating success.
    }

    /**
     * Returns the list of validation errors that occurred during the validation process.
     *
     * @return The list of validation errors, or an empty list if validation passed.
     */
    public List<ValidationError> getErrors() {
        return errors;
    }
}