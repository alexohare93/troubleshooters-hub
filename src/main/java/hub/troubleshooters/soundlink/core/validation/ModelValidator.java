package hub.troubleshooters.soundlink.core.validation;

/**
 * Class responsible for model validation.
 */
public interface ModelValidator<T> {

    /**
     * Validates a given model using rules that have been specified.
     * @param model the model that is being validated
     * @return a ValidationResult which can contain potentially many validation errors that occurred during validation.
     */
    ValidationResult validate(T model);
}
