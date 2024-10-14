package hub.troubleshooters.soundlink.data.models;

import java.util.Optional;

public class UserProfile {
    private int id;
    private int userId;
    private String displayName;
    private String bio;
    private Integer profileImageId;

    public UserProfile(int id, int userId, String displayName, String bio, Integer profileImageId) {
        this.id = id;
        this.userId = userId;
        this.displayName = displayName;
        this.bio = bio;
        this.profileImageId = profileImageId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Optional<Integer> getProfileImageId() {
        return profileImageId == null ? Optional.empty() : Optional.of(profileImageId);
    }

    public void setProfileImageId(Integer profileImageId) {
        this.profileImageId = profileImageId;
    }
}
