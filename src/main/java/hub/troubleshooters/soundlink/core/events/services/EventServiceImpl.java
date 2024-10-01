package hub.troubleshooters.soundlink.core.events.services;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.events.models.CreateEventModel;
import hub.troubleshooters.soundlink.core.events.validation.CreateEventModelValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.factories.EventFactory;
import hub.troubleshooters.soundlink.data.factories.SearchEventFactory;

import java.sql.SQLException;

public class EventServiceImpl implements EventService {

    private final CreateEventModelValidator createEventModelValidator;
    private final EventFactory eventFactory;

    @Inject
    public EventServiceImpl(CreateEventModelValidator createEventModelValidator, EventFactory eventFactory) {
        this.createEventModelValidator = createEventModelValidator;
        this.eventFactory = eventFactory;
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
            eventFactory.create(model.name(), model.description(), model.communityId(), model.location(), model.capacity(), model.scheduledDate());
        } catch (SQLException e) {
            return new ValidationResult(new ValidationError("Internal error: please contact SoundLink Support."));
        }
        return new ValidationResult();
    }

    @Override
    public List<Event> getUserCommunityEvents(int userId) throws SQLException {
        // Implementation to fetch events where the user is a member
        return eventFactory.findUserCommunityEvents(userId);
    }

    @Override
    public List<Event> getPublicCommunityEvents(int userId) throws SQLException {
        // Implementation to fetch public events where the user is not a member
        return eventFactory.findPublicCommunityEvents(userId);
    }

    @Override
    public List<Event> search(SearchEventModel searchModel) throws SQLException {
        // fetch all events from the database
        List<Event> allEvents = eventFactory.getAllEvents();

        // apply filtering logic in-memory
        return allEvents.stream()
                .filter(event -> 
                    (searchModel.name() == null || event.getName().contains(searchModel.name())) &&
                    (searchModel.description() == null || event.getDescription().contains(searchModel.description())) &&
                    (searchModel.scheduledDate() == null || event.getScheduledDate().equals(searchModel.scheduledDate())) &&
                    (searchModel.Venue() == null || event.getVenue().contains(searchModel.Venue())) &&
                    (searchModel.capacity() == 0 || event.getCapacity() == searchModel.capacity()) &&
                    (searchModel.communityId() == 0 || event.getCommunityId() == searchModel.communityId())
                )
                .collect(Collectors.toList());
    }
}
