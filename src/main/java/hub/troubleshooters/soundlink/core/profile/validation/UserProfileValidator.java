package hub.troubleshooters.soundlink.core.profile.validation;

import hub.troubleshooters.soundlink.core.profile.models.UserProfileUpdateModel;
import hub.troubleshooters.soundlink.core.validation.ModelValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;

import java.io.File;

public class UserProfileValidator extends ModelValidator<UserProfileUpdateModel> {

    @Override
    public ValidationResult validate(UserProfileUpdateModel updateUserModel) {
        if (updateUserModel == null) {
            return new ValidationResult(new ValidationError("Model is null"));
        }

        return ensure(
                notEmpty("Display name", updateUserModel.displayName()),
                ifPresent("Profile image", updateUserModel.profileImage(), (name, value) -> isImage(name, (File) value))
        );
    }
}
