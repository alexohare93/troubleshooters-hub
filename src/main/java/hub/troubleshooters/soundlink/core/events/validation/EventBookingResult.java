package hub.troubleshooters.soundlink.core.events.validation;

import hub.troubleshooters.soundlink.core.CoreResult;
import hub.troubleshooters.soundlink.data.models.EventAttendee;

public class EventBookingResult extends CoreResult<EventAttendee, BookingAlreadyExistsException> {

    public EventBookingResult(EventAttendee booking) {
        super(booking);
    }

    public EventBookingResult(BookingAlreadyExistsException error) {
        super(error);
    }
}
