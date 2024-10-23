package hub.troubleshooters.soundlink.core.communities.models;

import hub.troubleshooters.soundlink.core.profile.models.UserModel;

import java.util.Date;

/**
 * Represents a data model for a community post, containing information about the post's ID,
 * the community it belongs to, the user who created it, the post's title, content, and creation date.
 *
 * @param id The unique ID of the community post.
 * @param community The {@link CommunityModel} object representing the community the post belongs to.
 * @param user The {@link UserModel} object representing the user who created the post.
 * @param title The title of the community post.
 * @param content The content of the community post.
 * @param created The date when the post was created.
 */
public record CommunityPostModel(int id, CommunityModel community, UserModel user, String title, String content, Date created) {
}
