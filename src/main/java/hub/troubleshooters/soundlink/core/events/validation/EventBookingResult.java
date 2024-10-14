package hub.troubleshooters.soundlink.core.events.validation;

import hub.troubleshooters.soundlink.core.CoreResult;
import hub.troubleshooters.soundlink.data.models.Booking;

public class EventBookingResult extends CoreResult<Booking, BookingAlreadyExistsException> {

    public EventBookingResult(Booking booking) {
        super(booking);
    }

    public EventBookingResult(BookingAlreadyExistsException error) {
        super(error);
    }
}
