package hub.troubleshooters.soundlink.core.events.services;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.events.models.CreateEventModel;
import hub.troubleshooters.soundlink.core.events.validation.CreateEventModelValidator;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.factories.EventFactory;
import hub.troubleshooters.soundlink.data.factories.ImageFactory;

import java.io.IOException;
import java.sql.SQLException;

public class EventServiceImpl implements EventService {

    private final CreateEventModelValidator createEventModelValidator;
    private final EventFactory eventFactory;
    private final ImageUploaderService imageUploaderService;

    @Inject
    public EventServiceImpl(CreateEventModelValidator createEventModelValidator, EventFactory eventFactory, ImageUploaderService imageUploaderService) {
        this.createEventModelValidator = createEventModelValidator;
        this.eventFactory = eventFactory;
        this.imageUploaderService = imageUploaderService;
    }

    @Override
    public ValidationResult createEvent(CreateEventModel model) {

        // validate
        var result = createEventModelValidator.validate(model);
        if (!result.isSuccess()) {
            return result;
        }

        // save event to DB
        try {
            imageUploaderService.upload(model.bannerImage());
            eventFactory.create(model.name(), model.description(), model.communityId(), model.location(), model.capacity(), model.scheduledDate());
        } catch (SQLException | IOException e) {
            return new ValidationResult(new ValidationError("Internal error: please contact SoundLink Support."));
        }
        return new ValidationResult();
    }
}
