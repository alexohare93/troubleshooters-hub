package hub.troubleshooters.soundlink.core.events.models;

import java.io.File;
import java.util.Date;

public record CreateCommunityModel(int id, String name, String description, String genre, Date created, File bannerImage) {
}
