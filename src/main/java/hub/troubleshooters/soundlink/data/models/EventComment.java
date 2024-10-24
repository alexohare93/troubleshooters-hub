package hub.troubleshooters.soundlink.data.models;

import java.util.Date;

/**
 * Represents a comment made on an event within the system.
 * Contains information about the comment such as its ID, the event it belongs to, the user who made the comment,
 * the content of the comment, and the date it was created.
 */
public class EventComment {
	private final int id;
	private final int eventId;
	private final int userId;
	private String content;
	private final Date created;

	/**
	 * Constructs a new {@code EventComment} with the specified attributes.
	 *
	 * @param id The unique ID of the comment.
	 * @param eventId The ID of the event the comment is associated with.
	 * @param userId The ID of the user who made the comment.
	 * @param content The text content of the comment.
	 * @param created The date the comment was created.
	 */
	public EventComment(int id, int eventId, int userId, String content, Date created) {
		this.id = id;
		this.eventId = eventId;
		this.userId = userId;
		this.content = content;
		this.created = created;
	}

	/**
	 * Gets the unique ID of the comment.
	 *
	 * @return The ID of the comment.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the ID of the event this comment is associated with.
	 *
	 * @return The event ID.
	 */
	public int getEventId() {
		return eventId;
	}

	/**
	 * Gets the ID of the user who made the comment.
	 *
	 * @return The user ID.
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Gets the content of the comment.
	 *
	 * @return The text content of the comment.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the content of the comment.
	 *
	 * @param content The text content to set.
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Gets the date the comment was created.
	 *
	 * @return The creation date of the comment.
	 */
	public Date getCreated() {
		return created;
	}
}