package hub.troubleshooters.soundlink.core.communities.models;

import hub.troubleshooters.soundlink.data.models.Image;

import java.util.Date;
import java.util.Optional;
/**
 * Represents the data model for a community, containing relevant details such as the community's name,
 * description, genre, creation date, and privacy status.
 *
 * @param communityId The unique ID of the community.
 * @param name The name of the community.
 * @param description A description of the community.
 * @param genre The genre associated with the community.
 * @param created The date when the community was created.
 * @param bannerImage An {@link Optional} containing the community's banner image, or {@code Optional.empty()} if no banner exists.
 * @param isPrivate Indicates whether the community is private ({@code true}) or public ({@code false}).
 */
public record CommunityModel(int communityId, String name, String description, String genre, Date created, Optional<Image> bannerImage, Boolean isPrivate) {
}
