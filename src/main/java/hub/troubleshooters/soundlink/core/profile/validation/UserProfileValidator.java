package hub.troubleshooters.soundlink.core.profile.validation;

import hub.troubleshooters.soundlink.core.profile.models.UserProfileUpdateModel;
import hub.troubleshooters.soundlink.core.validation.ModelValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;

import java.io.File;

/**
 * Validator class for validating a {@link UserProfileUpdateModel}.
 *
 * <p>This class extends {@link ModelValidator} and provides validation logic for user profile updates,
 * including checking if the display name is not empty and, if present, whether the profile image is a valid image format.</p>
 */
public class UserProfileValidator extends ModelValidator<UserProfileUpdateModel> {

    /**
     * Validates the provided {@link UserProfileUpdateModel}.
     *
     * <p>This method performs validation on the display name and, if present, the profile image.
     * The display name must be non-empty, and the profile image must be in a valid image format (PNG, JPG, or JPEG).</p>
     *
     * @param updateUserModel The user profile update model to validate.
     * @return A {@link ValidationResult} object containing any validation errors or indicating success.
     */
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
