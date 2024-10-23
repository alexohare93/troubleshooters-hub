package hub.troubleshooters.soundlink.data.models;

import java.util.Date;

/**
 * Represents a member of a community, containing information about the user,
 * the community they belong to, and the member's permissions.
 */
public class CommunityMember {
    private int id;
    private int communityId;
    private int userId;
    private final Date created;
    private int permission;

    /**
     * Constructs a new {@code CommunityMember} object.
     *
     * @param id The unique ID of the community member.
     * @param communityId The ID of the community the user belongs to.
     * @param userId The ID of the user who is a member of the community.
     * @param created The date when the user joined the community.
     * @param permission The permission level of the user within the community.
     */
    public CommunityMember(int id, int communityId, int userId, Date created, int permission) {
        this.id = id;
        this.communityId = communityId;
        this.userId = userId;
        this.created = created;
        this.permission = permission;
    }

    /**
     * Gets the unique ID of the community member.
     *
     * @return The member ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique ID of the community member.
     *
     * @param id The new member ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of the community the user belongs to.
     *
     * @return The community ID.
     */
    public int getCommunityId() {
        return communityId;
    }

    /**
     * Sets the ID of the community the user belongs to.
     *
     * @param communityId The new community ID.
     */
    public void setCommunityId(int communityId) {
        this.communityId = communityId;
    }

    /**
     * Gets the ID of the user who is a member of the community.
     *
     * @return The user ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who is a member of the community.
     *
     * @param userId The new user ID.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the permission level of the user within the community.
     *
     * @return The permission level.
     */
    public int getPermission() {
        return permission;
    }

    /**
     * Sets the permission level of the user within the community.
     *
     * @param permission The new permission level.
     */
    public void setPermission(int permission) {
        this.permission = permission;
    }

    /**
     * Gets the date when the user joined the community.
     *
     * @return The date the user joined the community.
     */
    public Date getCreated() {
        return created;
    }
}