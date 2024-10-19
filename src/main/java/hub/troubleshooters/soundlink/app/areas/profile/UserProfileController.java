package hub.troubleshooters.soundlink.app.areas.profile;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.Map;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import hub.troubleshooters.soundlink.core.profile.models.UserProfileModel;
import hub.troubleshooters.soundlink.core.profile.models.UserProfileUpdateModel;
import hub.troubleshooters.soundlink.core.profile.services.UserProfileService;
import hub.troubleshooters.soundlink.data.models.UserProfile;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import hub.troubleshooters.soundlink.app.UserDataStore;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class UserProfileController {

    // Reference to UserDataStore for storing user data
    private UserDataStore userDataStore = UserDataStore.getInstance();  // todo: DI or get rid of this

    private final ImageUploaderService imageUploaderService;
    private final UserProfileService userProfileService;
    private final IdentityService identityService;
    private final SceneManager sceneManager;
    private final Map map;

    private UserProfileModel userProfile;
    private File profileImageFile;

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

    @Inject
    public UserProfileController(ImageUploaderService imageUploaderService, UserProfileService userProfileService, IdentityService identityService, SceneManager sceneManager, Map map) {
        this.imageUploaderService = imageUploaderService;
        this.userProfileService = userProfileService;
        this.identityService = identityService;
        this.sceneManager = sceneManager;
        this.map = map;
    }

    // Initialize method, runs when the FXML file is loaded
    @FXML
    public void initialize() {
        var user = identityService.getUserContext().getUser();
        try {
            userProfile = map.userProfile(userProfileService.getUserProfile(user.getId()).get());     // TODO: error handling
        } catch (SQLException e) {
            throw new RuntimeException(e);  // TODO: error handling
        }

        nameField.setText(userProfile.displayName());
        bioField.setText(userProfile.bio());
        updatePostsLabel();
        updateEventsLabel();
        updateCommunitiesLabel();

        if (userProfile.profileImage().isPresent()) {
            var img = userProfile.profileImage().get();
            userImageView.setImage(new Image(imageUploaderService.getFullProtocolPath(img)));
        } else {
            var img = imageUploaderService.getDefaultProfileImageFile();
            userImageView.setImage(new Image(imageUploaderService.getFullProtocolPath(img)));
        }
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

    @FXML
    public void changeImage() {
        var file = sceneManager.openFileDialog();
        if (file == null) return;
        userImageView.setImage(new Image(imageUploaderService.getFullProtocolPath(file)));
        profileImageFile = file;
    }

    @FXML
    protected void onSaveButtonClick() {
        var updateModel = new UserProfileUpdateModel(userProfile.id(), nameField.getText(), bioField.getText(), profileImageFile);
        var result = userProfileService.update(updateModel, userProfile.userId());
        if (result.isSuccess()) {
            sceneManager.navigate(Routes.HOME);
            sceneManager.alert(new Alert(Alert.AlertType.CONFIRMATION, "Profile updated successfully"));
        } else {
            var error = result.getErrors().getFirst();
            sceneManager.alert(new Alert(Alert.AlertType.ERROR, "Validation error: " + error.getMessage()));
        }
    }
}
