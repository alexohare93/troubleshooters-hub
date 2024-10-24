package hub.troubleshooters.soundlink.core.events.validation;

import hub.troubleshooters.soundlink.core.CoreResult;
import hub.troubleshooters.soundlink.data.models.Booking;

/**
 * Represents the result of an event booking operation.
 *
 * <p>This class extends the {@link CoreResult} class and provides the result of booking an event.
 * It can either contain a successful {@link Booking} or a {@link BookingAlreadyExistsException}
 * in case the booking already exists.</p>
 *
 * @see CoreResult
 * @see Booking
 * @see BookingAlreadyExistsException
 */
public class EventBookingResult extends CoreResult<Booking, BookingAlreadyExistsException> {

    /**
     * Constructs an {@link EventBookingResult} representing a successful event booking.
     *
     * @param booking The booking result of the event.
     */
    public EventBookingResult(Booking booking) {
        super(booking);
    }

    /**
     * Constructs an {@link EventBookingResult} representing a failed event booking due to an existing booking.
     *
     * @param error The exception indicating that the booking already exists.
     */
    public EventBookingResult(BookingAlreadyExistsException error) {
        super(error);
    }
}