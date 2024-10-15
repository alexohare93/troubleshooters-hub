package hub.troubleshooters.soundlink.app.areas.profile;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.UserDataStore;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.Map;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import hub.troubleshooters.soundlink.core.profile.models.UserProfileModel;
import hub.troubleshooters.soundlink.core.profile.models.UserProfileUpdateModel;
import hub.troubleshooters.soundlink.core.profile.services.UserProfileService;
import hub.troubleshooters.soundlink.data.models.UserProfile;
import hub.troubleshooters.soundlink.data.models.Image;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.SQLException;
import java.util.Optional;

public class UserProfileController {

    private final UserDataStore userDataStore;
    private final ImageUploaderService imageUploaderService;
    private final UserProfileService userProfileService;
    private final IdentityService identityService;
    private final SceneManager sceneManager;
    private final Map map;

    private UserProfileModel userProfile;
    private File profileImageFile;

    @FXML
    private TextField nameField;
    @FXML
    private TextField bioField;
    @FXML
    private Label postsLabel;
    @FXML
    private Label eventsLabel;
    @FXML
    private Label communitiesLabel;
    @FXML
    private ImageView userImageView;
    @FXML
    private Button saveButton;

    @Inject
    public UserProfileController(ImageUploaderService imageUploaderService, UserProfileService userProfileService, IdentityService identityService, SceneManager sceneManager, Map map, UserDataStore userDataStore) {
        this.imageUploaderService = imageUploaderService;
        this.userProfileService = userProfileService;
        this.identityService = identityService;
        this.sceneManager = sceneManager;
        this.map = map;
        this.userDataStore = userDataStore;
    }

    @FXML
    public void initialize() {
        var user = identityService.getUserContext().getUser();
        try {
            Optional<UserProfile> optionalProfile = userProfileService.getUserProfile(user.getId());
            if (optionalProfile.isPresent()) {
                userProfile = map.userProfile(optionalProfile.get());
            } else {
                System.err.println("User profile not found for user: " + user.getId());
                return;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user profile: " + e.getMessage());
            return;
        }

        nameField.setText(userProfile.displayName());
        bioField.setText(userProfile.bio());
        updatePostsLabel();
        updateEventsLabel();
        updateCommunitiesLabel();

        var img = userProfile.profileImage()
                .map(Image::getFileName)
                .orElse(imageUploaderService.getDefaultProfileImageFile().getName());

        userImageView.setImage(new javafx.scene.image.Image(imageUploaderService.getFullProtocolPath(new File(img))));

        saveButton.setDisable(true);

        nameField.textProperty().addListener((observable, oldValue, newValue) -> enableSaveButton());
        bioField.textProperty().addListener((observable, oldValue, newValue) -> enableSaveButton());
    }

    private void updatePostsLabel() {
        postsLabel.setText(String.join("\n", userDataStore.getUserPosts()));
    }

    private void updateEventsLabel() {
        eventsLabel.setText(String.join("\n", userDataStore.getUserEvents()));
    }

    private void updateCommunitiesLabel() {
        communitiesLabel.setText(String.join("\n", userDataStore.getUserCommunities()));
    }

    @FXML
    public void changeImage() {
        var file = sceneManager.openFileDialog();
        if (file == null) return;
        userImageView.setImage(new javafx.scene.image.Image(imageUploaderService.getFullProtocolPath(file)));
        profileImageFile = file;
        enableSaveButton();
    }

    @FXML
    protected void onSaveButtonClick() {
        if (userProfile != null) {
            var updateModel = new UserProfileUpdateModel(userProfile.id(), nameField.getText(), bioField.getText(), profileImageFile);
            var result = userProfileService.update(updateModel, userProfile.userId());
            if (result.isSuccess()) {
                System.out.println("Profile updated successfully");
                disableSaveButton();
            } else {
                System.out.println(result.getError().getMessage());
            }
        }
    }

    private void enableSaveButton() {
        saveButton.setDisable(false);
    }

    private void disableSaveButton() {
        saveButton.setDisable(true);
    }
}




