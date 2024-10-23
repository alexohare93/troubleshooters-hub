package hub.troubleshooters.soundlink.core.communities.validation;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.communities.models.CreateCommunityModel;
import hub.troubleshooters.soundlink.core.validation.ModelValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;

import java.io.File;
import java.util.Optional;

/**
 * Validator class for validating the {@link CreateCommunityModel}.
 * Ensures that the model is well-formed and contains all the necessary fields for creating a community.
 */
public class CreateCommunityModelValidator extends ModelValidator<CreateCommunityModel> {

    /**
     * Constructs a new {@code CreateCommunityModelValidator}.
     * This class is typically injected into the service layer where the community creation process takes place.
     */
    @Inject
    public CreateCommunityModelValidator() {
    }

    /**
     * Validates the given {@link CreateCommunityModel} to ensure that it contains valid data for creating a community.
     * The validation checks if the model itself is non-null and that the community name, description, and genre
     * are not empty. It also validates if a banner image is provided, ensuring the file is a valid image format.
     *
     * @param createCommunityModel The {@link CreateCommunityModel} to be validated.
     * @return A {@link ValidationResult} indicating whether the validation passed or failed.
     *         If the validation fails, the result contains a list of {@link ValidationError}s.
     */
    @Override
    public ValidationResult validate(CreateCommunityModel createCommunityModel) {
        if (createCommunityModel == null) {
            return new ValidationResult(new ValidationError("Model is null"));
        }

        return ensure(
                notEmpty("Name", createCommunityModel.name()),
                notEmpty("Description", createCommunityModel.description()),
                notEmpty("Genre", createCommunityModel.genre()),
                ifPresent("Banner image", createCommunityModel.bannerImage(), (name, value) -> isImage(name, (File) value))
        );
    }
}
