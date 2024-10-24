package hub.troubleshooters.soundlink.app.areas.communities;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.core.Map;
import hub.troubleshooters.soundlink.core.communities.models.CommunityModel;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.core.communities.services.CommunityService;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.app.services.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller class responsible for managing the search functionality for communities.
 * This class allows users to search for communities by name, filter by private communities,
 * and display the search results as community cards.
 */
public class SearchCommunityController {

    private static final Logger LOGGER = Logger.getLogger(SearchCommunityController.class.getName());

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
    private final ImageUploaderService imageUploaderService;
    private final Map map;

    /**
     * Constructs a new {@code SearchCommunityController} with the necessary services.
     *
     * @param communityService The service responsible for managing community-related operations.
     * @param identityService The service responsible for managing user identity and permissions.
     * @param sceneManager The manager responsible for handling scene navigation.
     * @param imageUploaderService The service responsible for handling image uploads.
     * @param map The utility for mapping between database entities and models.
     */
    @Inject
    public SearchCommunityController(CommunityService communityService, IdentityService identityService, SceneManager sceneManager, ImageUploaderService imageUploaderService, Map map) {
        this.communityService = communityService;
        this.identityService = identityService;
        this.sceneManager = sceneManager;
        this.imageUploaderService = imageUploaderService;
        this.map = map;
    }

    /**
     * Initializes the controller. Called automatically after the FXML file is loaded.
     * Fetches and displays the list of communities.
     */
    @FXML
    public void initialize() {
        try {
            fetchCommunities();
        } catch (Exception e) {
            System.err.println("Error populating community list: " + e.getMessage());
        }
    }

    /**
     * Fetches and displays the list of communities based on the search criteria.
     * The search criteria include the text from the search field and the private filter checkbox.
     */
    @FXML
    public void fetchCommunities() {
        String searchText = searchTextField.getText();
        boolean showOnlyPrivate = privateFilterCheckbox.isSelected();
        List<Community> filteredCommunities = communityService.searchCommunities(searchText, showOnlyPrivate);
        displayCommunities(filteredCommunities);
    }

    /**
     * Creates a visual card representation of a community. The card includes the community's banner image, name, genre,
     * description, and a button that navigates to the community's details page.
     *
     * @param community The community to create a card for.
     * @return A {@link VBox} containing the visual representation of the community.
     * @throws SQLException If there is an issue fetching the community details.
     */
    private VBox createCommunityCard(Community community) throws SQLException {
        VBox vbox = new VBox();
        vbox.setSpacing(10.0);
        vbox.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 10px;");
        vbox.setPrefWidth(250);

        var communityModel = map.community(community);

        var imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(100);
        var bannerImageOpt = communityModel.bannerImage();
        if (bannerImageOpt.isPresent()) {
            var bannerImage = bannerImageOpt.get();
            var path = imageUploaderService.getFullProtocolPath(bannerImage);
            imageView.setImage(new Image(path, 250, 100, false, false, true));
        } else {
            var file = imageUploaderService.getSampleBannerImageFile(communityModel.communityId());
            imageView.setImage(new Image(imageUploaderService.getFullProtocolPath(file), 250, 100, false, false, true));
        }

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

        vbox.getChildren().addAll(imageView, nameLabel, genreLabel, descriptionLabel, detailsButton);

        return vbox;
    }

    /**
     * Displays the list of communities as visual cards in the community list container.
     *
     * @param communities The list of communities to display.
     */
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

            try {
                VBox communityCard = createCommunityCard(communities.get(i));
                row.getChildren().add(communityCard);
            } catch (SQLException e) {
                LOGGER.severe("Error creating community card: " + e.getMessage());
                // we don't need to go back to the previous screen since some cards might succeed
            }
        }
    }
}

