package hub.troubleshooters.soundlink.data.models;

import java.util.Date;

/**
 * Represents a booking for an event. A booking links a user to an event and tracks the permission level
 * and creation date of the booking.
 */
public class Booking {
	private final int id;
	private final int eventId;
	private final int userId;
	private final Date created;
	private int permission;

	/**
	 * Constructs a new {@code Booking} object.
	 *
	 * @param id The unique ID of the booking.
	 * @param eventId The ID of the event that the booking is associated with.
	 * @param userId The ID of the user making the booking.
	 * @param created The date when the booking was created.
	 * @param permission The permission level associated with the booking.
	 */
	public Booking(int id, int eventId, int userId, Date created, int permission) {
		this.id = id;
		this.eventId = eventId;
		this.userId = userId;
		this.created = created;
		this.permission = permission;
	}

	/**
	 * Gets the unique ID of the booking.
	 *
	 * @return The booking ID.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the ID of the user who made the booking.
	 *
	 * @return The user ID.
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Gets the ID of the event associated with the booking.
	 *
	 * @return The event ID.
	 */
	public int getEventId() {
		return eventId;
	}

	/**
	 * Gets the date when the booking was created.
	 *
	 * @return The creation date of the booking.
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * Gets the permission level associated with the booking.
	 *
	 * @return The permission level.
	 */
	public int getPermission() {
		return permission;
	}

	/**
	 * Sets the permission level for the booking.
	 *
	 * @param permission The new permission level.
	 */
	public void setPermission(int permission) {
		this.permission = permission;
	}
}