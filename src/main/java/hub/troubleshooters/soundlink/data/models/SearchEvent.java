package hub.troubleshooters.soundlink.data.models;

import java.util.Date;

public class SearchEvent {
    private final int id;
    private int communityId;
    private String name;
    private String description;
    private String venue;
    private int capacity;
    private Date scheduledDate;

    // Constructor
    public SearchEvent(int id, int communityId, String name, String description, String venue, int capacity, Date scheduledDate) {
        this.id = id;
        this.communityId = communityId;
        this.name = name;
        this.description = description;
        this.venue = venue;
        this.capacity = capacity;
        this.scheduledDate = scheduledDate;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public int getCommunityId() {
        return communityId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getVenue() {
        return venue;
    }

    public int getCapacity() {
        return capacity;
    }
    public Date getScheduledDate() {
        return scheduledDate;
    }
}

