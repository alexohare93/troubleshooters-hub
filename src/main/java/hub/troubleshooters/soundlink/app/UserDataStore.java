package hub.troubleshooters.soundlink.app;

import java.util.ArrayList;
import java.util.List;

public class UserDataStore {

    // Fields to store user data
    private final List<String> userPosts = new ArrayList<>(); // Posts made by the user
    private final List<String> userEvents = new ArrayList<>(); // Events attended or created by the user
    private final List<String> userCommunities = new ArrayList<>(); // Communities the user is part of

    // Public constructor (no longer Singleton)
    public UserDataStore() {
        // Initialize with default or sample data if necessary
    }

    // Method to add a post to the user's data
    public void addUserPost(String post) {
        userPosts.add(post);
    }

    // Method to add an event to the user's data
    public void addUserEvent(String event) {
        userEvents.add(event);
    }

    // Method to add a community to the user's data
    public void addUserCommunity(String community) {
        userCommunities.add(community);
    }

    // Getters for the user data lists
    public List<String> getUserPosts() {
        return userPosts;
    }

    public List<String> getUserEvents() {
        return userEvents;
    }

    public List<String> getUserCommunities() {
        return userCommunities;
    }

    // Method to set the user's name (optional if needed)
    private String userName = "Default Name";

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    // Method to set the user's bio (optional if needed)
    private String userBio = "This is a short bio.";

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String bio) {
        this.userBio = bio;
    }
}
