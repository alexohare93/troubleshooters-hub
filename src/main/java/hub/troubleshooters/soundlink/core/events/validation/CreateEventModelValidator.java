package hub.troubleshooters.soundlink.core.events.validation;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.events.models.CreateEventModel;
import hub.troubleshooters.soundlink.core.validation.ModelValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;

import java.io.File;
import java.util.Optional;

/**
 * Validator for validating a {@link CreateEventModel} before creating an event.
 *
 * <p>This class extends the {@link ModelValidator} class and provides validation rules specific to event creation.
 * It checks the required fields, ensures the scheduled date is in the future, checks the community existence, and verifies the event capacity.</p>
 *
 * @see CreateEventModel
 * @see ModelValidator
 */
public class CreateEventModelValidator extends ModelValidator<CreateEventModel> {

    private final CommunityFactory communityFactory;

    /**
     * Constructs the validator and injects the {@link CommunityFactory} to check the existence of communities.
     *
     * @param communityFactory The factory used to validate if a community exists.
     */
    @Inject
    public CreateEventModelValidator(CommunityFactory communityFactory) {
        this.communityFactory = communityFactory;
    }

    /**
     * Validates the given {@link CreateEventModel}.
     *
     * <p>This method checks for the following validation rules:
     * <ul>
     *  <li>The event name and description should not be empty.</li>
     *  <li>The event's scheduled date must be in the future.</li>
     *  <li>The location of the event should not be empty.</li>
     *  <li>The event capacity should be positive.</li>
     *  <li>The specified community must exist in the system.</li>
     *  <li>The optional banner image, if present, should be in the correct image format (PNG, JPG, JPEG).</li>
     * </ul>
     * </p>
     *
     * @param createEventModel The model that holds the event details.
     * @return A {@link ValidationResult} containing validation errors, if any.
     */
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
