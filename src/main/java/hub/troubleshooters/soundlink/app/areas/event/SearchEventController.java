package hub.troubleshooters.soundlink.app.areas.event;

import hub.troubleshooters.soundlink.data.factories.SearchEventFactory;
import hub.troubleshooters.soundlink.data.factories.EventAttendeeFactory;
import hub.troubleshooters.soundlink.data.models.EventAttendee;
import hub.troubleshooters.soundlink.data.models.SearchEvent;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.data.DatabaseConnection;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.Optional;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;

import java.sql.SQLException;
import java.util.List;

public class SearchEventController {

    @FXML
    private VBox eventListVBox;

    @FXML
    private ComboBox<String> eventTypeComboBox;

    @FXML
    private ComboBox<String> locationComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button searchButton;

    private final SearchEventFactory searchEventFactory;
    private final IdentityService identityService;
    private final EventAttendeeFactory eventAttendeeFactory;


    @Inject
    public SearchEventController(SearchEventFactory searchEventFactory, IdentityService identityService, EventAttendeeFactory eventAttendeeFactory) {
        this.searchEventFactory = searchEventFactory;
        this.identityService = identityService;
        this.eventAttendeeFactory = eventAttendeeFactory;  // Inject EventAttendeeFactory
    }


    @FXML
    public void initialize() {
        try {
            populateEventList();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        searchButton.setOnAction(event -> {
            // Perform filtering logic based on selected filters
            try {
                populateEventList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Populates the event list in the UI
     * @throws SQLException if a database error occurs
     */
    private void populateEventList() throws SQLException {
        // Clear the previous event list
        eventListVBox.getChildren().clear();

        // Fetch upcoming events for the user
        List<SearchEvent> events = listUpcomingEvents();

        // Dynamically add event cards to the VBox
        for (SearchEvent event : events) {
            VBox eventCard = createEventCard(event);
            eventListVBox.getChildren().add(eventCard);
        }
    }

    /**
     * Creates an event card for each event to display in the VBox.
     * @param event The event to display
     * @return A VBox containing the event card
     */
    private VBox createEventCard(SearchEvent event) {
        VBox eventCard = new VBox();
        eventCard.setSpacing(10.0);
        eventCard.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 10px;");
    
        Label nameLabel = new Label(event.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fa8072;");
    
        Label locationLabel = new Label("Location: " + event.getVenue());
        Label dateLabel = new Label("Date: " + event.getScheduledDate().toString());
    
        Button signUpButton = new Button("Sign Up");
        signUpButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: white;");
        
        // Handle Sign Up button action
        signUpButton.setOnAction(e -> handleSignUp(event));
    
        eventCard.getChildren().addAll(nameLabel, locationLabel, dateLabel, signUpButton);
        return eventCard;
    }


    /**
     * Fetches the list of upcoming events for the user
     * @return a list of SearchEvent objects
     * @throws SQLException if a database error occurs
     */
    public List<SearchEvent> listUpcomingEvents() throws SQLException {
        int userId = identityService.getUserContext().getUser().getId();

        // Fetch events from communities the user is a member of
        List<SearchEvent> userCommunityEvents = searchEventFactory.findUserCommunityEvents(userId);

        // Fetch public events from communities the user is not a member of
        List<SearchEvent> publicEvents = searchEventFactory.findPublicCommunityEvents(userId);

        // Combine both lists
        userCommunityEvents.addAll(publicEvents);

        return userCommunityEvents;
    }


  private void handleSignUp(SearchEvent event) {
    int userId = identityService.getUserContext().getUser().getId();  // Fetch the logged-in user ID
    int eventId = event.getId();  // Get the event ID

    try {
        // Create the EventAttendee using the factory

        Optional<EventAttendee> existingAttendee = eventAttendeeFactory.get(eventId);

        EventAttendee attendee = existingAttendee.get();
        eventAttendeeFactory.save(attendee);

        // Display a success alert to the user
        showAlert(AlertType.INFORMATION, "Success", "You have successfully signed up for the event!");

    } catch (SQLException e) {
            // Show a user-friendly alert
            showAlert(AlertType.ERROR, "Error", "Failed to sign up for the event. Please try again.");
        }
    }

    // Helper method to display alerts
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
