package hub.troubleshooters.soundlink.core.events.validation;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.events.models.SearchEventModel;
import hub.troubleshooters.soundlink.core.validation.ModelValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;

import java.util.Optional;

public class SearchEventModelValidator extends ModelValidator<SearchEventModel> {

    private final EventFactory eventFactory;

    @Inject
    public SearchEventModelValidator(EventFactory eventFactory) {
        this.eventFactory= eventFactory;
    }

    @Override
    public ValidationResult validate(SearchEventModel searchEventModel) {
        if (searchEventModel == null) {
            return new ValidationResult(new ValidationError("Model is null"));
        }

        return ensure(
            notEmpty("Name", searchEventModel.name()),
            notEmpty("Description", searchEventModel.description()),
            isFuture("Scheduled date", searchEventModel.scheduledDate()),
            notEmpty("Venue", searchEventModel.venue()),
            isPositive("Capacity", searchEventModel.capacity()),
            communityExists(searchEventModel.communityId())
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
