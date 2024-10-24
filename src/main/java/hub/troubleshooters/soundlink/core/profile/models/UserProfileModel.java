package hub.troubleshooters.soundlink.core.profile.models;

import hub.troubleshooters.soundlink.data.models.Image;

import java.util.Date;
import java.util.Optional;

/**
 * Represents a user's profile in the system.
 *
 * <p>This model is used to retrieve and display user profile information, including the user's display name, bio, and an optional profile image.</p>
 *
 * @param id           The unique identifier of the user profile.
 * @param userId       The ID of the user associated with the profile.
 * @param displayName  The display name of the user. This is the name that is publicly visible.
 * @param bio          The biography or personal description of the user.
 * @param profileImage An optional {@link Image} representing the user's profile picture. If no image is provided, this will be empty.
 */
public record UserProfileModel(int id, int userId, String displayName, String bio, Optional<Image> profileImage) {
}
