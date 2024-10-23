package hub.troubleshooters.soundlink.core.events.validation;

/**
 * Exception thrown when a user attempts to book into an event they are already booked into.
 */
public class BookingAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new {@code BookingAlreadyExistsException} with a message indicating that the user
     * is already booked into the specified event.
     *
     * @param eventId The ID of the event the user is attempting to book into.
     * @param userId The ID of the user who is already booked into the event.
     */
    public BookingAlreadyExistsException(int eventId, int userId) {
        super("User id " + userId + " is already booked into event id " + eventId);
    }
}