package hub.troubleshooters.soundlink.app.areas.profile;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.UserDataStore;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.Map;
import hub.troubleshooters.soundlink.core.auth.services.CommunityService;
import hub.troubleshooters.soundlink.core.auth.services.EventService;
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

/**
 * Controller for the user profile view. Handles updating and displaying user profile data.
 */
public class UserProfileController {

    private static final Logger logger = Logger.getLogger(UserProfileController.class.getName());

    private final UserDataStore userDataStore;
    private final ImageUploaderService imageUploaderService;
    private final UserProfileService userProfileService;
    private final IdentityService identityService;
    private final SceneManager sceneManager;
    private final Map map;
    private final EventService eventService;
    private final CommunityService communityService;

    private UserProfileModel userProfile;
    private File profileImageFile;

    @FXML
    private TextField nameField;
    @FXML
    private TextField bioField;
    @FXML
    private Label eventsLabel;
    @FXML
    private Label communitiesLabel;
    @FXML
    private ImageView userImageView;
    @FXML
    private Button saveButton;
    @FXML
    private Button clearImageButton;

    /**
     * Constructs a new {@code UserProfileController} with the necessary services.
     * @param imageUploaderService Service responsible for handling image uploads.
     * @param userProfileService Service responsible for handling user profile information.
     * @param identityService Service responsible for handling user identity information.
     * @param sceneManager Service responsible for managing the application's scene.
     * @param map Utility responsible for mapping database entities to models.
     * @param userDataStore Utility responsible for storing user data.
     * @param eventService Service responsible for managing event information.
     * @param communityService Service responsible for managing community information.
     */
    @Inject
    public UserProfileController(ImageUploaderService imageUploaderService, UserProfileService userProfileService,
                                 IdentityService identityService, SceneManager sceneManager, Map map,
                                 UserDataStore userDataStore, EventService eventService, CommunityService communityService) {
        this.imageUploaderService = imageUploaderService;
        this.userProfileService = userProfileService;
        this.identityService = identityService;
        this.sceneManager = sceneManager;
        this.map = map;
        this.userDataStore = userDataStore;
        this.eventService = eventService;
        this.communityService = communityService;
    }

    /**
     * Initializes the controller, called automatically bt FXML. Loads and displays user information,
     * including communities and bookings. Binds events for input fields and handles errors by routing the user to
     * the home view.
     */
    @FXML
    public void initialize() {
        try {
            var user = identityService.getUserContext().getUser();
            if (user == null) {
                throw new IllegalStateException("User is not authenticated");
            }

            int userId = user.getId();
            Optional<UserProfile> optionalProfile = userProfileService.getUserProfile(userId);

            if (optionalProfile.isPresent()) {
                userProfile = map.userProfile(optionalProfile.get());
            } else {
                showError("User profile not found.");
                disableSaveButton();
                return;
            }

            nameField.setText(userProfile.displayName());
            bioField.setText(userProfile.bio());
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
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initializing UserProfileController", e);
            sceneManager.navigate(Routes.HOME);
            sceneManager.alert(new Alert(Alert.AlertType.ERROR, "Unable to load profile. Please contact support."));
        }
    }

    /**
     * Opens the file dialog and allows the user to select and image. Then updates the display with the new image and
     * enables the save button.
     */
    @FXML
    protected void changeImage() {
        var file = sceneManager.openFileDialog();
        if (file == null) return;
        userImageView.setImage(new Image(imageUploaderService.getFullProtocolPath(file)));
        profileImageFile = file;
        clearImageButton.setDisable(false);
        enableSaveButton();
    }

    /**
     * Removes the current image. Then updates the display with the default image and enables the save button.
     */
    @FXML
    protected void onClearImageButtonClick() {
        var defaultImage = imageUploaderService.getDefaultProfileImageFile();
        userImageView.setImage(new Image(imageUploaderService.getFullProtocolPath(defaultImage)));
        profileImageFile = null;
        clearImageButton.setDisable(true);
        enableSaveButton();
    }

    /**
     * Updates the events label with the users events.
     */
    private void updateEventsLabel() {
        int userId = identityService.getUserContext().getUser().getId();
        var events = eventService.getEventsForUser((long) userId);
        eventsLabel.setText(String.join("\n", events));
    }

    /**
     * Updates the communities label with the users communities.
     */
    private void updateCommunitiesLabel() {
        var communities = identityService.getCommunities().stream()
                .map(community -> community.getName())
                .toList();
        communitiesLabel.setText(String.join("\n", communities));
    }

    /**
     * Disables the save button.
     */
    private void disableSaveButton() {
        saveButton.setDisable(true);
    }

    /**
     * Envables the save button.
     */
    private void enableSaveButton() {
        saveButton.setDisable(false);
    }

    /**
     * Updates the user profile with the new information. Displays messages on success and failure.
     */
    @FXML
    protected void onSaveButtonClick() {
        if (userProfile == null) {
            showError("User profile is not available. Cannot save changes.");
            return;
        }

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

    /**
     * Displays and error message.
     * @param message Error to be displayed.
     */
    private void showError(String message) {
        logger.log(Level.SEVERE, message);
        sceneManager.alert(new Alert(Alert.AlertType.ERROR, message));
    }
}















