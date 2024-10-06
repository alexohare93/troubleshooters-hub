package hub.troubleshooters.soundlink.core.events.models;

import java.util.Date;

public record CreateCommunityModel(int id, String name, String description, String genre, Date created) {
}
