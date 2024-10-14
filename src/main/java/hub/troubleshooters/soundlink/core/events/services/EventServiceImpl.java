package hub.troubleshooters.soundlink.core.events.services;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.Map;
import hub.troubleshooters.soundlink.core.auth.Scope;
import hub.troubleshooters.soundlink.core.auth.ScopeUtils;
import hub.troubleshooters.soundlink.core.events.models.CreateEventModel;
import hub.troubleshooters.soundlink.core.events.models.EventModel;
import hub.troubleshooters.soundlink.core.events.validation.BookingAlreadyExistsException;
import hub.troubleshooters.soundlink.core.events.validation.CreateEventModelValidator;
import hub.troubleshooters.soundlink.core.events.validation.EventBookingResult;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.factories.EventCommentFactory;
import hub.troubleshooters.soundlink.data.factories.EventFactory;
import hub.troubleshooters.soundlink.data.factories.BookingFactory;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.data.models.Event;

import java.util.List;
import java.util.stream.Collectors;

import hub.troubleshooters.soundlink.core.events.models.SearchEventModel;
import hub.troubleshooters.soundlink.data.models.EventComment;

import java.sql.SQLException;
import java.util.Optional;
import java.io.IOException;


public class EventServiceImpl implements EventService {

    private final CreateEventModelValidator createEventModelValidator;
    private final EventFactory eventFactory;
    private final IdentityService identityService;
    private final BookingFactory bookingFactory;
    private final ImageUploaderService imageUploaderService;
    private final Map map;
    private final EventCommentFactory eventCommentFactory;

    @Inject
    public EventServiceImpl(
            CreateEventModelValidator createEventModelValidator,
            EventFactory eventFactory,
            IdentityService identityService,
            BookingFactory bookingFactory,
            ImageUploaderService imageUploaderService,
            Map map,
            EventCommentFactory eventCommentFactory

    ) {
        this.createEventModelValidator = createEventModelValidator;
        this.eventFactory = eventFactory;
        this.identityService = identityService;
        this.bookingFactory = bookingFactory;
        this.imageUploaderService = imageUploaderService;
        this.map = map;
        this.eventCommentFactory = eventCommentFactory;
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
                .filter(event -> {
                    // Combine text search for name, description, and venue
                    var text = searchModel.textSearch().toLowerCase();
                    return (
                            event.getName().toLowerCase().contains(text) ||
                            event.getDescription().toLowerCase().contains(text) ||
                            event.getVenue().toLowerCase().contains(text)
                    ) &&
                            // Filter by 'fromDate' and 'toDate' range if provided
                            (searchModel.fromDate() == null || !event.getScheduled().before(searchModel.fromDate())) &&
                            (searchModel.toDate() == null || !event.getScheduled().after(searchModel.toDate())) &&

                            // Filter by capacity if provided
                            (searchModel.capacity() == 0 || event.getCapacity() == searchModel.capacity()) &&

                            // Filter by communityId if provided
                            (searchModel.communityId() == 0 || event.getCommunityId() == searchModel.communityId());
                })
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
    public EventBookingResult bookEvent(int eventId, int userId) throws SQLException {
        if (isBooked(eventId, userId)) {
            return new EventBookingResult(new BookingAlreadyExistsException(eventId, userId));
        }

        bookingFactory.create(eventId, userId, ScopeUtils.combineScopes(Scope.EVENT_READ));
        var booking = bookingFactory.get(eventId, userId).get();  // unwrap should never fail since we've just inserted the record.
        return new EventBookingResult(booking);
    }

    @Override
    public boolean isBooked(int eventId, int userId) throws SQLException {
       var booking = bookingFactory.get(eventId, userId);
       return booking.isPresent();
    }

    @Override
    public List<EventModel> getCommunityEvents(int communityId) throws SQLException {
        List<Event> events = eventFactory.findCommunityEvents(communityId);
        List<EventModel> eventModels = new ArrayList<>();
        for (Event event : events) eventModels.add(map.event(event));
        return eventModels;

    public List<EventComment> getComments(int eventId) throws SQLException {
        return eventCommentFactory.getByEventId(eventId);
    }

    @Override
    public void comment(int eventId, int userId, String comment) throws SQLException {
        eventCommentFactory.create(eventId, userId, comment);
    }
}

