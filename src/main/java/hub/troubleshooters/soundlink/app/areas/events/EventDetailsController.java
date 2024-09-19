package hub.troubleshooters.soundlink.app.areas.events;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.events.services.EventService;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class EventDetailsController {
    @FXML private ImageView bannerImageView;
    @FXML private Label nameLabel;
    @FXML private TextArea descriptionTextArea;
    @FXML private Label communityLabel;
    @FXML private Label venueLabel;

    private final EventService eventService;
    private final ImageUploaderService imageUploaderService;

    @Inject
    public EventDetailsController(EventService eventService, ImageUploaderService imageUploaderService) {
        this.eventService = eventService;
        this.imageUploaderService = imageUploaderService;
    }

    public void loadEventDetails(int eventId) {
        var eventOpt = eventService.getEvent(eventId);
        if (eventOpt.isEmpty()) {
            return;
        }

        var event = eventOpt.get();
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

    }
}
