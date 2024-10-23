package hub.troubleshooters.soundlink.core.events.models;

import java.io.File;
import java.util.Date;

/**
 * Represents the data model used for creating a new event, containing details such as the event's name,
 * description, scheduled date, location, capacity, associated community, and an optional banner image.
 *
 * @param name The name of the event.
 * @param description A brief description of the event.
 * @param scheduledDate The date when the event is scheduled to take place.
 * @param location The location where the event will be held.
 * @param capacity The maximum number of attendees allowed for the event.
 * @param communityId The ID of the community that the event is associated with.
 * @param bannerImage A {@link File} representing an optional banner image for the event.
 */
public record CreateEventModel(String name, String description, Date scheduledDate, String location, int capacity, int communityId, File bannerImage) {
}
