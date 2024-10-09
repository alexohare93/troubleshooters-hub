package hub.troubleshooters.soundlink.app.areas.community;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.core.events.services.CommunityService;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;


import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import java.util.List;

public class SearchCommunityController {

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    private VBox communityListContainer;

    private final CommunityService communityService;

    private final IdentityService identityService;

    @Inject
    public SearchCommunityController(CommunityService communityService, IdentityService identityService) {
        this.communityService = communityService;
        this.identityService = identityService;
    }

    @FXML
    public void initialize() {
        try {         

            // fetch communities without any filter (e.g., initially show all communities)
            List<Community> communities = communityService.searchCommunities(null);
            displayCommunities(communities);            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error populating community list: " + e.getMessage());
        }
    }

    @FXML
    public void fetchCommunities() {
        String searchText = searchTextField.getText();

        List<Community> filteredCommunities = communityService.searchCommunities(searchText);

        displayCommunities(filteredCommunities);
    }


    private VBox createCommunityCard(Community community) {
        VBox vbox = new VBox();
        vbox.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 10px;");
        vbox.setSpacing(10.0);
        vbox.setPrefWidth(250);
        vbox.setPrefHeight(100);

        Label nameLabel = new Label(community.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fa8072;");

        Label genreLabel = new Label("Genre: " + community.getGenre());
        genreLabel.setStyle("-fx-padding: 5px 0px;");

        //Need to allow this to wrap
        Label descriptionLabel = new Label("Description: " + community.getDescription());
        descriptionLabel.setStyle("-fx-padding: 5px 0px;");

        Button joinButton = new Button("Join Community");
        joinButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: white;");
        joinButton.setOnAction(event -> joinCommunity(community));

        vbox.getChildren().addAll(nameLabel, genreLabel, descriptionLabel, joinButton);

        return vbox;
    }


    private void joinCommunity(Community community) {
        System.out.println("Joining community: " + community.getName());
        handleSignUp(community);
    }

    private void handleSignUp(Community community) {
        try {
            int userId = identityService.getUserContext().getUser().getId();
            boolean signUpSuccess = communityService.signUpForCommunity(userId, community.getId());

            if (signUpSuccess) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "You have successfully signed up for the community!");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Already Signed Up", "You have already signed up for this community.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to sign up for the community. Please try again.");
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void displayCommunities(List<Community> communities) {
        // Clear previous results
        communityListContainer.getChildren().clear();

        // Create a new HBox to hold each row of community boxes
        HBox row = null;
        for (int i = 0; i < communities.size(); i++) {
            // Create a new row (HBox) every two communities
            if (i % 2 == 0) {
                row = new HBox(10); // 10 is the spacing between community boxes
                row.setStyle("-fx-padding: 10px;");
                communityListContainer.getChildren().add(row);
            }

            // Create a community card using the provided method
            VBox communityCard = createCommunityCard(communities.get(i));

            row.getChildren().add(communityCard);
        }
    }
}

