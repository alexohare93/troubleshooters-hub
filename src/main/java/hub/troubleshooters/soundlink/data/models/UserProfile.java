package hub.troubleshooters.soundlink.data.models;

import java.util.Optional;

/**
 * Represents a user profile containing personal details such as display name, bio, and profile image.
 * This model is associated with a user account and stores additional information about the user.
 */
public class UserProfile {
    private int id;
    private int userId;
    private String displayName;
    private String bio;
    private Integer profileImageId;

    /**
     * Constructs a new {@code UserProfile} with the specified attributes.
     *
     * @param id The unique ID of the user profile.
     * @param userId The ID of the user associated with this profile.
     * @param displayName The display name of the user.
     * @param bio The user's biography or description.
     * @param profileImageId The ID of the user's profile image, or null if no image is set.
     */
    public UserProfile(int id, int userId, String displayName, String bio, Integer profileImageId) {
        this.id = id;
        this.userId = userId;
        this.displayName = displayName;
        this.bio = bio;
        this.profileImageId = profileImageId;
    }

    /**
     * Gets the unique ID of the user profile.
     *
     * @return The ID of the profile.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique ID of the user profile.
     *
     * @param id The ID to set for the profile.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the user ID associated with this profile.
     *
     * @return The user ID associated with the profile.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user ID associated with this profile.
     *
     * @param userId The user ID to set.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the display name of the user.
     *
     * @return The display name of the user.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name of the user.
     *
     * @param displayName The display name to set.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the biography or description of the user.
     *
     * @return The biography of the user.
     */
    public String getBio() {
        return bio;
    }

    /**
     * Sets the biography or description of the user.
     *
     * @param bio The biography to set.
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * Gets the profile image ID associated with the user, if available.
     *
     * @return An {@code Optional} containing the profile image ID if it exists, or an empty {@code Optional} if no image is set.
     */
    public Optional<Integer> getProfileImageId() {
        return profileImageId == null ? Optional.empty() : Optional.of(profileImageId);
    }

    /**
     * Sets the profile image ID for the user.
     *
     * @param profileImageId The profile image ID to set, or null if there is no profile image.
     */
    public void setProfileImageId(Integer profileImageId) {
        this.profileImageId = profileImageId;
    }
}