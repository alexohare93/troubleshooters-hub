package hub.troubleshooters.soundlink.core.events.services;

import hub.troubleshooters.soundlink.core.events.models.CreateEventModel;
import hub.troubleshooters.soundlink.core.events.models.EventModel;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.models.Event;

import java.util.Optional;

/**
 * Contains all the methods involved in event CRUD operations
 */
public interface EventService {
    ValidationResult createEvent(CreateEventModel model);
    Optional<EventModel> getEvent(int id);
}
