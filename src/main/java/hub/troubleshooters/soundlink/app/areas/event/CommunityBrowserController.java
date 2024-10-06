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
import javafx.scene.control.TextField;
import java.sql.SQLException;

import java.util.List;

public class CommunityBrowserController {

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    private VBox communityListVBox;

    private final CommunityService communityService;

    @Inject
    public CommunityBrowserController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @FXML
    public void initialize() {
        try {
            System.out.println("Initializing CommunityBrowserController...");

            // Fetch communities without any filter (e.g., initially show all communities)
            List<Community> communities = communityService.searchCommunities(null);
            displayCommunities(communities); // Pass the list of communities to the display method

            System.out.println("Community list populated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error populating community list: " + e.getMessage());
        }
    }

    @FXML
    public void fetchCommunities() {
        String searchText = searchTextField.getText();

        // Fetch filtered communities by name, description, or genre
        List<Community> filteredCommunities = communityService.searchCommunities(searchText);

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

        // Display community name
        Label nameLabel = new Label(community.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fa8072;");

        // Display community genre
        Label genreLabel = new Label("Genre: " + community.getGenre());
        genreLabel.setStyle("-fx-padding: 5px 0px;");

        // Display community description
        Label descriptionLabel = new Label(community.getDescription());
        descriptionLabel.setStyle("-fx-padding: 5px 0px;");

        // Join button
        Button joinButton = new Button("Join Community");
        joinButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: white;");
        joinButton.setOnAction(event -> joinCommunity(community));

        // Add components to VBox
        vbox.getChildren().addAll(nameLabel, genreLabel, descriptionLabel, joinButton);

        return vbox;
    }



    private void joinCommunity(Community community) {
        System.out.println("Joining community: " + community.getName());
        // Implement the join functionality
    }
}

