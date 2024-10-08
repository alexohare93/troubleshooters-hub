package hub.troubleshooters.soundlink.app.areas.event;


import hub.troubleshooters.soundlink.data.models.Event;
import hub.troubleshooters.soundlink.data.factories.EventFactory;

import hub.troubleshooters.soundlink.core.events.services.EventService;
import hub.troubleshooters.soundlink.core.events.models.SearchEventModel;
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
import javafx.scene.control.TextField;
import hub.troubleshooters.soundlink.app.components.IntegerTextField;
import javafx.collections.FXCollections;

public class SearchEventController {

    @FXML
    private VBox eventListVBox;

    @FXML
    private TextField searchTextField;

    @FXML
    private IntegerTextField capacityTextField;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private ComboBox<String> eventTypeComboBox;

    @FXML
    private Button searchButton;

    private final EventService eventService;

    private final IdentityService identityService;

    @Inject
    public SearchEventController(EventService eventService, IdentityService identityService) {
        this.eventService = eventService;
        this.identityService = identityService;
    }

    @FXML
    public void initialize() {
        try {
            System.out.println("Initializing SearchEventController...");
            populateEventList();
            System.out.println("Event list populated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error populating event list: " + e.getMessage());
        }

        searchButton.setOnAction(event -> {
            System.out.println("Search button clicked.");
            try {
                searchEvents();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error during search: " + e.getMessage());
            }
        });
    }

    /**
     * Populates the event list in the UI
     * @throws SQLException if a database error occurs
     */
    private void populateEventList() throws SQLException {
        eventListVBox.getChildren().clear();

        // Fetch upcoming events for the user
        int userId = identityService.getUserContext().getUser().getId();  // Get the user ID
        List<Event> events = eventService.listUpcomingEvents(userId);
        System.out.println("Events:" + events);

        for (Event event : events) {
            VBox eventCard = createEventCard(event);
            eventListVBox.getChildren().add(eventCard);
        }
    }

    private VBox createEventCard(Event event) {
        VBox eventCard = new VBox();
        eventCard.setSpacing(10.0);
        eventCard.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 10px;");

        Label nameLabel = new Label(event.getName());
        Label descriptionLabel = new Label("Description: " + event.getDescription());
        Label locationLabel = new Label("Location: " + event.getVenue());
        Label dateLabel = new Label("Date: " + event.getScheduled().toString());
        Label capacityLabel = new Label("Capacity: " + event.getCapacity());
        Button signUpButton = new Button("Sign Up");
        signUpButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: white;");

        // Handle sign-up logic on button click
        signUpButton.setOnAction(e -> handleSignUp(event));

        eventCard.getChildren().addAll(nameLabel, descriptionLabel, locationLabel, dateLabel, capacityLabel,signUpButton);
        return eventCard;
    }

    private void handleSignUp(Event event) {
        try {
            int userId = identityService.getUserContext().getUser().getId();
            boolean signUpSuccess = eventService.signUpForEvent(event.getId(), userId);

            if (signUpSuccess) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "You have successfully signed up for the event!");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Already Signed Up", "You have already signed up for this event.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to sign up for the event. Please try again.");
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void searchEvents() throws SQLException {
        // Get input from the Text Search field
        String textSearch = searchTextField.getText();
        String capacityText = capacityTextField.getText();
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        int capacity = 0;
        if (!capacityText.isEmpty()) {
            try {
                capacity = Integer.parseInt(capacityText);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Capacity must be a valid number.");
                return;
            }
        }

        // Create SearchEventModel
        SearchEventModel searchModel = new SearchEventModel(
                textSearch.isEmpty() ? null : textSearch,   // Combined text search field
                fromDate != null ? java.sql.Date.valueOf(fromDate) : null,
                toDate != null ? java.sql.Date.valueOf(toDate) : null,
                capacity,
                0  // Assuming no communityId filter
        );

        // Get filtered events
        List<Event> searchResults = eventService.search(searchModel);
        ObservableList<Event> observableSearchResults = FXCollections.observableArrayList(searchResults);

        updateEventList(observableSearchResults);
    }



    private void updateEventList(ObservableList<Event> searchResults) {
        System.out.println("Updating event list. Clearing previous events...");
        eventListVBox.getChildren().clear();

        if (searchResults.isEmpty()) {
            System.out.println("No search results to display.");
        } else {
            System.out.println("Displaying " + searchResults.size() + " events.");
        }

        for (Event event : searchResults) {
            VBox eventCard = createEventCard(event);
            eventListVBox.getChildren().add(eventCard);
            System.out.println("Added event card for: " + event.getName());
        }
    }
}
