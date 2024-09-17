package hub.troubleshooters.soundlink.data.models;

import java.util.Date;


public class EventAttendee {
	private final int id;
	private final int eventId;
	private final int userId;
	private Date created;
	private int permission;

	public EventAttendee(int id, int eventId, int userId, Date created, int permission) {
		this.id = id;
		this.eventId = eventId;
		this.userId = userId;
		this.created = created;
		this.permission = permission;
	}

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public int getEventId() {
		return eventId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}
}