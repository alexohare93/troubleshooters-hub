package hub.troubleshooters.soundlink.core.auth.validation;

import hub.troubleshooters.soundlink.core.auth.models.RegisterModel;
import hub.troubleshooters.soundlink.core.validation.ModelValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;

public class RegisterModelValidator extends ModelValidator<RegisterModel> {

    @Override
    public ValidationResult validate(RegisterModel model) {
        if (model == null) {
            return new ValidationResult(new ValidationError("Login model is null"));
        }

        return ensure(
                notEmpty("Username", model.username()),
                notEmpty("Password", model.password())
        );
    }
}
