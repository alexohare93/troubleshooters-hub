package hub.troubleshooters.soundlink.data.models;

import java.util.Date;

public class Event {
	private final int id;
	private int communityId;
	private String name;
	private String description;
	private String venue;
	private int capacity;
	private Date scheduled;
	private final Date created;

	public Event(int id, int communityId, String name, String description, String venue, int capacity, Date scheduled, Date created) {
		this.id = id;
		this.communityId = communityId;
		this.name = name;
		this.description = description;
		this.venue = venue;
		this.capacity = capacity;
		this.scheduled = scheduled;
		this.created = created;
	}

	public int getId() { return id; }

	public int getCommunityId() { return communityId; }

	public void setCommunityId(int communityId) { this.communityId = communityId; }

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	public String getDescription() { return description; }

	public void setDescription(String description) { this.description = description; }

	public String getVenue() { return venue; }

	public void setVenue(String venue) { this.venue = venue; }

	public int getCapacity() { return capacity; }

	public void setCapacity(int capacity) { this.capacity = capacity; }

	public Date getScheduled() { return scheduled; }

	public void setScheduled(Date scheduled) { this.scheduled = scheduled; }

	public Date getCreated() { return created; }
}
