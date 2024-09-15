package hub.troubleshooters.soundlink.core.events.models;

import java.util.Date;

public record CreateEventModel(String name, String description, Date scheduledDate, String location, int capacity, int communityId) {
}
