package hub.troubleshooters.soundlink.core.events.validation;

import hub.troubleshooters.soundlink.core.events.models.CreateEventModel;
import hub.troubleshooters.soundlink.core.validation.ModelValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationException;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateEventModelValidator implements ModelValidator<CreateEventModel> {

    private final CommunityFactory communityFactory;

    public CreateEventModelValidator(CommunityFactory communityFactory) {
        this.communityFactory = communityFactory;
    }

    @Override
    public ValidationResult validate(CreateEventModel createEventModel) {
        List<ValidationException> errors = new ArrayList<>();
        if (createEventModel == null) {
            return new ValidationResult(new ValidationException("Model is null"));
        }

        if (createEventModel.name() == null || createEventModel.name().isEmpty()) {
            errors.add(new ValidationException("Name is null or empty"));
        }

        if (createEventModel.description() == null || createEventModel.description().isEmpty()) {
            errors.add(new ValidationException("Description is null or empty"));
        }

        if (createEventModel.publishDate() == null) {
            errors.add(new ValidationException("Date is null"));
        } else if (createEventModel.publishDate().before(Date.from(Instant.now()))) {
            errors.add(new ValidationException("Publish date is before current date"));
        }

        // verify that community exists
        try {
            var community = communityFactory.get(createEventModel.communityId());
            if (community.isEmpty()) {
                errors.add(new ValidationException("Community not found with id: " + createEventModel.communityId()));
            }
        } catch (SQLException e) {
            errors.add(new ValidationException("Internal error: " + e.getMessage()));
        }

        if (errors.isEmpty()) {
            return new ValidationResult();
        }
        return new ValidationResult(errors);
    }
}
