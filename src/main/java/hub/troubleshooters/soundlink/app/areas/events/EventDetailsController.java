package hub.troubleshooters.soundlink.app.areas.events;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.areas.communities.CommunityDetailsController;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.Map;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.communities.models.CommunityModel;
import hub.troubleshooters.soundlink.core.events.models.EventModel;
import hub.troubleshooters.soundlink.core.events.services.EventService;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import hub.troubleshooters.soundlink.core.profile.models.UserProfileModel;
import hub.troubleshooters.soundlink.core.profile.services.UserProfileService;
import hub.troubleshooters.soundlink.data.models.EventComment;
import hub.troubleshooters.soundlink.data.models.Community;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.InvalidPathException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

/**
 * Controller class for managing the event details view.
 * This class handles the display and management of event information, including joining and leaving
 * an event, as well as updating and deleting the event if the user has the necessary permissions.
 */
public class EventDetailsController {
    @FXML private ImageView bannerImageView;
    @FXML private Label nameLabel;
    @FXML private TextArea descriptionTextArea;
    @FXML private Label communityLabel;
    @FXML private TextField venueLabel;
    @FXML private VBox commentsVbox;
    @FXML private TextArea commentTextArea;
    @FXML private Button signUpButton;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private HBox adminButtonBox;
    @FXML private TextField capacityTextField;
    @FXML private Label scheduledDateLabel;
    @FXML private Label placesRemainingLabel;
    @FXML private Label creatorLabel;
    @FXML private Label createdDate;


    private final EventService eventService;
    private final ImageUploaderService imageUploaderService;
    private final IdentityService identityService;
    private final SceneManager sceneManager;
    private final UserProfileService userProfileService;
    private final Map map;

    private EventModel event;

    /**
     * Constructs the {@code EventDetailsController}.
     * @param eventService The service responsible for handling event information.
     * @param imageUploaderService The service responsible for handling image uploads.
     * @param identityService The service responsible for managing user identity and permissions.
     * @param sceneManager The manager for handling scene navigation.
     * @param userProfileService The service responsible for handling user profile information.
     * @param map The utility responsible for mapping database entities and models.
     */
    @Inject
    public EventDetailsController(
            EventService eventService,
            ImageUploaderService imageUploaderService,
            IdentityService identityService,
            SceneManager sceneManager,
            UserProfileService userProfileService,
            Map map
    ) {
        this.eventService = eventService;
        this.imageUploaderService = imageUploaderService;
        this.identityService = identityService;
        this.sceneManager = sceneManager;
        this.userProfileService = userProfileService;
        this.map = map;
    }

    /**
     * Loads the event information from the {@code eventId} and displays it.
     * Handles errors if the event can not be found and implements logic for booking capacity.
     * @param eventId The ID of the event that will be detailed.
     */
    public void loadEventDetails(int eventId) {
        var eventOpt = eventService.getEvent(eventId);
        if (eventOpt.isEmpty()) {
            sceneManager.alert(new Alert(Alert.AlertType.ERROR, "Event not found with ID " + eventId));
            return;
        }

        event = eventOpt.get();
        nameLabel.setText(event.name());
        descriptionTextArea.setText(event.description());
        communityLabel.setText("This event is shared with the " + event.community().getName() + " community.");
        venueLabel.setText(event.venue());

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(event.scheduled());
        scheduledDateLabel.setText(formattedDate);
        String formattedCreatedDate = dateFormat.format(event.created());
        createdDate.setText(formattedCreatedDate);

        capacityTextField.setText(String.valueOf(event.capacity()));

        commentTextArea.clear();

        try {
            int totalBookings = eventService.getBookingCountForEvent(event.id());
            int placesRemaining = event.capacity() - totalBookings;
            placesRemainingLabel.setText(String.valueOf(placesRemaining));

            if (placesRemaining < event.capacity() / 2) {
                showAlert(Alert.AlertType.WARNING, "Warning: Less than 50% capacity remaining.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load booking information.");
        }

        try {
            int userId = identityService.getUserContext().getUser().getId();
            String creatorName = eventService.getDisplayNameById(userId);
            creatorLabel.setText(creatorName);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load event creator.");
        }

        updateBookButtons();
        updateAdminButtonsVisibility();

        // set banner image
        try {
            if (event.bannerImage().isPresent()) {
                var img = event.bannerImage().get();
                var path = imageUploaderService.getFullProtocolPath(img);
                bannerImageView.setImage(new Image(path));
            } else {
                var img = imageUploaderService.getSampleBannerImageFile(event.id());
                var path = imageUploaderService.getFullProtocolPath(img);
                bannerImageView.setImage(new Image(path));
            }
        } catch (InvalidPathException e) {
            bannerImageView.setImage(null);
        } finally {
            if (bannerImageView.getImage() == null) {
                // make image element not take up any space
                bannerImageView.setVisible(false);
                bannerImageView.setManaged(false);
            }
        }

        // comments
        try {
            var comments = eventService.getComments(eventId);
            commentsVbox.getChildren().clear();
            for (var comment : comments) {
                var card = createCommentCard(comment);
                commentsVbox.getChildren().add(card);
            }
        } catch (SQLException e) {
            // TODO: error handling
        }
    }

    /**
     * Sets the visibility of admin tool depending on the users permissions.
     */
    private void updateAdminButtonsVisibility() {
        try {
            int userId = identityService.getUserContext().getUser().getId();
            boolean isAdmin = eventService.isAdmin(userId, event.id());
            adminButtonBox.setVisible(isAdmin);
            adminButtonBox.setManaged(isAdmin);
            descriptionTextArea.setEditable(isAdmin);
            capacityTextField.setEditable(isAdmin);
            venueLabel.setEditable(isAdmin);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Unable to determine admin status. Please try again.");
        }
    }

    /**
     * Creates a javaFX {@link Node} for a comment.
     * @param comment An {@code EventComment} model.
     * @return A javaFX {@link Node} containing the formatted information in the comment.
     */
    private Node createCommentCard(EventComment comment) {
        UserProfileModel userProfile = null; // TODO: error handling
        try {
            userProfile = map.userProfile(userProfileService.getUserProfile(comment.getUserId()).get());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Outer card layout
        var card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 15px; -fx-spacing: 10px;");
        card.setPrefWidth(300);
        card.setPrefHeight(150);

        // top row: profile image and name
        var r1 = new HBox();
        r1.setSpacing(10);
        r1.setAlignment(Pos.CENTER_LEFT);

        var img = imageUploaderService.getFullProtocolPath(imageUploaderService.getDefaultProfileImageFile());
        if (userProfile.profileImage().isPresent()) {
            img = imageUploaderService.getFullProtocolPath(userProfile.profileImage().get());
        }

        var imgView = new ImageView(img);
        imgView.setFitWidth(40);
        imgView.setFitHeight(40);
        imgView.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #FF6F61; -fx-border-width: 3px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 5);");
        imgView.setClip(new Circle(20, 20, 20));

        var nameLabel = new Label(userProfile.displayName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        r1.getChildren().addAll(imgView, nameLabel);

        // middle row: comment
        var contentLabel = new Label(comment.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-font-size: 12px;");

        // bottom row: date
        var r2 = new HBox();
        r2.setAlignment(Pos.BOTTOM_RIGHT);
        var createdDateLabel = new Label(formatDate(comment.getCreated()));
        createdDateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        r2.getChildren().add(createdDateLabel);

        card.getChildren().addAll(r1, contentLabel, r2);
        return card;
    }

    /**
     * Formats {@link Date} object into a formatted {@link String}.
     * @param date A {@link Date}.
     * @return A formatted {@link String}.
     */
    private String formatDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");
        return dateFormat.format(date);
    }

    /**
     * Attempts to book the user for the event, displayed. Handles errors on failure.
     */
    @FXML
    protected void onBookButtonClick() {
        if (event == null) {
            return;
        }
        try {
            // TODO: if you are already booked into event, this button shouldn't event appear / replace with "cancel booking" button.
            var result = eventService.bookEvent(event.id(), identityService.getUserContext().getUser().getId());
            if (result.isSuccess()) {
                sceneManager.alert(new Alert(Alert.AlertType.INFORMATION, "Booked into event successfully"));
                toggleJoiningButtons(true);
            } else {
                toggleJoiningButtons(true);
                sceneManager.alert(new Alert(Alert.AlertType.ERROR, "You are already booked into this event"));
            }
        } catch (SQLException e) {
            sceneManager.alert(new Alert(Alert.AlertType.ERROR, "Something went wrong booking into this event. Please contact SoundLink Support."));
        }
    }

    /**
     * Attempts to cancel the users booking of the event, handles errors on failure.
     */
    @FXML
    protected void onCancelButtonClick() {
        if (event == null) {
            return;
        }
        handleBookingOperation(() -> {
                    int userId = identityService.getUserContext().getUser().getId();
                    boolean cancelled = eventService.cancelBooking(userId,event.id());
                    if (cancelled) {
                        toggleJoiningButtons(false);
                    }
                    return cancelled;
                },
                "Successfully removed from the event",
                "Unable to be removed from the event. Please try again.");
    }

    /**
     * Posts the users comment, handles errors on failure.
     */
    @FXML
    protected void onCommentButtonClick() {
        if (event == null) {
            return;
        }

        // mild validation
        if (commentTextArea.getText().isEmpty()) {
            sceneManager.alert(new Alert(Alert.AlertType.INFORMATION, "Comment cannot be empty"));
            return;
        }

        // leave comment
        try {
            eventService.comment(event.id(), identityService.getUserContext().getUser().getId(), commentTextArea.getText());
        } catch (SQLException e) {
            sceneManager.alert(new Alert(Alert.AlertType.ERROR, "Error creating comment."));
        }

        // refresh current view
        loadEventDetails(event.id());
    }

    /**
     * Handles whether the Book or Cancel Booking button is displayed.
     * @param isJoined boolean for if the user is booked in the current event.
     */
    private void toggleJoiningButtons(boolean isJoined) {
        signUpButton.setVisible(!isJoined);
        signUpButton.setManaged(!isJoined);

        cancelButton.setVisible(isJoined);
        cancelButton.setManaged(isJoined);
    }

    /**
     * Handles the operations for booking the user into and event or canceling the user booking.
     * Displays success or failure message on completion.
     * @param operation A {@code BookOperation}.
     * @param successMessage Message to be displayed on success.
     * @param failureMessage Message to be displayed on failure.
     */
    private void handleBookingOperation(BookOperation operation, String successMessage, String failureMessage) {
        try {
            boolean result = operation.execute();
            if (result) {
                showAlert(Alert.AlertType.INFORMATION, successMessage);
            } else {
                showAlert(Alert.AlertType.ERROR, failureMessage);
            }
        } catch (SQLException e) {
            System.out.println(e);
            showAlert(Alert.AlertType.ERROR, "Something went wrong. Please contact SoundLink Support.");
        }
    }

    /**
     * Functional interface for inline functions.
     */
    @FunctionalInterface
    private interface BookOperation {
        /**
         * Executes the {@code BookOperation}.
         * @return A boolean indicating the operations' success.
         * @throws SQLException If there is an SQL error.
         */
        boolean execute() throws SQLException;
    }

    /**
     * Shows alert.
     * @param alertType {@link Alert} {@code AlertType} Enum.
     * @param message {@link String}
     */
    private void showAlert(Alert.AlertType alertType, String message) {
        sceneManager.alert(new Alert(alertType, message));
    }

    /**
     * Updates the display of the Book or Cancel Booking buttons.
     */
    private void updateBookButtons() {
        try {
            int userId = identityService.getUserContext().getUser().getId();
            boolean isJoined = eventService.isBooked(userId,event.id());
            toggleJoiningButtons(isJoined);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Unable to update booking status. Please try again.");
        }
    }

    /**
     * Updates the event with the changes made by a user with permissions. Displays success or failure messages on
     * completion.
     */
    @FXML
    protected void onSaveChangesClick() {
        if (event == null) {
            showAlert(Alert.AlertType.ERROR, "No event selected to update.");
            return;
        }

        if (nameLabel.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Event name cannot be empty.");
            return;
        }

        // validate capacity
        int capacity;
        try {
            String capacityText = capacityTextField.getText().trim();

            if (capacityText.equalsIgnoreCase("SAMPLE CAPACITY")) {
                showAlert(Alert.AlertType.ERROR, "Please enter a valid capacity.");
                return;
            }

            capacity = Integer.parseInt(capacityText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid capacity value. Please enter a valid number.");
            return;
        }

        Community community = event.community();

        EventModel updatedEventToBeSaved = new EventModel(
                event.id(),
                nameLabel.getText(),
                descriptionTextArea.getText(),
                community,
                venueLabel.getText(),
                capacity,
                event.scheduled(),
                event.created(),
                event.bannerImage()
        );

        try {
            eventService.updateEvent(updatedEventToBeSaved);
            showAlert(Alert.AlertType.INFORMATION, "Event details updated successfully.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Something went wrong while updating. Please try again.");
        }
    }


    /**
     * Deletes the event, can only be preformed by a user with pemissions.
     * Routes the user to search event view on success and displays error message on failure.
     */
    @FXML
    protected void onDeleteEventClick() {
        if (event == null) {
            showAlert(Alert.AlertType.ERROR, "No event selected to delete.");
            return;
        }

        int userId = identityService.getUserContext().getUser().getId();

        try {
            eventService.deleteEvent(event.id(), userId);
            showAlert(Alert.AlertType.INFORMATION, "Event deleted successfully.");
            sceneManager.navigateToSearchEventView();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Something went wrong while deleting. Please try again.");
        }
    }
}
