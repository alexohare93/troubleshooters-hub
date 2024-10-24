package hub.troubleshooters.soundlink.app.areas.communities;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.communities.models.CommunityPostModel;
import hub.troubleshooters.soundlink.core.communities.services.CommunityService;
import hub.troubleshooters.soundlink.core.events.models.EventModel;
import hub.troubleshooters.soundlink.core.events.services.EventService;
import hub.troubleshooters.soundlink.core.communities.models.CommunityModel;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for managing the community feed view, including displaying events and posts related to a community.
 * This class allows users to switch between viewing events and posts within a community and provides navigation to
 * event details when the user selects an event.
 */
public class CommunityFeedController {

	@FXML
	private Label nameLabel;
	@FXML
	private Button eventsTab;
	@FXML
	private Button postsTab;
	@FXML
	private VBox listContainer;

	private final CommunityService communityService;
	private final EventService eventService;
	private final SceneManager sceneManager;
	private List<EventModel> events;
	private List<CommunityPostModel> posts;
	private static final Logger LOGGER = Logger.getLogger(CommunityFeedController.class.getName());
	private final Insets cardMargins = new Insets(5, 10, 5, 10);

	/**
	 * Constructs a new {@code CommunityFeedController} with the necessary services.
	 *
	 * @param communityService The service responsible for managing community-related operations.
	 * @param eventService The service responsible for managing event-related operations.
	 * @param imageUploaderService The service responsible for handling image uploads.
	 * @param identityService The service responsible for managing user identity and permissions.
	 * @param sceneManager The manager responsible for handling scene navigation.
	 */
	@Inject
	public CommunityFeedController(CommunityService communityService, EventService eventService,
								   SceneManager sceneManager) {
		this.communityService = communityService;
		this.eventService = eventService;
		this.sceneManager = sceneManager;
	}

	/**
	 * Initializes the controller. Called automatically after the FXML file is loaded.
	 */
	@FXML
	public void initialize() {
	}

	/**
	 * Loads the community feed, including events and posts, for the specified community ID.
	 *
	 * @param communityId The ID of the community to load.
	 */
	public void LoadFeed(int communityId) {
		try {
			Optional<CommunityModel> optionalCommunity = communityService.getCommunity(communityId);
			if (optionalCommunity.isEmpty()) {
				LOGGER.log(Level.SEVERE, "Community not found with ID " + communityId);
				throw new Exception("Community not found with ID " + communityId);
			}
			CommunityModel community = optionalCommunity.get();
			nameLabel.setText(community.name());
			posts = communityService.getCommunityPosts(communityId);
			events = eventService.getCommunityEvents(communityId);
			displayEvents();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error populating Community Feed: " + e.getMessage());
			System.err.println("Error populating Community Feed: " + e.getMessage());
		}
	}

	/**
	 * Handles the event when the events tab is clicked. Switches the view to display the community's events.
	 */
	@FXML
	private void eventsTabClicked() {
		eventsTab.setDisable(true);
		postsTab.setDisable(false);
		displayEvents();
	}

	/**
	 * Handles the event when the posts tab is clicked. Switches the view to display the community's posts.
	 */
	@FXML
	private void postsTabClicked() {
		postsTab.setDisable(true);
		eventsTab.setDisable(false);
		displayPosts();
	}

	/**
	 * Displays the community's events in the feed.
	 */
	private void displayEvents() {
		// clear previous list
		listContainer.getChildren().clear();
		VBox card;
		for (EventModel event : events) {
			card = createEventCard(event);
			listContainer.getChildren().add(card);
		}
	}

	/**
	 * Displays the community's posts in the feed.
	 */
	private void displayPosts() {
		// clear previous list
		listContainer.getChildren().clear();

		VBox card;
		for (CommunityPostModel post : posts) {
			card = createCommunityPostCard(post);
			listContainer.getChildren().add(card);
		}
	}

	/**
	 * Creates a card layout for displaying an event.
	 *
	 * @param event The event to be displayed.
	 * @return A {@link VBox} containing the event details.
	 */
	private VBox createEventCard(EventModel event) {
		VBox vbox = new VBox();

		vbox.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1px; " +
				"-fx-padding: 10px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
		vbox.setAlignment(Pos.TOP_LEFT);
		VBox.setMargin(vbox, cardMargins);
		vbox.setSpacing(10.0);
		vbox.setMinWidth(250);
		vbox.setMinHeight(100);

		Label eventNameLabel = new Label(event.name());
		eventNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fa8072; -fx-padding: 5px;");

		// Need to allow this to wrap
		Label descriptionLabel = new Label("Description: " + event.description());
		descriptionLabel.setStyle("-fx-padding: 5px 0px;");

		Label venueLabel = new Label("Genre: " + event.venue());
		venueLabel.setStyle("-fx-padding: 5px 0px;");

		Label scheduledLabel = new Label("Scheduled: " + event.scheduled().toString());
		scheduledLabel.setStyle("-fx-padding: 5px 0px;");

		Button detailsButton = new Button("Details");
		detailsButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: white;");

		// Handle sign-up logic on button click
		detailsButton.setOnAction(e -> sceneManager.navigateToEventDetailsView(event.id()));

		vbox.getChildren().addAll(eventNameLabel, descriptionLabel, venueLabel, scheduledLabel, detailsButton);
		VBox.setVgrow(vbox, Priority.NEVER);
		return vbox;
	}

	/**
	 * Creates a card layout for displaying a community post.
	 *
	 * @param post The post to be displayed.
	 * @return A {@link VBox} containing the post details.
	 */
	private VBox createCommunityPostCard(CommunityPostModel post) {
		VBox vbox = new VBox();

		vbox.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1px; " +
				"-fx-padding: 10px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
		vbox.setAlignment(Pos.TOP_LEFT);
		VBox.setMargin(vbox, cardMargins);
		vbox.setSpacing(10.0);
		vbox.setMinWidth(250);
		vbox.setMinHeight(100);

		Label titleLabel = new Label(post.title());
		titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fa8072; -fx-padding: 5px;");

		Label userLabel = new Label("By: " + post.user().username());
		userLabel.setStyle("-fx-padding: 5px 0px;");

		// Need to allow this to wrap
		Label contentLabel = new Label(post.content());
		contentLabel.setStyle("-fx-padding: 5px 0px;");


		Label postedLabel = new Label("Posted: " + post.created().toString());
		postedLabel.setStyle("-fx-padding: 5px 0px;");

		vbox.getChildren().addAll(titleLabel, userLabel, contentLabel, postedLabel);
		VBox.setVgrow(vbox, Priority.NEVER);
		return vbox;
	}
}
