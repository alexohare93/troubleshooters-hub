package hub.troubleshooters.soundlink.core.events.services;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.Map;
import hub.troubleshooters.soundlink.core.auth.Scope;
import hub.troubleshooters.soundlink.core.auth.ScopeUtils;
import hub.troubleshooters.soundlink.core.communities.models.CommunityModel;
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
import hub.troubleshooters.soundlink.data.models.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import hub.troubleshooters.soundlink.core.events.models.SearchEventModel;

import javax.sound.midi.SysexMessage;
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
        return eventFactory.findUserCommunityEvents(userId);
    }

    @Override
    public List<Event> getPublicCommunityEvents(int userId) throws SQLException {
        return eventFactory.findPublicCommunityEvents(userId);
    }

    @Override
    public List<Event> search(SearchEventModel searchModel) throws SQLException {

        List<Event> allEvents = eventFactory.getAllEvents();

        return allEvents.stream()
                .filter(event -> {
                    var text = searchModel.textSearch().toLowerCase();
                    return (
                            event.getName().toLowerCase().contains(text) ||
                            event.getDescription().toLowerCase().contains(text) ||
                            event.getVenue().toLowerCase().contains(text)
                    ) &&

                            (searchModel.fromDate() == null || !event.getScheduled().before(searchModel.fromDate())) &&
                            (searchModel.toDate() == null || !event.getScheduled().after(searchModel.toDate())) &&
                            (searchModel.capacity() == 0 || event.getCapacity() == searchModel.capacity()) &&
                            (searchModel.communityId() == 0 || event.getCommunityId() == searchModel.communityId());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> listUpcomingEvents(int userId) throws SQLException {


        List<Event> userCommunityEvents = eventFactory.findUserCommunityEvents(userId);

        List<Event> publicEvents = eventFactory.findPublicCommunityEvents(userId);

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
    }

    @Override
    public List<EventComment> getComments(int eventId) throws SQLException {
        return eventCommentFactory.getByEventId(eventId);
    }

    @Override
    public void comment(int eventId, int userId, String comment) throws SQLException {
        eventCommentFactory.create(eventId, userId, comment);
    }

    @Override
    public boolean cancelBooking(int userId, int eventId) throws SQLException {
        Optional<Booking> existingBooking = bookingFactory.get(eventId, userId);

        if (existingBooking.isPresent()) {
            try {
                bookingFactory.delete(userId, eventId);
                return true;
            } catch (SQLException e) {
                throw new SQLException("Error removing user from the booked Event.", e);
            }
        } else {
            return false;
        }
    }

    @Override
    public void updateEvent(EventModel event) throws SQLException {
        try {
            Integer bannerImageId = event.bannerImage().map(img -> img.getId()).orElse(null);

            int communityId = event.community().getId();

            Event updatedEvent = new Event(
                    event.id(),
                    communityId,
                    event.name(),
                    event.description(),
                    event.venue(),
                    event.capacity(),
                    event.scheduled(),
                    event.created(),
                    bannerImageId
            );
            eventFactory.save(updatedEvent);

        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public Optional<Integer> getUserPermissionLevel(int userId, int eventId) throws SQLException {
        Optional<Booking> booking = bookingFactory.get(eventId, userId);
        return booking.map(Booking::getPermission).or(() -> Optional.of(0)); // 0 is ready-only
    }

    @Override
    public boolean isAdmin(int userId, int eventId) throws SQLException {
        Optional<Integer> permissionLevel = getUserPermissionLevel(userId, eventId);
        return permissionLevel.map(level -> level == 1).orElse(false);
    }

    @Override
    public void deleteEvent(int eventId, int userId) throws SQLException {
        if (!isAdmin(userId, eventId)) {
            throw new SecurityException("Only admins can delete events.");
        }
        try {
            Optional<Event> eventOpt = eventFactory.get(eventId);

            if (eventOpt.isEmpty()) {
                throw new SQLException("Event with ID " + eventId + " not found.");
            }
            eventFactory.delete(eventOpt.get());
        } catch (SQLException e) {
            throw e;
        }
    }
}

