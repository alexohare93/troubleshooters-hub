package hub.troubleshooters.soundlink.app.areas.events;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.events.models.EventModel;
import hub.troubleshooters.soundlink.core.events.services.EventService;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.SQLException;

public class EventDetailsController {
    @FXML private ImageView bannerImageView;
    @FXML private Label nameLabel;
    @FXML private TextArea descriptionTextArea;
    @FXML private Label communityLabel;
    @FXML private Label venueLabel;

    private final EventService eventService;
    private final ImageUploaderService imageUploaderService;
    private final IdentityService identityService;
    private final SceneManager sceneManager;

    private EventModel event;

    @Inject
    public EventDetailsController(EventService eventService, ImageUploaderService imageUploaderService, IdentityService identityService, SceneManager sceneManager) {
        this.eventService = eventService;
        this.imageUploaderService = imageUploaderService;
        this.identityService = identityService;
        this.sceneManager = sceneManager;
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

        // set banner image
        try {
            if (event.bannerImage().isPresent()) {
                var img = event.bannerImage().get();
                var path = "file:///" + imageUploaderService.getImageFile(img).getAbsolutePath();
                bannerImageView.setImage(new Image(path));
            }
        } catch (IOException e) {
            bannerImageView.setImage(null);
        } finally {
            if (bannerImageView.getImage() == null) {
                // make image element not take up any space
                bannerImageView.setVisible(false);
                bannerImageView.setManaged(false);
            }
        }

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
}
