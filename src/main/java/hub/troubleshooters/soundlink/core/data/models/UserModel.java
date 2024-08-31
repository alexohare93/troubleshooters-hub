package hub.troubleshooters.soundlink.core.data.models;

import java.util.Date;

/**
 * Represents a user in the system. Does not include the password.
 * @param id The user's unique identifier.
 * @param username The user's username.
 * @param created The date the user was created.
 * @param lastLogin The date the user last logged in.
 * @param permission The user's permissions stored as a bit field.
 */
public record UserModel(int id, String username, Date created, Date lastLogin, int permission) { }
