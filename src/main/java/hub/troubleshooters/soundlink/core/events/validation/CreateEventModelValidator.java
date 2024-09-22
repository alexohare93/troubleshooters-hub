package hub.troubleshooters.soundlink.core.events.validation;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.events.models.CreateEventModel;
import hub.troubleshooters.soundlink.core.validation.ModelValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;

import java.io.File;
import java.util.Optional;

public class CreateEventModelValidator extends ModelValidator<CreateEventModel> {

    private final CommunityFactory communityFactory;

    @Inject
    public CreateEventModelValidator(CommunityFactory communityFactory) {
        this.communityFactory = communityFactory;
    }

    @Override
    public ValidationResult validate(CreateEventModel createEventModel) {
        if (createEventModel == null) {
            return new ValidationResult(new ValidationError("Model is null"));
        }

        return ensure(
                notEmpty("Name", createEventModel.name()),
                notEmpty("Description", createEventModel.description()),
                isFuture("Scheduled date", createEventModel.scheduledDate()),
                notEmpty("Location", createEventModel.location()),
                isPositive("Capacity", createEventModel.capacity()),
                communityExists(createEventModel.communityId()),
                ifPresent("Banner image", createEventModel.bannerImage(), (name, value) -> isImage(name, (File) value))
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
