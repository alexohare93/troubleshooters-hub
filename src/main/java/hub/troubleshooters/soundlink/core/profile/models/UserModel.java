package hub.troubleshooters.soundlink.core.profile.models;

import java.util.Date;

/**
 * Represents a user in the system.
 *
 * <p>This model is used to store and retrieve basic user information such as username, account creation date, and the last login timestamp.</p>
 *
 * @param id         The unique identifier of the user.
 * @param username   The username of the user.
 * @param create     The date when the user account was created.
 * @param LastLogin  The date and time of the user's last login.
 */
public record UserModel(int id, String username, Date create, Date LastLogin) {
}
