package hub.troubleshooters.soundlink.core.auth.validation;

import hub.troubleshooters.soundlink.core.auth.models.LoginModel;
import hub.troubleshooters.soundlink.core.validation.ModelValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;

/**
 * Validator class for validating the {@link LoginModel}.
 * Ensures that the model is well-formed and that both the username and password fields are not empty.
 */
public class LoginModelValidator extends ModelValidator<LoginModel> {

    /**
     * Validates the given {@link LoginModel} to ensure that it contains valid data.
     * The validation checks if the model itself is non-null and that both the username and password
     * are not empty. If any validation rule is violated, an error is added to the {@link ValidationResult}.
     *
     * @param model The {@link LoginModel} to be validated.
     * @return A {@link ValidationResult} indicating whether the validation passed or failed.
     *         If the validation fails, the result contains a list of {@link ValidationError}s.
     */
    @Override
    public ValidationResult validate(LoginModel model) {
        if (model == null) {
            return new ValidationResult(new ValidationError("Login model is null"));
        }

        return ensure(
                notEmpty("Username", model.username()),
                notEmpty("Password", model.password())
        );
    }
}
