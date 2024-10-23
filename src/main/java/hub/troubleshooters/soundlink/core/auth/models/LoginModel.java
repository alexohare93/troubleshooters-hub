package hub.troubleshooters.soundlink.core.auth.models;

/**
 * Represents the data model used for user login, containing the username and password.
 *
 * @param username The username provided by the user.
 * @param password The password provided by the user.
 */
public record LoginModel(String username, String password) {
}
