package hub.troubleshooters.soundlink.core.events.models;

import java.util.Date;

/**
 * Represents the search parameters for filtering events.
 *
 * @param textSearch The text search query to filter event names or descriptions.
 * @param fromDate The start date for filtering events.
 * @param toDate The end date for filtering events.
 * @param capacity The capacity filter for events.
 * @param communityId The ID of the community to filter events by.
 */
public record SearchEventModel(String textSearch, Date fromDate, Date toDate, int capacity, int communityId) {
}