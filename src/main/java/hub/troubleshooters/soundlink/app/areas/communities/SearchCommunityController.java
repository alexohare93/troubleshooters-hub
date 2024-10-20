package hub.troubleshooters.soundlink.app.areas.communities;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.core.communities.services.CommunityService;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.app.services.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import java.util.List;

public class SearchCommunityController {

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    private CheckBox privateFilterCheckbox;

    @FXML
    private VBox communityListContainer;

    private final CommunityService communityService;

    private final IdentityService identityService;

    private final SceneManager sceneManager;

    @Inject
    public SearchCommunityController(CommunityService communityService, IdentityService identityService, SceneManager sceneManager) {
        this.communityService = communityService;
        this.identityService = identityService;
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        try {

            // no communities fetched when first initialized, so it follows the same functionality as search event controller
            communityListContainer.getChildren().clear();
        } catch (Exception e) {
            System.err.println("Error populating community list: " + e.getMessage());
        }
    }

    @FXML
    public void fetchCommunities() {
        String searchText = searchTextField.getText();
        boolean showOnlyPrivate = privateFilterCheckbox.isSelected();
        List<Community> filteredCommunities = communityService.searchCommunities(searchText, showOnlyPrivate);
        displayCommunities(filteredCommunities);
    }


    private VBox createCommunityCard(Community community) {
        VBox vbox = new VBox();
        vbox.setSpacing(10.0);
        vbox.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 10px;");
        vbox.setPrefWidth(250);
        vbox.setPrefHeight(100);

        Label nameLabel = new Label(community.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fa8072;");

        Label genreLabel = new Label("Genre: " + community.getGenre());
        genreLabel.setStyle("-fx-padding: 5px 0px;");

        // Need to allow this to wrap
        Label descriptionLabel = new Label("Description: " + community.getDescription());
        descriptionLabel.setStyle("-fx-padding: 5px 0px;");

        Button detailsButton = new Button("Details");
        detailsButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: white;");

        detailsButton.setOnAction(e -> sceneManager.navigateToCommunityDetailsView(community.getId()));

        vbox.getChildren().addAll(nameLabel, genreLabel, descriptionLabel, detailsButton);

        return vbox;
    }

    private void displayCommunities(List<Community> communities) {
        // Clear previous results
        communityListContainer.getChildren().clear();

        HBox row = null;
        for (int i = 0; i < communities.size(); i++) {
            if (i % 2 == 0) {
                row = new HBox(10);
                row.setStyle("-fx-padding: 10px;");
                communityListContainer.getChildren().add(row);
            }

            VBox communityCard = createCommunityCard(communities.get(i));
            row.getChildren().add(communityCard);
        }
    }
}

