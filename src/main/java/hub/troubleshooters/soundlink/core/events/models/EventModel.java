package hub.troubleshooters.soundlink.core.events.models;

import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.data.models.Image;

import java.util.Date;
import java.util.Optional;

/**
 * Represents the data model for an event, containing information such as the event's ID, name, description,
 * associated community, venue, capacity, scheduled date, creation date, and an optional banner image.
 *
 * @param id The unique identifier of the event.
 * @param name The name of the event.
 * @param description A brief description of the event.
 * @param community The {@link Community} that the event is associated with.
 * @param venue The venue where the event will be held.
 * @param capacity The maximum number of attendees allowed for the event.
 * @param scheduled The date when the event is scheduled to take place.
 * @param created The date when the event was created.
 * @param bannerImage An {@link Optional} containing the event's banner image, if one is provided.
 */
public record EventModel(int id, String name, String description, Community community, String venue, int capacity, Date scheduled, Date created, Optional<Image> bannerImage) {
}
