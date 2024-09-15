package hub.troubleshooters.soundlink.core.events.models;

import java.util.Date;

public record CreateEventModel(String name, String description, Date publishDate, int communityId) {
}
