package hub.troubleshooters.soundlink.app.areas.profile;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import hub.troubleshooters.soundlink.app.UserDataStore;


import java.io.File;

public class UserProfileController {

    // Fields for user profile data
    @FXML
    private TextField nameField; // Field for the user's name
    @FXML
    private TextField bioField;  // Field for the user's short bio

    // Labels for user's posts, events, and communities
    @FXML
    private Label postsLabel;
    @FXML
    private Label eventsLabel;
    @FXML
    private Label communitiesLabel;

    // ImageView for displaying user profile image
    @FXML
    private ImageView userImageView; // ImageView for user's profile picture

    // Buttons for saving edited fields
    @FXML
    private Button saveNameButton; // Button for saving edited name
    @FXML
    private Button saveBioButton;  // Button for saving edited bio

    // Reference to UserDataStore for storing user data
    private UserDataStore userDataStore = UserDataStore.getInstance();

    // Initialize method, runs when the FXML file is loaded
    @FXML
    public void initialize() {
        // Load user data from UserDataStore
        nameField.setText(userDataStore.getUserName());
        bioField.setText(userDataStore.getUserBio());
        updatePostsLabel();
        updateEventsLabel();
        updateCommunitiesLabel();
    }

    // Method to update posts label from UserDataStore
    private void updatePostsLabel() {
        if (userDataStore.getUserPosts().isEmpty()) {
            postsLabel.setText("No posts available.");
        } else {
            postsLabel.setText(String.join("\n", userDataStore.getUserPosts()));
        }
    }

    // Method to update events label from UserDataStore
    private void updateEventsLabel() {
        if (userDataStore.getUserEvents().isEmpty()) {
            eventsLabel.setText("No events available.");
        } else {
            eventsLabel.setText(String.join("\n", userDataStore.getUserEvents()));
        }
    }

    // Method to update communities label from UserDataStore
    private void updateCommunitiesLabel() {
        if (userDataStore.getUserCommunities().isEmpty()) {
            communitiesLabel.setText("No communities available.");
        } else {
            communitiesLabel.setText(String.join("\n", userDataStore.getUserCommunities()));
        }
    }

    // Method to make the name field editable and show the save button
    @FXML
    public void editName() {
        nameField.setEditable(true);
        saveNameButton.setVisible(true);
    }

    // Method to save the name and disable editing
    @FXML
    public void saveName() {
        String newName = nameField.getText();
        nameField.setEditable(false);
        saveNameButton.setVisible(false);

        // Save the name to UserDataStore and update it
        userDataStore.setUserName(newName);
    }

    // Method to make the bio field editable and show the save button
    @FXML
    public void editBio() {
        bioField.setEditable(true);
        saveBioButton.setVisible(true);
    }

    // Method to save the bio and disable editing
    @FXML
    public void saveBio() {
        String newBio = bioField.getText();
        bioField.setEditable(false);
        saveBioButton.setVisible(false);

        // Save the bio to UserDataStore and update it
        userDataStore.setUserBio(newBio);
    }

    // Method to allow the user to change their profile image
    @FXML
    public void changeImage() {
        FileChooser fileChooser = new FileChooser(); // Open file dialog
        fileChooser.setTitle("Choose an Image");     // Set the dialog title
        // Limit file types to images
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        // Show dialog and get the selected file
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            // Convert the selected file to an image and set it in the ImageView
            Image image = new Image(selectedFile.toURI().toString());
            userImageView.setImage(image); // Update ImageView with the new image
        }
    }
}
