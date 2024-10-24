package hub.troubleshooters.soundlink.data.models;

import java.util.Date;

/**
 * Represents a user in the system, containing information such as username, password, and timestamps for when the account was created and last logged in.
 */
public class User {
    private final int id;
    private String username;
    private String hashedPassword;
    private Date created;
    private Date lastLogin;

    /**
     * Constructor for creating a user object with only an ID.
     * This constructor should only be used for testing purposes.
     *
     * @param id The unique ID of the user.
     */
    public User(int id) {
        this.id = id;
    }

    /**
     * Constructs a new {@code User} with the specified attributes.
     *
     * @param id The unique ID of the user.
     * @param username The username of the user.
     * @param hashedPassword The hashed password of the user.
     * @param created The date the user account was created.
     * @param lastLogin The date the user last logged in.
     */
    public User(int id, String username, String hashedPassword, Date created, Date lastLogin) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.created = created;
        this.lastLogin = lastLogin;
    }

    /**
     * Gets the unique ID of the user.
     *
     * @return The ID of the user.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the username of the user.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the hashed password of the user.
     *
     * @return The hashed password of the user.
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * Sets the hashed password of the user.
     *
     * @param hashedPassword The hashed password to set.
     */
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    /**
     * Gets the date the user account was created.
     *
     * @return The creation date of the user account.
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Gets the date the user last logged in.
     *
     * @return The last login date of the user.
     */
    public Date getLastLogin() {
        return lastLogin;
    }

    /**
     * Sets the date the user last logged in.
     *
     * @param lastLogin The last login date to set.
     */
    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
}