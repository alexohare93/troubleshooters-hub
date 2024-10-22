package hub.troubleshooters.soundlink.core.auth.services;

import java.util.Arrays;
import java.util.List;

public class EventService {
    public List<String> getEventsForUser(Long userId) {
        return Arrays.asList(
                "Community Meetup - 23 October 2024",
                "Music Festival - 12 November 2024",
                "Charity Concert - 5 December 2024"
        );
    }
}
