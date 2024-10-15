package hub.troubleshooters.soundlink.app.areas.profile;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.UserDataStore;
import hub.troubleshooters.soundlink.app.areas.Routes;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.File;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserProfileController {

    private static final Logger logger = Logger.getLogger(UserProfileController.class.getName());

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
    private javafx.scene.control.Button saveButton;

    @Inject
    public UserProfileController(ImageUploaderService imageUploaderService, UserProfileService userProfileService,
                                 IdentityService identityService, SceneManager sceneManager, Map map,
                                 UserDataStore userDataStore) {
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
                logger.log(Level.SEVERE, "User profile not found for user: {0}", user.getId());
                showErrorMessage("User profile not found", "Please contact support.");
                sceneManager.switchToOutletScene(Routes.HOME);
                return;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching user profile for user: " + user.getId(), e);
            showErrorMessage("Unable to load profile", "Please contact support.");
            sceneManager.switchToOutletScene(Routes.HOME);
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
        disableSaveButton();

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
                logger.log(Level.INFO, "Profile updated successfully for user: {0}", userProfile.userId());
                disableSaveButton();
            } else {
                logger.log(Level.SEVERE, "Failed to update profile for user: {0}", userProfile.userId());
                showErrorMessage("Failed to update profile", "Please try again.");
            }
        }
    }

    private void showErrorMessage(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(content);
        alert.showAndWait();
    }

    private void enableSaveButton() {
        saveButton.setDisable(false);
    }

    private void disableSaveButton() {
        saveButton.setDisable(true);
    }
}








