package hub.troubleshooters.soundlink.core.profile.models;

import java.io.File;

/**
 * Represents the data required to update a user profile.
 *
 * <p>This model contains fields for updating a user's display name, bio, and profile image. It is
 * used in operations that modify a user's profile.</p>
 *
 * @param id           The ID of the user profile being updated.
 * @param displayName  The new display name for the user profile. This field is mandatory and will be validated.
 * @param bio          The new bio for the user profile. This field is optional and may be empty.
 * @param profileImage The new profile image for the user, represented as a {@link File}. This field is optional.
 */
public record UserProfileUpdateModel(int id, String displayName, String bio, File profileImage) {
}
