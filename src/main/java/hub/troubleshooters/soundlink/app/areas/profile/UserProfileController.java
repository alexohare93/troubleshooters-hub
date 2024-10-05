package hub.troubleshooters.soundlink.app.areas.profile;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;

public class UserProfileController {

    // Fields for user profile data
    @FXML
    private TextField nameField; // Field for the user's name
    @FXML
    private TextField bioField; // Field for the user's short bio
    @FXML
    private TextField preferencesField; // Field for the user's preferences
    @FXML
    private TextField postsField; // Field for the user's posts
    @FXML
    private TextField eventsField; // Field for the user's events
    @FXML
    private TextField communitiesField; // Field for the user's communities

    // ImageView for displaying user profile image
    @FXML
    private ImageView userImageView; // ImageView for user's profile picture

    // Buttons for saving edited fields
    @FXML
    private Button saveNameButton; // Button for saving edited name
    @FXML
    private Button saveBioButton; // Button for saving edited bio
    @FXML
    private Button savePreferencesButton; // Button for saving edited preferences
    @FXML
    private Button savePostsButton; // Button for saving edited posts
    @FXML
    private Button saveEventsButton; // Button for saving edited events
    @FXML
    private Button saveCommunitiesButton; // Button for saving edited communities

    // Initialize method, runs when the FXML file is loaded
    @FXML
    public void initialize() {
        // Currently empty; can be used for future initializations
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
        nameField.setEditable(false);
        saveNameButton.setVisible(false);
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
        bioField.setEditable(false);
        saveBioButton.setVisible(false);
    }

    // Method to make the preferences field editable and show the save button
    @FXML
    public void editPreferences() {
        preferencesField.setEditable(true);
        savePreferencesButton.setVisible(true);
    }

    // Method to save the preferences and disable editing
    @FXML
    public void savePreferences() {
        preferencesField.setEditable(false);
        savePreferencesButton.setVisible(false);
    }

    // Method to make the posts field editable and show the save button
    @FXML
    public void editPosts() {
        postsField.setEditable(true);
        savePostsButton.setVisible(true);
    }

    // Method to save the posts and disable editing
    @FXML
    public void savePosts() {
        postsField.setEditable(false);
        savePostsButton.setVisible(false);
    }

    // Method to make the events field editable and show the save button
    @FXML
    public void editEvents() {
        eventsField.setEditable(true);
        saveEventsButton.setVisible(true);
    }

    // Method to save the events and disable editing
    @FXML
    public void saveEvents() {
        eventsField.setEditable(false);
        saveEventsButton.setVisible(false);
    }

    // Method to make the communities field editable and show the save button
    @FXML
    public void editCommunities() {
        communitiesField.setEditable(true);
        saveCommunitiesButton.setVisible(true);
    }

    // Method to save the communities and disable editing
    @FXML
    public void saveCommunities() {
        communitiesField.setEditable(false);
        saveCommunitiesButton.setVisible(false);
    }

    // Method to allow the user to change their profile image
    @FXML
    public void changeImage() {
        FileChooser fileChooser = new FileChooser(); // Open file dialog
        fileChooser.setTitle("Choose an Image"); // Set the dialog title
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
