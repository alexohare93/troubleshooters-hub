package hub.troubleshooters.soundlink.core.communities.models;

import hub.troubleshooters.soundlink.data.models.Image;

import java.util.Date;
import java.util.Optional;

public record CommunityModel(int communityId, String name, String description, String genre, Date created, Optional<Image> bannerImage, Boolean isPrivate) {
}
