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

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
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

    @FXML
    private Button clearImageButton;

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
                throw new SQLException();   // immediately caught below
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching user profile for user: " + user.getId(), e);
            sceneManager.navigate(Routes.HOME);
            sceneManager.alert(new Alert(Alert.AlertType.ERROR, "Unable to load profile. Please contact support."));
            return;
        }

        nameField.setText(userProfile.displayName());
        bioField.setText(userProfile.bio());
        updatePostsLabel();
        updateEventsLabel();
        updateCommunitiesLabel();

        if (userProfile.profileImage().isPresent()) {
            var img = userProfile.profileImage().get();
            userImageView.setImage(new Image(imageUploaderService.getFullProtocolPath(img)));
            clearImageButton.setDisable(false);
        } else {
            var img = imageUploaderService.getDefaultProfileImageFile();
            userImageView.setImage(new Image(imageUploaderService.getFullProtocolPath(img)));
            clearImageButton.setDisable(true);
        }

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
        clearImageButton.setDisable(false);
        enableSaveButton();
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

    @FXML
    protected void onClearImageButtonClick() {
        userImageView.setImage(new Image(imageUploaderService.getDefaultProfileImageFile().toURI().toString()));
        profileImageFile = null;
        clearImageButton.setDisable(true);
        enableSaveButton();
    }

    private void enableSaveButton() {
        saveButton.setDisable(false);
    }

    private void disableSaveButton() {
        saveButton.setDisable(true);
    }
}






