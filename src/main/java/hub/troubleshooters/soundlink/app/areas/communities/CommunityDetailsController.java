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
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private Button feedButton;

    private final CommunityService communityService;
    private final ImageUploaderService imageUploaderService;
    private final IdentityService identityService;
    private final SceneManager sceneManager;

    private CommunityModel community;
    private static final Logger LOGGER = Logger.getLogger(CommunityDetailsController.class.getName());

    @Inject
    public CommunityDetailsController(CommunityService communityService, ImageUploaderService imageUploaderService,
                                      IdentityService identityService, SceneManager sceneManager) {
        this.communityService = communityService;
        this.imageUploaderService = imageUploaderService;
        this.identityService = identityService;
        this.sceneManager = sceneManager;
    }

    public void loadCommunityDetails(int id) {
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

    private void updateJoinButtons() {
        try {
            int userId = identityService.getUserContext().getUser().getId();
            boolean isJoined = communityService.hasUserJoinedIntoCommunity(userId, community.communityId());
            toggleBookingButtons(isJoined);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating booking status", e);
            showAlert(Alert.AlertType.ERROR, "Unable to update booking status. Please try again.");
        }
    }

    @FXML
    protected void onBookButtonClick() {
        if (community == null) {
            showAlert(Alert.AlertType.ERROR, "No community selected for booking.");
            return;
        }
        handleJoinOperation(() -> communityService.signUpForCommunity(identityService.getUserContext().getUser().getId(), community.communityId()),
                "Booked into event successfully",
                "You are already booked into this event");
    }

    @FXML
    protected void onCancelButtonClick() {
        if (community == null) {
            showAlert(Alert.AlertType.ERROR, "No community selected for cancellation.");
            return;
        }
        handleJoinOperation(() -> communityService.cancelJoin(identityService.getUserContext().getUser().getId(), community.communityId()),
                "Booking canceled successfully",
                "Unable to cancel booking. Please try again.");
    }

    private void handleJoinOperation(JoinOperation operation, String successMessage, String failureMessage) {
        try {
            boolean result = operation.execute();
            if (result) {
                showAlert(Alert.AlertType.INFORMATION, successMessage);
                updateJoinButtons();
            } else {
                showAlert(Alert.AlertType.ERROR, failureMessage);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error processing booking operation", e);
            showAlert(Alert.AlertType.ERROR, "Something went wrong. Please contact SoundLink Support.");
        }
    }

    private void toggleBookingButtons(boolean isBooked) {
        signUpButton.setVisible(!isBooked);
        cancelButton.setVisible(isBooked);
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        sceneManager.alert(new Alert(alertType, message));
    }

    @FunctionalInterface
    private interface JoinOperation {
        boolean execute() throws SQLException;
    }
}

