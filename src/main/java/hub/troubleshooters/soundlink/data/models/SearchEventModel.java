package hub.troubleshooters.soundlink.core.events.models;

import java.util.Date;

/**
 * Represents the search parameters for filtering events
 */
public record SearchEventModel(String name, String description, Date scheduledDate, String Venue, int capacity, int communityId) {
}
