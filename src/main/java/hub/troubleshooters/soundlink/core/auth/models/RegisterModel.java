package hub.troubleshooters.soundlink.core.auth.models;

/**
 * Represents the data model used for user registration, containing the username and password.
 *
 * @param username The username chosen by the user during registration.
 * @param password The password chosen by the user during registration.
 */
public record RegisterModel(String username, String password) {
}
