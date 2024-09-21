package hub.troubleshooters.soundlink.core.events.models;

import java.util.Date;

public record SearchEventModel(String name, String description, Date scheduledDate, String Venue, int capacity, int communityId) {
}
