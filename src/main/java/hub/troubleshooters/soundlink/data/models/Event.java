package hub.troubleshooters.soundlink.data.models;

import java.util.Date;
import java.util.Optional;
/**
 * Represents an event in the system. This model contains details about an event such as its name, description,
 * venue, capacity, scheduled date, and an optional banner image.
 */
public class Event {
	private final int id;
	private int communityId;
	private String name;
	private String description;
	private String venue;
	private int capacity;
	private Date scheduled;
	private final Date created;
	private Integer bannerImageId;

	/**
	 * Constructs a new {@code Event} with the specified attributes.
	 *
	 * @param id The unique ID of the event.
	 * @param communityId The ID of the community associated with the event.
	 * @param name The name of the event.
	 * @param description A description of the event.
	 * @param venue The venue where the event will take place.
	 * @param capacity The maximum number of attendees for the event.
	 * @param scheduled The date the event is scheduled to occur.
	 * @param created The date the event was created.
	 * @param bannerImageId The ID of the banner image for the event, or null if there is no banner image.
	 */
	public Event(int id, int communityId, String name, String description, String venue, int capacity, Date scheduled, Date created, Integer bannerImageId) {
		this.id = id;
		this.communityId = communityId;
		this.name = name;
		this.description = description;
		this.venue = venue;
		this.capacity = capacity;
		this.scheduled = scheduled;
		this.created = created;
		this.bannerImageId = bannerImageId;
	}

	/**
	 * Gets the unique ID of the event.
	 *
	 * @return The ID of the event.
	 */
	public int getId() { return id; }

	/**
	 * Gets the ID of the community associated with the event.
	 *
	 * @return The community ID.
	 */
	public int getCommunityId() { return communityId; }

	/**
	 * Sets the ID of the community associated with the event.
	 *
	 * @param communityId The community ID to set.
	 */
	public void setCommunityId(int communityId) { this.communityId = communityId; }

	/**
	 * Gets the name of the event.
	 *
	 * @return The name of the event.
	 */
	public String getName() { return name; }

	/**
	 * Sets the name of the event.
	 *
	 * @param name The name to set.
	 */
	public void setName(String name) { this.name = name; }

	/**
	 * Gets the description of the event.
	 *
	 * @return The description of the event.
	 */
	public String getDescription() { return description; }

	/**
	 * Sets the description of the event.
	 *
	 * @param description The description to set.
	 */
	public void setDescription(String description) { this.description = description; }

	/**
	 * Gets the venue where the event will take place.
	 *
	 * @return The venue of the event.
	 */
	public String getVenue() { return venue; }

	/**
	 * Sets the venue where the event will take place.
	 *
	 * @param venue The venue to set.
	 */
	public void setVenue(String venue) { this.venue = venue; }

	/**
	 * Gets the maximum number of attendees for the event.
	 *
	 * @return The capacity of the event.
	 */
	public int getCapacity() { return capacity; }

	/**
	 * Sets the maximum number of attendees for the event.
	 *
	 * @param capacity The capacity to set.
	 */
	public void setCapacity(int capacity) { this.capacity = capacity; }

	/**
	 * Gets the date the event is scheduled to occur.
	 *
	 * @return The scheduled date of the event.
	 */
	public Date getScheduled() { return scheduled; }

	/**
	 * Sets the date the event is scheduled to occur.
	 *
	 * @param scheduled The scheduled date to set.
	 */
	public void setScheduled(Date scheduled) { this.scheduled = scheduled; }

	/**
	 * Gets the date the event was created.
	 *
	 * @return The creation date of the event.
	 */
	public Date getCreated() { return created; }

	/**
	 * Gets the ID of the banner image associated with the event, if available.
	 *
	 * @return An {@code Optional} containing the banner image ID if it exists, or an empty {@code Optional} if no image is set.
	 */
	public Optional<Integer> getBannerImageId() { return bannerImageId == null ? Optional.empty() : Optional.of(bannerImageId); }

	/**
	 * Sets the ID of the banner image associated with the event.
	 *
	 * @param bannerImageId The banner image ID to set, or null if there is no banner image.
	 */
	public void setBannerImageId(Integer bannerImageId) {
		this.bannerImageId = bannerImageId;
	}
}