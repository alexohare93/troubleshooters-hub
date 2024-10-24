package hub.troubleshooters.soundlink.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to store user information.
 */
public class UserDataStore {

    // Fields to store user data
    private final List<String> userPosts = new ArrayList<>(); // Posts made by the user
    private final List<String> userEvents = new ArrayList<>(); // Events attended or created by the user
    private final List<String> userCommunities = new ArrayList<>(); // Communities the user is part of

    /**
     * Constructs UserDataStore.
     */
    public UserDataStore() {
        // Initialize with default or sample data if necessary
    }

    /**
     * Adds a post to the user data store.
      * @param post {@link String}
     */
    public void addUserPost(String post) {
        userPosts.add(post);
    }

    /**
     * Adds a event to the user data store.
     * @param event {@link String}
     */
    public void addUserEvent(String event) {
        userEvents.add(event);
    }

    /**
     * Adds a community to the user data store.
     * @param community {@link String}
     */
    public void addUserCommunity(String community) {
        userCommunities.add(community);
    }

    /**
     * Gets users posts
     * @return A {@link List<String>} of posts
     */
    public List<String> getUserPosts() {
        return userPosts;
    }

    /**
     * Gets users events
     * @return A {@link List<String>} of events
     */
    public List<String> getUserEvents() {
        return userEvents;
    }

    /**
     * Gets users communities
     * @return A {@link List<String>} of communities
     */
    public List<String> getUserCommunities() {
        return userCommunities;
    }

    // Method to set the user's name (optional if needed)
    private String userName = "Default Name";

    /**
     * Gets users name
     * @return {@link String} username.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets users name.
     * @param name {@link String}
     */
    public void setUserName(String name) {
        this.userName = name;
    }

    // Method to set the user's bio (optional if needed)
    private String userBio = "This is a short bio.";

    /**
     * Gets users bio
     * @return {@link String} Bio.
     */
    public String getUserBio() {
        return userBio;
    }

    /**
     * Sets users bio.
     * @param bio {@link String}
     */
    public void setUserBio(String bio) {
        this.userBio = bio;
    }
}
