package hub.troubleshooters.soundlink.core.events.services;

import hub.troubleshooters.soundlink.core.events.models.CreateEventModel;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.models.SearchEvent;
import hub.troubleshooters.soundlink.data.factories.SearchEventFactory;

import java.util.List;
import java.sql.SQLException;

/**
 * Contains all the methods involved in event CRUD operations
 */
public interface EventService {
    ValidationResult createEvent(CreateEventModel model);

    List<Event> getUserCommunityEvents(int userId) throws SQLException;
    List<Event> getPublicCommunityEvents(int userId) throws SQLException;
}

