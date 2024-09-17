package hub.troubleshooters.soundlink.data.models;

import java.util.Date;

public class EventComment {
	private final int id;
	private final int eventId;
	private final int userId;
	private String content;
	private final Date created;

	public EventComment(int id, int eventId, int userId, String content, Date created) {
		this.id = id;
		this.eventId = eventId;
		this.userId = userId;
		this.content = content;
		this.created = created;
	}

	public int getId() {
		return id;
	}

	public int getEventId() {
		return eventId;
	}

	public int getUserId() {
		return userId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreated() {
		return created;
	}
}
