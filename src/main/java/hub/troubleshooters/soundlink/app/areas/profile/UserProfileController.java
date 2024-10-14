package hub.troubleshooters.soundlink.app.areas.profile;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import hub.troubleshooters.soundlink.core.profile.services.UserProfileService;
import hub.troubleshooters.soundlink.data.models.UserProfile;
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

    @FXML
    private Button saveButton;

    // Reference to UserDataStore for storing user data
    private UserDataStore userDataStore = UserDataStore.getInstance();  // todo: DI or get rid of this

    private final ImageUploaderService imageUploaderService;
    private final UserProfileService userProfileService;
    private final IdentityService identityService;

    private UserProfile userProfile;

    @Inject
    public UserProfileController(ImageUploaderService imageUploaderService, UserProfileService userProfileService, IdentityService identityService) {
        this.imageUploaderService = imageUploaderService;
        this.userProfileService = userProfileService;
        this.identityService = identityService;
    }

    // Initialize method, runs when the FXML file is loaded
    @FXML
    public void initialize() {
        var user = identityService.getUserContext().getUser();
        userProfile = userProfileService.getUserProfile(user.getId()).get();     // TODO: error handling

        nameField.setText(userProfile.getDisplayName());
        bioField.setText(userProfile.getBio());
        updatePostsLabel();
        updateEventsLabel();
        updateCommunitiesLabel();

        // set default profile image if none exists
        var img = imageUploaderService.getDefaultProfileImageFile();
        userImageView.setImage(new Image(imageUploaderService.getFullProtocolPath(img)));
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

    // Method to allow the user to change their profile image
    @FXML
    public void changeImage() {
        // TODO: use ImageUploaderService
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

    @FXML
    protected void onSaveButtonClick() {
        System.out.println("G");
    }
}
