package hub.troubleshooters.soundlink.core.communities.models;

import java.io.File;
import java.util.Date;

/**
 * Represents a model used for creating a new community, containing details such as the community's name,
 * description, genre, creation date, banner image, and privacy status.
 *
 * @param id The unique ID of the community.
 * @param name The name of the community.
 * @param description A description of the community.
 * @param genre The genre associated with the community.
 * @param created The date when the community was created.
 * @param bannerImage A {@link File} representing the banner image for the community.
 * @param isPrivate Indicates whether the community is private ({@code true}) or public ({@code false}).
 */
public record CreateCommunityModel(int id, String name, String description, String genre, Date created, File bannerImage, Boolean isPrivate) {
}
