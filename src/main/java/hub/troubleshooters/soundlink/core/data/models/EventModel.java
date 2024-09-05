package hub.troubleshooters.soundlink.core.data.models;

import java.util.Date;

/**
 * Represents a user in the system. Does not include the password.
 * @param id The event's unique identifier.
 * @param communityId The id of the community the event belongs to.
 * @param name The name of the event.
 * @param description The description of the event.
 * @param venue The venue the event is to be held at.
 * @param capacity The capacity of the venue for this event.
 * @param scheduled The datetime the event is scheduled for.
 * @param created the datetime the event object was created
 */
public record EventModel (int id, int communityId, String name, String description, String venue, int capacity, Date scheduled, Date created) {}
