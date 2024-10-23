package hub.troubleshooters.soundlink.core.auth.services;

import java.util.Arrays;
import java.util.List;

/**
 * Service class responsible for handling event-related operations.
 * This class provides methods for retrieving event information for users.
 */
public class EventService {

    /**
     * Retrieves a list of upcoming events for a given user.
     * <p>
     * This is a placeholder method that currently returns a static list of events.
     * </p>
     *
     * @param userId The ID of the user for whom to retrieve events.
     * @return A list of event descriptions associated with the specified user.
     */
    public List<String> getEventsForUser(Long userId) {
        return Arrays.asList(
                "Community Meetup - 23 October 2024",
                "Music Festival - 12 November 2024",
                "Charity Concert - 5 December 2024"
        );
    }
}