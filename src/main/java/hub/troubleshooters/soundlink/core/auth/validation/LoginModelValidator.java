package hub.troubleshooters.soundlink.core.auth.validation;

import hub.troubleshooters.soundlink.core.auth.models.LoginModel;
import hub.troubleshooters.soundlink.core.validation.ModelValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;

public class LoginModelValidator extends ModelValidator<LoginModel> {

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
