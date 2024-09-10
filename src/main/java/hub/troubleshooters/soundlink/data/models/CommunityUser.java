package hub.troubleshooters.soundlink.data.models;

import java.util.Date;

public class CommunityUser {
    private int id;
    private int communityId;
    private int userId;
    private Date created;
    private int permission;

    public CommunityUser(int id, int communityId, int userId, Date created, int permission) {
        this.id = id;
        this.communityId = communityId;
        this.userId = userId;
        this.created = created;
        this.permission = permission;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommunityId() {
        return communityId;
    }

    public void setCommunityId(int communityId) {
        this.communityId = communityId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
