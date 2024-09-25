package hub.troubleshooters.soundlink.app.areas.event;

import hub.troubleshooters.soundlink.data.factories.SearchEventFactory;
import hub.troubleshooters.soundlink.data.factories.EventAttendeeFactory;
import hub.troubleshooters.soundlink.data.models.EventAttendee;
import hub.troubleshooters.soundlink.data.models.SearchEvent;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.sql.SQLException;

public class SearchEventController {

    @FXML
    private VBox eventListVBox;

    @FXML
    private ComboBox<String> nameComboBox;

    @FXML
    private ComboBox<String> descriptionComboBox;

    @FXML
    private ComboBox<String> venueComboBox;

    @FXML
    private ComboBox<String> capacityComboBox;

    @FXML
    private DatePicker scheduledDatePicker;

    @FXML
    private Button searchButton;

    private final SearchEventFactory searchEventFactory;
    private final IdentityService identityService;
    private final EventAttendeeFactory eventAttendeeFactory;


    @Inject
    public SearchEventController(SearchEventFactory searchEventFactory, IdentityService identityService, EventAttendeeFactory eventAttendeeFactory) {
        this.searchEventFactory = searchEventFactory;
        this.identityService = identityService;
        this.eventAttendeeFactory = eventAttendeeFactory;
    }


    @FXML
    public void initialize() {
        try {
            populateEventList();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        searchButton.setOnAction(event -> {
            try {
                searchEvents();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Add listener for name search
        nameComboBox.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            try {
                handleNameSearch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Add listener for description search
        descriptionComboBox.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            try {
                handleDescriptionSearch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Add listener for venue search
        venueComboBox.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            try {
                handleVenueSearch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Add listener for capacity search
        capacityComboBox.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            try {
                handleCapacitySearch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Add listener for scheduled date selection
        scheduledDatePicker.setOnAction(event -> {
            try {
                handleScheduledDateSelection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleNameSearch() throws SQLException {
        String searchTerm = nameComboBox.getEditor().getText();
        if (!searchTerm.isEmpty()) {
            ObservableList<String> names = searchEventFactory.searchEventNames(searchTerm);
            nameComboBox.setItems(names);
            nameComboBox.show();
        }
    }

    private void handleDescriptionSearch() throws SQLException {
        String searchTerm = descriptionComboBox.getEditor().getText();
        if (!searchTerm.isEmpty()) {
            ObservableList<String> descriptions = searchEventFactory.searchEventDescriptions(searchTerm);
            descriptionComboBox.setItems(descriptions);
            descriptionComboBox.show();
        }
    }

    private void handleVenueSearch() throws SQLException {
        String searchTerm = venueComboBox.getEditor().getText();
        if (!searchTerm.isEmpty()) {
            ObservableList<String> venues = searchEventFactory.searchEventVenues(searchTerm);
            venueComboBox.setItems(venues);
            venueComboBox.show();
        }
    }

    private void handleCapacitySearch() throws SQLException {
        String searchTerm = capacityComboBox.getEditor().getText();
        if (!searchTerm.isEmpty()) {
            ObservableList<String> capacities = searchEventFactory.searchEventCapacities(searchTerm);
            capacityComboBox.setItems(capacities);
            capacityComboBox.show();
        }
    }

    private void handleScheduledDateSelection() throws SQLException {
        LocalDate selectedDate = scheduledDatePicker.getValue();
        if (selectedDate != null) {
            ObservableList<SearchEvent> eventsOnDate = searchEventFactory.searchEventsByScheduledDate(selectedDate);
            updateEventList(eventsOnDate);
        }
    }

    /**
     * Populates the event list in the UI
     * @throws SQLException if a database error occurs
     */
    private void populateEventList() throws SQLException {
        eventListVBox.getChildren().clear();

        // Fetch upcoming events for the user
        List<SearchEvent> events = listUpcomingEvents();

        for (SearchEvent event : events) {
            VBox eventCard = createEventCard(event);
            eventListVBox.getChildren().add(eventCard);
        }
    }

    private VBox createEventCard(SearchEvent event) {
        VBox eventCard = new VBox();
        eventCard.setSpacing(10.0);
        eventCard.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 10px;");

        Label nameLabel = new Label(event.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fa8072;");

        // Format the date as day-month-year using SimpleDateFormat
        Date scheduledDate = event.getScheduledDate();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = formatter.format(scheduledDate);

        Label descriptionLabel = new Label("Description: " + event.getDescription());
        Label locationLabel = new Label("Location: " + event.getVenue());
        Label dateLabel = new Label("Date: " + formattedDate);
        Label capacityLabel = new Label("Capacity: " + event.getCapacity());

        Button signUpButton = new Button("Sign Up");
        signUpButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: white;");
        signUpButton.setOnAction(e -> handleSignUp(event));

        eventCard.getChildren().addAll(nameLabel, descriptionLabel, locationLabel, dateLabel, capacityLabel, signUpButton);

        return eventCard;
    }

    public List<SearchEvent> listUpcomingEvents() throws SQLException {
        int userId = identityService.getUserContext().getUser().getId();

        List<SearchEvent> userCommunityEvents = searchEventFactory.findUserCommunityEvents(userId);
        List<SearchEvent> publicEvents = searchEventFactory.findPublicCommunityEvents(userId);

        userCommunityEvents.addAll(publicEvents);
        return userCommunityEvents;
    }

    private void handleSignUp(SearchEvent event) {
        int userId = identityService.getUserContext().getUser().getId();
        int eventId = event.getId();

        try {
            Optional<EventAttendee> existingAttendee = eventAttendeeFactory.get(eventId, userId);
            if (existingAttendee.isPresent()) {
                showAlert(AlertType.INFORMATION, "Already Signed Up", "You have already signed up for this event.");
            } else {
                int permission = 6;  // Permission for comment ability
                eventAttendeeFactory.create(eventId, userId, permission);
                showAlert(AlertType.INFORMATION, "Success", "You have successfully signed up for the event!");
            }
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Error", "Failed to sign up for the event. Please try again.");
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void searchEvents() throws SQLException {
        // Get values from the ComboBoxes and DatePicker
        String name = nameComboBox.getEditor().getText();
        String description = descriptionComboBox.getEditor().getText();
        String venue = venueComboBox.getEditor().getText();
        String capacity = capacityComboBox.getEditor().getText();
        LocalDate scheduledDate = scheduledDatePicker.getValue();

        // Perform the search based on user input
        ObservableList<SearchEvent> searchResults = searchEventFactory.searchEvents(name, description, venue, capacity, scheduledDate);

        // Update the event list with the results
        updateEventList(searchResults);
    }

    private void updateEventList(ObservableList<SearchEvent> searchResults) {
        eventListVBox.getChildren().clear();

        for (SearchEvent event : searchResults) {
            VBox eventCard = createEventCard(event);
            eventListVBox.getChildren().add(eventCard);
        }
    }
}
