package hub.troubleshooters.soundlink.core.events.services;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.Map;
import hub.troubleshooters.soundlink.core.events.models.CreateEventModel;
import hub.troubleshooters.soundlink.core.events.models.EventModel;
import hub.troubleshooters.soundlink.core.events.validation.CreateEventModelValidator;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.factories.EventFactory;
import hub.troubleshooters.soundlink.data.factories.ImageFactory;
import hub.troubleshooters.soundlink.data.models.Event;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class EventServiceImpl implements EventService {

    private final CreateEventModelValidator createEventModelValidator;
    private final EventFactory eventFactory;
    private final ImageUploaderService imageUploaderService;
    private final Map map;

    @Inject
    public EventServiceImpl(CreateEventModelValidator createEventModelValidator, EventFactory eventFactory, ImageUploaderService imageUploaderService, Map map) {
        this.createEventModelValidator = createEventModelValidator;
        this.eventFactory = eventFactory;
        this.imageUploaderService = imageUploaderService;
        this.map = map;
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
            if (model.bannerImage() != null) {
                var img = imageUploaderService.upload(model.bannerImage());
                eventFactory.create(model.name(), model.description(), model.communityId(), model.location(), model.capacity(), model.scheduledDate(), img.getId());
            } else {
                eventFactory.create(model.name(), model.description(), model.communityId(), model.location(), model.capacity(), model.scheduledDate());
            }
        } catch (SQLException | IOException e) {
            return new ValidationResult(new ValidationError("Internal error: please contact SoundLink Support."));
        }
        return new ValidationResult();
    }

    @Override
    public Optional<EventModel> getEvent(int id) {
        try {
            var eventOpt = eventFactory.get(id);
            if (eventOpt.isPresent()) {
                return Optional.of(map.event(eventOpt.get()));
            }
            return Optional.empty();
        } catch (SQLException e) {
            return Optional.empty();
        }
    }
}
