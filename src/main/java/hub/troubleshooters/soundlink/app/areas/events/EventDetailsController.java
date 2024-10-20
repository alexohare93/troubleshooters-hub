package hub.troubleshooters.soundlink.app.areas.events;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.Map;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.events.models.EventModel;
import hub.troubleshooters.soundlink.core.events.services.EventService;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import hub.troubleshooters.soundlink.core.profile.models.UserProfileModel;
import hub.troubleshooters.soundlink.core.profile.services.UserProfileService;
import hub.troubleshooters.soundlink.data.models.EventComment;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventDetailsController {
    @FXML private ImageView bannerImageView;
    @FXML private Label nameLabel;
    @FXML private TextArea descriptionTextArea;
    @FXML private Label communityLabel;
    @FXML private Label venueLabel;
    @FXML private Label capacityLabel;
    @FXML private VBox commentsVbox;
    @FXML private TextArea commentTextArea;

    private final EventService eventService;
    private final ImageUploaderService imageUploaderService;
    private final IdentityService identityService;
    private final SceneManager sceneManager;
    private final UserProfileService userProfileService;
    private final Map map;

    private EventModel event;

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
        capacityLabel.setText("" + event.capacity());
        commentTextArea.clear();

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

    private String formatDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");
        return dateFormat.format(date);
    }

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
            } else {
                sceneManager.alert(new Alert(Alert.AlertType.ERROR, "You are already booked into this event"));
            }
        } catch (SQLException e) {
            sceneManager.alert(new Alert(Alert.AlertType.ERROR, "Something went wrong booking into this event. Please contact SoundLink Support."));
        }
    }

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
}
