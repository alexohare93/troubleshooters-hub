package hub.troubleshooters.soundlink.core.events.validation;

public class BookingAlreadyExistsException extends RuntimeException {
    public BookingAlreadyExistsException(int eventId, int userId) {
        super("User id " + userId + " is already booked into event id " + eventId);
    }
}
