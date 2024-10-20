package hub.troubleshooters.soundlink.app.areas.home;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.auth.services.BookingService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class HomeController {
    private final SceneManager sceneManager;
    private final BookingService bookingService;

    @FXML
    private VBox upcomingEventsContainer;

    @Inject
    public HomeController(SceneManager sceneManager, BookingService bookingService) {
        this.sceneManager = sceneManager;
        this.bookingService = bookingService;
    }

    @FXML
    public void initialize() {
        List<String> bookings = bookingService.getUpcomingEventsForUser();

        if (bookings.isEmpty()) {
            Label noEventsLabel = new Label("You have no upcoming events");
            noEventsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
            upcomingEventsContainer.getChildren().add(noEventsLabel);
        } else {
            bookings.stream()
                    .limit(3)
                    .forEach(event -> {
                        Label eventLabel = new Label(event);
                        eventLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
                        upcomingEventsContainer.getChildren().add(eventLabel);
                    });
        }
    }

    @FXML
    protected void onBrowseCommunitiesButtonClick() {
        sceneManager.navigate(Routes.SEARCH_COMMUNITIES);
    }
}
