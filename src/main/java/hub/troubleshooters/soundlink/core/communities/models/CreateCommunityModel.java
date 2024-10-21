package hub.troubleshooters.soundlink.core.communities.models;

import java.io.File;
import java.util.Date;

public record CreateCommunityModel(int id, String name, String description, String genre, Date created, File bannerImage, Boolean isPrivate) {
}
