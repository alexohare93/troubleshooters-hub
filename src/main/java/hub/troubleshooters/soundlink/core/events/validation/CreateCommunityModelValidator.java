package hub.troubleshooters.soundlink.core.events.validation;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.events.models.CreateCommunityModel;
import hub.troubleshooters.soundlink.core.validation.ModelValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;

import java.io.File;
import java.util.Optional;

public class CreateCommunityModelValidator extends ModelValidator<CreateCommunityModel> {

    private final CommunityFactory communityFactory;

    @Inject
    public CreatecommunityModelValidator(CommunityFactory communityFactory) {
        this.communityFactory = communityFactory;
    }

    @Override
    public ValidationResult validate(CreateCommunityModel createCommunityModel) {
        if (createCommunityModel == null) {
            return new ValidationResult(new ValidationError("Model is null"));
        }

        return ensure(
                communityExists(createCommunityModel.Id()),
                notEmpty("Name", createEventModel.name()),
                notEmpty("Description", createEventModel.description()),
                isFuture("Genre", createEventModel.genre()),
                notEmpty("Community creation date", createEventModel.created()),
                ifPresent("Banner image", createCommunityModel.bannerImage(), (name, value) -> isImage(name, (File) value))
        );
    }

    /**
     * A wrapper for the base class' version of this method that injects CommunityFactory
     * @param communityId The ID of the community being checked
     * @return Optional.empty() if validation succeeds, else an Optional.of the validation error.
     */
    private Optional<ValidationError> communityExists(int communityId) {
        return communityExists(communityFactory, communityId);
    }
}
