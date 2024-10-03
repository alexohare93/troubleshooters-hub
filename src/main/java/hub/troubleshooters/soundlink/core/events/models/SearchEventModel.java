package hub.troubleshooters.soundlink.core.events.models;

import java.util.Date;

/**
 * Represents the search parameters for filtering events
 */
public record SearchEventModel(String textSearch, Date fromDate, Date toDate, int capacity, int communityId) {
}

