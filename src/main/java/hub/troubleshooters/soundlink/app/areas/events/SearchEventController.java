package hub.troubleshooters.soundlink.app.areas.events;


import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.data.models.Event;

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

import java.time.LocalDate;
import java.util.List;
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
    private final SceneManager sceneManager;

    @Inject
    public SearchEventController(EventService eventService, IdentityService identityService, SceneManager sceneManager) {
        this.eventService = eventService;
        this.identityService = identityService;
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        try {
            populateEventList();
        } catch (SQLException e) {
            System.err.println("Error populating event list: " + e.getMessage());
        }

        searchButton.setOnAction(event -> {
            try {
                searchEvents();
            } catch (SQLException e) {
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
        Button detailsButton = new Button("Details");
        detailsButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: white;");

        // Handle sign-up logic on button click
        detailsButton.setOnAction(e -> sceneManager.navigateToEventDetailsView(event.getId()));

        eventCard.getChildren().addAll(nameLabel, descriptionLabel, locationLabel, dateLabel, capacityLabel, detailsButton);
        return eventCard;
    }

    private void searchEvents() throws SQLException {
        // Get input from the Text Search field
        String textSearch = searchTextField.getText();
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        int capacity = capacityTextField.getValue();

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
