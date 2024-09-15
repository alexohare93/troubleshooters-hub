package hub.troubleshooters.soundlink.core.events.validation;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.events.models.CreateEventModel;
import hub.troubleshooters.soundlink.core.validation.ModelValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateEventModelValidator implements ModelValidator<CreateEventModel> {

    private final CommunityFactory communityFactory;

    @Inject
    public CreateEventModelValidator(CommunityFactory communityFactory) {
        this.communityFactory = communityFactory;
    }

    @Override
    public ValidationResult validate(CreateEventModel createEventModel) {
        List<ValidationError> errors = new ArrayList<>();
        if (createEventModel == null) {
            return new ValidationResult(new ValidationError("Model is null"));
        }

        if (createEventModel.name() == null || createEventModel.name().isEmpty()) {
            errors.add(new ValidationError("Name is null or empty"));
        }

        if (createEventModel.description() == null || createEventModel.description().isEmpty()) {
            errors.add(new ValidationError("Description is null or empty"));
        }

        if (createEventModel.publishDate() == null) {
            errors.add(new ValidationError("Date is null"));
        } else if (createEventModel.publishDate().before(Date.from(Instant.now()))) {
            errors.add(new ValidationError("Publish date is before current date"));
        }

        if (createEventModel.location() == null || createEventModel.location().isEmpty()) {
            errors.add(new ValidationError("Location is null or empty"));
        }

        // verify that community exists
        try {
            var community = communityFactory.get(createEventModel.communityId());
            if (community.isEmpty()) {
                errors.add(new ValidationError("Community not found with id: " + createEventModel.communityId()));
            }
        } catch (SQLException e) {
            errors.add(new ValidationError("Internal error: " + e.getMessage()));
        }

        if (errors.isEmpty()) {
            return new ValidationResult();
        }
        return new ValidationResult(errors);
    }
}
