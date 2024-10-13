package hub.troubleshooters.soundlink.app.areas.communities;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.communities.models.CommunityModel;
import hub.troubleshooters.soundlink.core.communities.services.CommunityService;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommunityAdminController {
    @FXML
    private ImageView bannerImageView;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private TextField genreTextField;
    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;

    private final CommunityService communityService;
    private final ImageUploaderService imageUploaderService;
    private final IdentityService identityService;
    private final SceneManager sceneManager;

    private CommunityModel community;
    private static final Logger LOGGER = Logger.getLogger(CommunityAdminController.class.getName());

    @Inject
    public CommunityAdminController(CommunityService communityService, ImageUploaderService imageUploaderService,
                                    IdentityService identityService, SceneManager sceneManager) {
        this.communityService = communityService;
        this.imageUploaderService = imageUploaderService;
        this.identityService = identityService;
        this.sceneManager = sceneManager;
    }

    public void loadCommunityDetails(int id) {
        int userId = identityService.getUserContext().getUser().getId();

        try {
            // Check if the user has admin permission (permission level = 1)
            Optional<Integer> permissionLevel = communityService.getUserPermissionLevel(userId, id);
            if (permissionLevel.isEmpty() || permissionLevel.get() != 1) {
                showAlert(Alert.AlertType.ERROR, "You do not have permission to perform admin actions for this community.");
                return;
            }

            // Load community details if the user has admin permission
            Optional<CommunityModel> optionalCommunity = communityService.getCommunity(id);
            if (optionalCommunity.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Community not found with ID " + id);
                return;
            }
            community = optionalCommunity.get();

            // Populate the fields with community data
            nameTextField.setText(community.name());
            descriptionTextArea.setText(community.description());
            genreTextField.setText(community.genre());

            // Set up the banner image
            setUpBannerImage();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading community details or permission check", e);
            showAlert(Alert.AlertType.ERROR, "Something went wrong. Please try again later.");
        }
    }

    private void setUpBannerImage() {
        try {
            community.bannerImage().ifPresent(img -> {
                try {
                    var path = "file:///" + imageUploaderService.getImageFile(img).getAbsolutePath();
                    bannerImageView.setImage(new Image(path));
                } catch (IOException e) {
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

    @FXML
    protected void onSaveChangesClick() {
        if (community == null) {
            showAlert(Alert.AlertType.ERROR, "No community selected to update.");
            return;
        }

        // Validate input (optional)
        if (nameTextField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Community name cannot be empty.");
            return;
        }

        // Create a new CommunityModel with updated values (since it's immutable)
        CommunityModel updatedCommunity = new CommunityModel(
                community.communityId(), // Keep the same ID
                nameTextField.getText(),  // Updated name
                descriptionTextArea.getText(),  // Updated description
                genreTextField.getText(),  // Updated genre
                community.created(),  // Keep the original created date
                community.bannerImage()  // Keep the original banner image
        );

        try {
            communityService.updateCommunity(updatedCommunity);
            showAlert(Alert.AlertType.INFORMATION, "Community details updated successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating community details", e);
            showAlert(Alert.AlertType.ERROR, "Something went wrong while updating. Please try again.");
        }
    }

    @FXML
    protected void onDeleteEventClick() {
        // Placeholder logic for deleting an event
        showAlert(Alert.AlertType.INFORMATION, "Delete event functionality is yet to be implemented.");
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        sceneManager.alert(new Alert(alertType, message));
    }
}


