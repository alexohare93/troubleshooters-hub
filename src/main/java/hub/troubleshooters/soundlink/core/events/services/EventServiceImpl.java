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
import hub.troubleshooters.soundlink.data.factories.EventAttendeeFactory;
import hub.troubleshooters.soundlink.data.models.EventAttendee;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.data.models.Event;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import hub.troubleshooters.soundlink.core.events.models.SearchEventModel;
import hub.troubleshooters.soundlink.data.factories.ImageFactory;
import hub.troubleshooters.soundlink.data.models.Event;

import java.sql.SQLException;
import java.util.Optional;
import java.io.IOException;


public class EventServiceImpl implements EventService {

    private final CreateEventModelValidator createEventModelValidator;
    private final EventFactory eventFactory;
    private final IdentityService identityService;
    private final EventAttendeeFactory eventAttendeeFactory;
    private final ImageUploaderService imageUploaderService;
    private final Map map;

    @Inject
    public EventServiceImpl(CreateEventModelValidator createEventModelValidator, EventFactory eventFactory, IdentityService identityService, EventAttendeeFactory eventAttendeeFactory, ImageUploaderService imageUploaderService, Map map) {
        this.createEventModelValidator = createEventModelValidator;
        this.eventFactory = eventFactory;
        this.identityService = identityService;
        this.eventAttendeeFactory = eventAttendeeFactory;
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
        // Fetch all events from the database
        List<Event> allEvents = eventFactory.getAllEvents();

        // Apply filtering logic in-memory
        return allEvents.stream()
                .filter(event ->
                        // Combine text search for name, description, and venue
                        (searchModel.textSearch() == null ||
                                event.getName().contains(searchModel.textSearch()) ||
                                event.getDescription().contains(searchModel.textSearch()) ||
                                event.getVenue().contains(searchModel.textSearch())) &&

                                // Filter by 'fromDate' and 'toDate' range if provided
                                (searchModel.fromDate() == null || !event.getScheduled().before(searchModel.fromDate())) &&
                                (searchModel.toDate() == null || !event.getScheduled().after(searchModel.toDate())) &&

                                // Filter by capacity if provided
                                (searchModel.capacity() == 0 || event.getCapacity() == searchModel.capacity()) &&

                                // Filter by communityId if provided
                                (searchModel.communityId() == 0 || event.getCommunityId() == searchModel.communityId())
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> listUpcomingEvents(int userId) throws SQLException {

        // Fetching user community events
        List<Event> userCommunityEvents = eventFactory.findUserCommunityEvents(userId);

        // Fetching public events
        List<Event> publicEvents = eventFactory.findPublicCommunityEvents(userId);

        // Combine both lists
        userCommunityEvents.addAll(publicEvents);

        return userCommunityEvents;
    }

    @Override
    public boolean signUpForEvent(int eventId, int userId) throws SQLException {
        // Check if the user is already signed up for the event
        Optional<EventAttendee> existingAttendee = eventAttendeeFactory.get(eventId, userId);

        if (existingAttendee.isPresent()) {
            return false;  // User already signed up
        } else {
            int permission = 6;  // Permission for comment ability
            eventAttendeeFactory.create(eventId, userId, permission);
            return true;  // Successful sign-up
        }
    }
}

