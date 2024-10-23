package hub.troubleshooters.soundlink.core.events.services;

import hub.troubleshooters.soundlink.core.events.models.CreateEventModel;
import hub.troubleshooters.soundlink.core.events.models.EventModel;
import hub.troubleshooters.soundlink.core.events.validation.EventBookingResult;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.models.Event;

import java.util.Optional;
import hub.troubleshooters.soundlink.core.events.models.SearchEventModel;
import hub.troubleshooters.soundlink.data.models.EventComment;

import java.util.List;
import java.sql.SQLException;

/**
 * Interface for event-related CRUD operations. This service handles the creation, retrieval, updating,
 * and deletion of events, as well as event bookings, searching, and event comments.
 */
public interface EventService {

    /**
     * Creates a new event based on the provided model.
     *
     * @param model The {@link CreateEventModel} containing the event details.
     * @return A {@link ValidationResult} indicating the success or failure of the event creation.
     */
    ValidationResult createEvent(CreateEventModel model);

    /**
     * Retrieves the event with the specified ID.
     *
     * @param id The ID of the event.
     * @return An {@link Optional} containing the {@link EventModel} if found, or empty if not found.
     */
    Optional<EventModel> getEvent(int id);

    /**
     * Retrieves the events for the communities that the specified user is a member of.
     *
     * @param userId The ID of the user.
     * @return A list of {@link Event} objects for the user's communities.
     * @throws SQLException If there is an error while retrieving the events.
     */
    List<Event> getUserCommunityEvents(int userId) throws SQLException;

    /**
     * Retrieves public community events for the specified user.
     *
     * @param userId The ID of the user.
     * @return A list of public {@link Event} objects for the user's communities.
     * @throws SQLException If there is an error while retrieving the events.
     */
    List<Event> getPublicCommunityEvents(int userId) throws SQLException;

    /**
     * Searches for events based on the provided search model.
     *
     * @param searchModel The {@link SearchEventModel} containing the search criteria.
     * @return A list of {@link Event} objects that match the search criteria.
     * @throws SQLException If there is an error during the search.
     */
    List<Event> search(SearchEventModel searchModel) throws SQLException;

    /**
     * Lists the upcoming events for the specified user.
     *
     * @param userId The ID of the user.
     * @return A list of upcoming {@link Event} objects for the user.
     * @throws SQLException If there is an error while retrieving the events.
     */
    List<Event> listUpcomingEvents(int userId) throws SQLException;

    /**
     * Books an event for the specified user.
     *
     * @param eventId The ID of the event to book.
     * @param userId The ID of the user.
     * @return An {@link EventBookingResult} indicating the success or failure of the booking.
     * @throws SQLException If there is an error during the booking process.
     */
    EventBookingResult bookEvent(int eventId, int userId) throws SQLException;

    /**
     * Checks if the specified user is already booked for an event.
     *
     * @param eventId The ID of the event.
     * @param userId The ID of the user.
     * @return {@code true} if the user is booked for the event, {@code false} otherwise.
     * @throws SQLException If there is an error while checking the booking.
     */
    boolean isBooked(int eventId, int userId) throws SQLException;

    /**
     * Retrieves all events for a specified community.
     *
     * @param communityId The ID of the community.
     * @return A list of {@link EventModel} objects for the community.
     * @throws SQLException If there is an error while retrieving the events.
     */
    List<EventModel> getCommunityEvents(int communityId) throws SQLException;

    /**
     * Retrieves the comments for a specific event.
     *
     * @param eventId The ID of the event.
     * @return A list of {@link EventComment} objects for the event.
     * @throws SQLException If there is an error while retrieving the comments.
     */
    List<EventComment> getComments(int eventId) throws SQLException;

    /**
     * Adds a comment to an event by the specified user.
     *
     * @param eventId The ID of the event.
     * @param userId The ID of the user making the comment.
     * @param comment The text of the comment.
     * @throws SQLException If there is an error while adding the comment.
     */
    void comment(int eventId, int userId, String comment) throws SQLException;

    /**
     * Cancels a user's booking for a specific event.
     *
     * @param userId The ID of the user.
     * @param eventId The ID of the event.
     * @return {@code true} if the booking was successfully canceled, {@code false} otherwise.
     * @throws SQLException If there is an error while canceling the booking.
     */
    boolean cancelBooking(int userId, int eventId) throws SQLException;

    /**
     * Updates an existing event with new details.
     *
     * @param event The {@link EventModel} containing the updated event details.
     * @throws SQLException If there is an error while updating the event.
     */
    void updateEvent(EventModel event) throws SQLException;

    /**
     * Retrieves the permission level of a user in a community.
     *
     * @param userId The ID of the user.
     * @param communityId The ID of the community.
     * @return An {@link Optional} containing the permission level, or empty if not found.
     * @throws SQLException If there is an error while retrieving the permission level.
     */
    Optional<Integer> getUserPermissionLevel(int userId, int communityId) throws SQLException;

    /**
     * Deletes an event from a community, if the user has permission.
     *
     * @param communityId The ID of the community.
     * @param userId The ID of the user requesting the deletion.
     * @throws SQLException If there is an error during the deletion process.
     */
    void deleteEvent(int communityId, int userId) throws SQLException;

    /**
     * Checks if the specified user is an admin for a specific event.
     *
     * @param userId The ID of the user.
     * @param eventId The ID of the event.
     * @return {@code true} if the user is an admin for the event, {@code false} otherwise.
     * @throws SQLException If there is an error while checking admin privileges.
     */
    boolean isAdmin(int userId, int eventId) throws SQLException;

    /**
     * Retrieves the total number of bookings for a specific event.
     *
     * @param eventId The ID of the event.
     * @return The number of bookings for the event.
     * @throws SQLException If there is an error while retrieving the booking count.
     */
    int getBookingCountForEvent(int eventId) throws SQLException;

    /**
     * Retrieves the display name of a user by their user ID.
     *
     * @param userId The ID of the user.
     * @return The display name of the user.
     * @throws SQLException If there is an error while retrieving the display name.
     */
    String getDisplayNameById(int userId) throws SQLException;
}
