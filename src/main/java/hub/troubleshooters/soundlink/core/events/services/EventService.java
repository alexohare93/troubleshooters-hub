package hub.troubleshooters.soundlink.core.events.services;

import hub.troubleshooters.soundlink.core.events.models.CreateEventModel;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.core.events.models.SearchEventModel;
import hub.troubleshooters.soundlink.data.models.Event;

import java.util.List;
import java.sql.SQLException;

/**
 * Contains all the methods involved in event CRUD operations
 */
public interface EventService {
    ValidationResult createEvent(CreateEventModel model);

    List<Event> getUserCommunityEvents(int userId) throws SQLException;
    
    List<Event> getPublicCommunityEvents(int userId) throws SQLException;

    List<Event> search(SearchEventModel searchModel) throws SQLException;

    List<Event> listUpcomingEvents(int userId) throws SQLException;

    boolean signUpForEvent(int eventId, int userId) throws SQLException;
}

