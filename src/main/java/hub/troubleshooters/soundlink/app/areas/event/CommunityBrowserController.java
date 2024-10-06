package hub.troubleshooters.soundlink.app.areas.event;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.core.events.services.CommunityService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.util.List;

public class CommunityBrowserController {

    @FXML
    private ComboBox<String> genreComboBox;

    @FXML
    private ComboBox<String> locationComboBox;

    @FXML
    private VBox communityListVBox;

    private final CommunityService communityService;

    @Inject
    public CommunityBrowserController(CommunityService communityService) {
        this.communityService = communityService;
    }

    public void initialize() {
        ObservableList<String> genres = FXCollections.observableArrayList("Rock", "Jazz", "Classical", "Hip-Hop");
        genreComboBox.setItems(genres);

        ObservableList<String> locations = FXCollections.observableArrayList("New York", "Los Angeles", "Chicago");
        locationComboBox.setItems(locations);
    }

    @FXML
    public void fetchCommunities(){
        String selectedGenre = genreComboBox.getSelectionModel().getSelectedItem();
        String selectedLocation = locationComboBox.getSelectionModel().getSelectedItem();

        // Fetch filtered communities from the service
        List<Community> filteredCommunities = communityService.searchCommunities(selectedGenre, selectedLocation);

        // Display the filtered results
        displayCommunities(filteredCommunities);
    }

    private void displayCommunities(List<Community> communities) {
        communityListVBox.getChildren().clear();

        for (Community community : communities) {
            VBox communityCard = createCommunityCard(community);
            communityListVBox.getChildren().add(communityCard);
        }
    }

    private VBox createCommunityCard(Community community) {
        VBox vbox = new VBox();
        vbox.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 10px;");
        vbox.setPrefWidth(450);
        vbox.setPrefHeight(100);

        Label nameLabel = new Label(community.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fa8072;");

        Label descriptionLabel = new Label(community.getDescription());
        Label locationLabel = new Label("Location: " + extractLocationFromDescription(community.getDescription()));

        Button joinButton = new Button("Join Community");
        joinButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: white;");
        joinButton.setOnAction(event -> joinCommunity(community));

        vbox.getChildren().addAll(nameLabel, locationLabel, descriptionLabel, joinButton);

        return vbox;
    }

    private String extractLocationFromDescription(String description) {
        return description.split(":")[1].trim(); // Assuming location is part of the description
    }

    private void joinCommunity(Community community) {
        System.out.println("Joining community: " + community.getName());
        // Implement the join functionality
    }
}

