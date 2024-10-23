package hub.troubleshooters.soundlink.app.areas.events;


import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.areas.communities.SearchCommunityController;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.Map;
import hub.troubleshooters.soundlink.core.events.models.EventModel;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.collections.ObservableList;

import java.text.DateFormat;
import java.time.LocalDate;
import java.text.SimpleDateFormat;
import java.util.List;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.scene.control.TextField;
import hub.troubleshooters.soundlink.app.components.IntegerTextField;
import javafx.collections.FXCollections;
import org.mockito.internal.matchers.Null;

public class SearchEventController {

    private static final Logger LOGGER = Logger.getLogger(SearchEventController.class.getName());

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
    private final Map map;
    private final ImageUploaderService imageUploaderService;

    @Inject
    public SearchEventController(EventService eventService, IdentityService identityService,SceneManager sceneManager, Map map, ImageUploaderService imageUploaderService){
        this.eventService = eventService;
        this.identityService = identityService;
        this.sceneManager = sceneManager;
        this.map = map;
        this.imageUploaderService = imageUploaderService;
    }

    @FXML
    public void initialize() {
        try {
            int userId = identityService.getUserContext().getUser().getId();
            displayEvents(eventService.listUpcomingEvents(userId));
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
    private void displayEvents(List<Event> events) throws SQLException {
        eventListVBox.getChildren().clear();

        HBox row = null;
        for (int i = 0; i < events.size(); i++) {
            if (i % 2 == 0) {
                row = new HBox(10); // 10 is the spacing between community boxes
                row.setStyle("-fx-padding: 10px;");
                eventListVBox.getChildren().add(row);
            }

            VBox eventCard = createEventCard(events.get(i));
            row.getChildren().add(eventCard);
        }
    }

    private VBox createEventCard(Event event) throws SQLException{
        VBox eventCard = new VBox();
        eventCard.setSpacing(10.0);
        eventCard.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 10px;");
        eventCard.setPrefWidth(250);
        eventCard.setPrefHeight(100);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(event.getScheduled());
      
        var eventModel = map.event(event);
      
        var imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(100);
        var bannerImageOpt = eventModel.bannerImage();

        var path = bannerImageOpt
                .map(imageUploaderService::getFullProtocolPath)
                .orElseGet(() -> imageUploaderService.getFullProtocolPath(imageUploaderService.getSampleBannerImageFile(eventModel.id())));

        imageView.setImage(new Image(path, 250, 100, false, false, true));


        Label nameLabel = new Label(event.getName());
        Label descriptionLabel = new Label("Description: " + event.getDescription());
        Label locationLabel = new Label("Location: " + event.getVenue());

        Label dateLabel = new Label("Date: " + formattedDate);
        Label capacityLabel = new Label("Capacity: " + event.getCapacity());        

        Button detailsButton = new Button("Details");
        detailsButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: white;");

        // Handle sign-up logic on button click
        detailsButton.setOnAction(e -> sceneManager.navigateToEventDetailsView(event.getId()));

        eventCard.getChildren().addAll(imageView, nameLabel, descriptionLabel, locationLabel, dateLabel, capacityLabel, detailsButton);
        return eventCard;
    }

    private void searchEvents() throws SQLException {
        String textSearch = searchTextField.getText();
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        int capacity = capacityTextField.getValue();

        SearchEventModel searchModel = new SearchEventModel(
                textSearch,
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
        eventListVBox.getChildren().clear();

        if (searchResults.isEmpty()) {
            System.out.println("No search results to display.");
        } else {
            System.out.println("Displaying " + searchResults.size() + " events.");
        }

        HBox row = null;
        for (int i = 0; i < searchResults.size(); i++) {
            if (i % 2 == 0) {
                row = new HBox(10);
                row.setStyle("-fx-padding: 10px;");
                eventListVBox.getChildren().add(row);
            }

            try {
                VBox eventCard = createEventCard(searchResults.get(i));
                row.getChildren().add(eventCard);
            } catch (SQLException e) {
                LOGGER.severe("Error creating event card: " + e.getMessage());
            }
        }
    }

}
