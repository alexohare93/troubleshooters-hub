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

import java.sql.SQLException;
import java.util.Optional;
import java.io.IOException;

/**
 * Implementation of the {@link EventService} interface, responsible for managing event-related CRUD operations,
 * event bookings, event searches, and user permissions. This class interacts with the data layer to persist event
 * data and manage bookings and event comments.
 */
public class EventServiceImpl implements EventService {

    private final CreateEventModelValidator createEventModelValidator;
    private final EventFactory eventFactory;
    private final IdentityService identityService;
    private final BookingFactory bookingFactory;
    private final ImageUploaderService imageUploaderService;
    private final Map map;
    private final EventCommentFactory eventCommentFactory;

    /**
     * Constructs a new {@code EventServiceImpl} with the necessary dependencies.
     *
     * @param createEventModelValidator Validator for event creation models.
     * @param eventFactory Factory for managing event data.
     * @param identityService Service for managing user identity and permissions.
     * @param bookingFactory Factory for managing event bookings.
     * @param imageUploaderService Service for uploading images.
     * @param map Mapper for converting between model and entity objects.
     * @param eventCommentFactory Factory for managing event comments.
     */
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

    /**
     * Creates a new event based on the provided model and persists it in the database.
     *
     * @param model The {@link CreateEventModel} containing the event details.
     * @return A {@link ValidationResult} indicating success or failure of the event creation.
     */
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

    /**
     * Retrieves an event by its ID.
     *
     * @param id The ID of the event.
     * @return An {@link Optional} containing the {@link EventModel} if found, or empty if not found.
     */
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

    /**
     * Retrieves events for the communities that the specified user is a member of.
     *
     * @param userId The ID of the user.
     * @return A list of {@link Event} objects.
     * @throws SQLException If there is an error while retrieving events.
     */
    public List<Event> getUserCommunityEvents(int userId) throws SQLException {
        return eventFactory.findUserCommunityEvents(userId);
    }

    /**
     * Retrieves public community events for the specified user.
     *
     * @param userId The ID of the user.
     * @return A list of public {@link Event} objects.
     * @throws SQLException If there is an error while retrieving public events.
     */
    @Override
    public List<Event> getPublicCommunityEvents(int userId) throws SQLException {
        return eventFactory.findPublicCommunityEvents(userId);
    }

    /**
     * Searches for events based on the provided search model.
     *
     * @param searchModel The {@link SearchEventModel} containing the search criteria.
     * @return A list of matching {@link Event} objects.
     * @throws SQLException If there is an error during the search.
     */
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

    /**
     * Retrieves upcoming events for a user, including both user community events and public events.
     *
     * @param userId The ID of the user.
     * @return A list of upcoming {@link Event} objects.
     * @throws SQLException If there is an error while retrieving events.
     */
    @Override
    public List<Event> listUpcomingEvents(int userId) throws SQLException {

        List<Event> userCommunityEvents = eventFactory.findUserCommunityEvents(userId);

        List<Event> publicEvents = eventFactory.findPublicCommunityEvents(userId);

        userCommunityEvents.addAll(publicEvents);

        return userCommunityEvents;
    }

    /**
     * Books an event for a user, ensuring that the user is not already booked for the event.
     *
     * @param eventId The ID of the event to book.
     * @param userId The ID of the user booking the event.
     * @return An {@link EventBookingResult} indicating success or failure of the booking.
     * @throws SQLException If there is an error during the booking process.
     */
    @Override
    public EventBookingResult bookEvent(int eventId, int userId) throws SQLException {
        if (isBooked(eventId, userId)) {
            return new EventBookingResult(new BookingAlreadyExistsException(eventId, userId));
        }

        bookingFactory.create(eventId, userId, ScopeUtils.combineScopes(Scope.EVENT_READ));
        var booking = bookingFactory.get(eventId, userId).get();  // unwrap should never fail since we've just inserted the record.
        return new EventBookingResult(booking);
    }

    /**
     * Checks if a user is already booked for a specific event.
     *
     * @param eventId The ID of the event.
     * @param userId The ID of the user.
     * @return {@code true} if the user is booked, {@code false} otherwise.
     * @throws SQLException If there is an error during the booking check.
     */
    @Override
    public boolean isBooked(int eventId, int userId) throws SQLException {
       var booking = bookingFactory.get(eventId, userId);
       return booking.isPresent();
    }

    /**
     * Retrieves a list of events for a specific community.
     *
     * @param communityId The ID of the community.
     * @return A list of {@link EventModel} objects representing the events in the specified community.
     * @throws SQLException If there is an error while retrieving the events.
     */
    @Override
    public List<EventModel> getCommunityEvents(int communityId) throws SQLException {
        List<Event> events = eventFactory.findCommunityEvents(communityId);
        List<EventModel> eventModels = new ArrayList<>();
        for (Event event : events) eventModels.add(map.event(event));
        return eventModels;
    }

    /**
     * Retrieves the comments associated with a specific event.
     *
     * @param eventId The ID of the event.
     * @return A list of {@link EventComment} objects representing the comments for the event.
     * @throws SQLException If there is an error while retrieving the comments.
     */
    @Override
    public List<EventComment> getComments(int eventId) throws SQLException {
        return eventCommentFactory.getByEventId(eventId);
    }

    /**
     * Adds a comment to an event by the specified user.
     *
     * @param eventId The ID of the event.
     * @param userId The ID of the user adding the comment.
     * @param comment The content of the comment.
     * @throws SQLException If there is an error while adding the comment.
     */
    @Override
    public void comment(int eventId, int userId, String comment) throws SQLException {
        eventCommentFactory.create(eventId, userId, comment);
    }

    /**
     * Cancels a booking for the specified user and event.
     *
     * @param userId The ID of the user canceling the booking.
     * @param eventId The ID of the event to cancel.
     * @return {@code true} if the booking was successfully canceled, {@code false} otherwise.
     * @throws SQLException If there is an error while canceling the booking.
     */
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

    /**
     * Updates an event with new details.
     *
     * @param event The {@link EventModel} containing the updated event details.
     * @throws SQLException If there is an error while updating the event.
     */
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

    /**
     * Retrieves the permission level of a user for a specific event.
     *
     * @param userId The ID of the user.
     * @param eventId The ID of the event.
     * @return An {@link Optional} containing the user's permission level, or 0 (read-only) if no permission is found.
     * @throws SQLException If there is an error while retrieving the permission level.
     */
    @Override
    public Optional<Integer> getUserPermissionLevel(int userId, int eventId) throws SQLException {
        Optional<Booking> booking = bookingFactory.get(eventId, userId);
        return booking.map(Booking::getPermission).or(() -> Optional.of(0)); // 0 is ready-only
    }

    /**
     * Checks if a user is an admin for a specific event.
     *
     * @param userId The ID of the user.
     * @param eventId The ID of the event.
     * @return {@code true} if the user is an admin for the event, {@code false} otherwise.
     * @throws SQLException If there is an error while checking the user's admin status.
     */
    @Override
    public boolean isAdmin(int userId, int eventId) throws SQLException {
        Optional<Integer> permissionLevel = getUserPermissionLevel(userId, eventId);
        return permissionLevel.map(level -> level == 1).orElse(false);
    }

    /**
     * Deletes an event, ensuring the user has admin permissions.
     *
     * @param eventId The ID of the event to be deleted.
     * @param userId The ID of the user requesting the deletion.
     * @throws SQLException If there is an error while deleting the event or if the user is not an admin.
     * @throws SecurityException If the user does not have admin privileges for the event.
     */
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

    /**
     * Retrieves the total number of bookings for a specific event.
     *
     * @param eventId The ID of the event.
     * @return The number of bookings for the event.
     * @throws SQLException If there is an error while retrieving the booking count.
     */
    @Override
    public int getBookingCountForEvent(int eventId) throws SQLException {
        return bookingFactory.countBookingsForEvent(eventId);
    }

    /**
     * Retrieves the display name of a user by their user ID.
     *
     * @param userId The ID of the user.
     * @return The display name of the user.
     * @throws SQLException If there is an error while retrieving the display name.
     */
    @Override
    public String getDisplayNameById(int userId) throws SQLException {
        return bookingFactory.getDisplayNameById(userId);
    }
}

