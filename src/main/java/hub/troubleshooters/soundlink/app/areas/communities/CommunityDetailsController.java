package hub.troubleshooters.soundlink.app.areas.communities;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.communities.models.CommunityModel;
import hub.troubleshooters.soundlink.core.communities.services.CommunityService;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.HBox;

/**
 * Controller class for managing the community details view.
 * This class handles the display and management of community information, including joining and leaving
 * a community, as well as updating and deleting the community if the user has the necessary permissions.
 */
public class CommunityDetailsController {
    @FXML
    private ImageView bannerImageView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label genreLabel;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private Button signUpButton;
    @FXML
    private Button cancelButton;
    @FXML
    private HBox adminButtonBox;
    @FXML
    private Button feedButton;

    private final CommunityService communityService;
    private final ImageUploaderService imageUploaderService;
    private final IdentityService identityService;
    private final SceneManager sceneManager;

    private CommunityModel community;
    private static final Logger LOGGER = Logger.getLogger(CommunityDetailsController.class.getName());

    /**
     * Constructs a new {@code CommunityDetailsController}.
     *
     * @param communityService The service responsible for managing community operations.
     * @param imageUploaderService The service responsible for handling image uploads.
     * @param identityService The service responsible for managing user identity and permissions.
     * @param sceneManager The manager for handling scene navigation.
     */
    @Inject
    public CommunityDetailsController(CommunityService communityService, ImageUploaderService imageUploaderService,
                                      IdentityService identityService, SceneManager sceneManager) {
        this.communityService = communityService;
        this.imageUploaderService = imageUploaderService;
        this.identityService = identityService;
        this.sceneManager = sceneManager;
    }

    /**
     * Loads the details of the specified community and updates the view.
     *
     * @param id The ID of the community to load.
     */
    public void loadCommunityDetails(int id) {
        int userId = identityService.getUserContext().getUser().getId();

        try {
            Optional<Integer> permissionLevelOpt = communityService.getUserPermissionLevel(userId, id);

            int permissionLevel = permissionLevelOpt.orElse(1);

            if (permissionLevel == 1) {
                adminButtonBox.setVisible(true);
                adminButtonBox.setManaged(true);
                descriptionTextArea.setEditable(true);
            } else {
                adminButtonBox.setVisible(false);
                adminButtonBox.setManaged(false);
                descriptionTextArea.setEditable(false);
            }

            Optional<CommunityModel> optionalCommunity = communityService.getCommunity(id);
            if (optionalCommunity.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Community not found with ID " + id);
                return;
            }
            community = optionalCommunity.get();
            genreLabel.setText(community.genre());
            nameLabel.setText(community.name());
            descriptionTextArea.setText(community.description());
            setUpBannerImage();
            updateJoinButtons();
            feedButton.setOnAction(e -> sceneManager.navigateToCommunityFeedView(community.communityId()));

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading community details or permission check", e);
            showAlert(Alert.AlertType.ERROR, "Something went wrong. Please try again later.");
        }
    }

    /**
     * Sets up the community's banner image if available.
     */
    private void setUpBannerImage() {
        try {
            community.bannerImage().ifPresent(img -> {
                try {
                    var path = imageUploaderService.getFullProtocolPath(img);
                    bannerImageView.setImage(new Image(path));
                } catch (InvalidPathException e) {
                    LOGGER.log(Level.WARNING, "Failed to load banner image from file", e);
                    bannerImageView.setImage(null);
                }
            });
        } finally {
            if (bannerImageView.getImage() == null) {
                bannerImageView.setVisible(false);
                bannerImageView.setManaged(false);
            } else {
                bannerImageView.setVisible(true);
                bannerImageView.setManaged(true);
            }
        }
    }

    /**
     * Updates the visibility of the join and cancel buttons based on the user's join status.
     */
    private void updateJoinButtons() {
        try {
            int userId = identityService.getUserContext().getUser().getId();
            boolean isJoined = communityService.hasUserJoinedIntoCommunity(userId, community.communityId());
            toggleJoiningButtons(isJoined);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating joining status", e);
            showAlert(Alert.AlertType.ERROR, "Unable to update join status. Please try again.");
        }
    }

    /**
     * Handles the logic for joining a community when the join button is clicked.
     */
    @FXML
    protected void onJoinButtonClick() {
        if (community == null) {
            showAlert(Alert.AlertType.ERROR, "No community selected for booking.");
            return;
        }

        handleJoinOperation(() -> {
                    int userId = identityService.getUserContext().getUser().getId();
                    boolean joined = communityService.signUpForCommunity(userId, community.communityId());
                    if (joined) {
                        toggleJoiningButtons(true);
                    }
                    return joined;
                },
                "Joined into community successfully",
                "You have already joined this community");
    }

    /**
     * Handles the logic for canceling the join request when the cancel button is clicked.
     */
    @FXML
    protected void onCancelButtonClick() {
        if (community == null) {
            return;
        }
        handleJoinOperation(() -> {
                    int userId = identityService.getUserContext().getUser().getId();
                    boolean cancelled = communityService.cancelJoin(userId, community.communityId());
                    if (cancelled) {
                        toggleJoiningButtons(false);
                    }
                    return cancelled;
                },
                "Successfully removed from community",
                "Unable to be removed from the community. Please try again.");
    }


    /**
     * Handles the join or cancel operation and displays the result message.
     *
     * @param operation The operation to be executed.
     * @param successMessage The message to display if the operation is successful.
     * @param failureMessage The message to display if the operation fails.
     */
    private void handleJoinOperation(JoinOperation operation, String successMessage, String failureMessage) {
        try {
            boolean result = operation.execute();
            if (result) {
                showAlert(Alert.AlertType.INFORMATION, successMessage);
            } else {
                showAlert(Alert.AlertType.ERROR, failureMessage);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error processing join operation", e);
            showAlert(Alert.AlertType.ERROR, "Something went wrong. Please contact SoundLink Support.");
        }
    }

    /**
     * Toggles the visibility of the join and cancel buttons based on whether the user is joined.
     *
     * @param isJoined {@code true} if the user has joined the community, {@code false} otherwise.
     */
    private void toggleJoiningButtons(boolean isJoined) {
        signUpButton.setVisible(!isJoined);
        signUpButton.setManaged(!isJoined);

        cancelButton.setVisible(isJoined);
        cancelButton.setManaged(isJoined);
    }

    /**
     * Displays an alert with the specified message.
     *
     * @param alertType The type of alert to display.
     * @param message The message to display in the alert.
     */
    private void showAlert(Alert.AlertType alertType, String message) {
        sceneManager.alert(new Alert(alertType, message));
    }

    /**
     * Functional interface representing an operation to join or cancel a community membership.
     */
    @FunctionalInterface
    private interface JoinOperation {
        boolean execute() throws SQLException;
    }

    /**
     * Saves the changes to the community details when the save button is clicked.
     */
    @FXML
    protected void onSaveChangesClick() {
        if (community == null) {
            showAlert(Alert.AlertType.ERROR, "No community selected to update.");
            return;
        }

        if (nameLabel.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Community name cannot be empty.");
            return;
        }

        CommunityModel updatedCommunity = new CommunityModel(
                community.communityId(),
                nameLabel.getText(),
                descriptionTextArea.getText(),
                genreLabel.getText(),
                community.created(),
                community.bannerImage(),
                community.isPrivate()
        );

        try {
            communityService.updateCommunity(updatedCommunity);
            showAlert(Alert.AlertType.INFORMATION, "Community details updated successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating community details", e);
            showAlert(Alert.AlertType.ERROR, "Something went wrong while updating. Please try again.");
        }
    }

    /**
     * Deletes the community when the delete button is clicked.
     */
    @FXML
    protected void onDeleteCommunityClick() {
        if (community == null) {
            showAlert(Alert.AlertType.ERROR, "No community selected to delete.");
            return;
        }

        int userId = identityService.getUserContext().getUser().getId();

        try {
            communityService.deleteCommunity(community.communityId(), userId);
            showAlert(Alert.AlertType.INFORMATION, "Community deleted successfully.");
            sceneManager.navigateToSearchCommunityView();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting community", e);
            showAlert(Alert.AlertType.ERROR, "Something went wrong while deleting. Please try again.");
        }
    }
}

