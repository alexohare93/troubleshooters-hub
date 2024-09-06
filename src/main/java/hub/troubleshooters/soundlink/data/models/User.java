package hub.troubleshooters.soundlink.data.models;

import java.util.Date;

/**
 * Represents a user in the system.
 */
public class User {
    private final int id;
    private String username;
    private String hashedPassword;
    private Date created;
    private Date lastLogin;

    public User(int id, String username, String hashedPassword, Date created, Date lastLogin) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.created = created;
        this.lastLogin = lastLogin;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
}
