package hub.troubleshooters.soundlink.app.areas.communities;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.communities.models.CommunityPostModel;
import hub.troubleshooters.soundlink.core.communities.services.CommunityService;
import hub.troubleshooters.soundlink.core.events.models.EventModel;
import hub.troubleshooters.soundlink.core.events.services.EventService;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
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
	private final ImageUploaderService imageUploaderService;
	private final IdentityService identityService;
	private final SceneManager sceneManager;

	private CommunityModel community;
	private List<EventModel> events;
	private List<CommunityPostModel> posts;
	private static final Logger LOGGER = Logger.getLogger(CommunityFeedController.class.getName());

	@Inject
	public CommunityFeedController(CommunityService communityService, EventService eventService, ImageUploaderService imageUploaderService,
								   IdentityService identityService, SceneManager sceneManager) {
		this.communityService = communityService;
		this.eventService = eventService;
		this.imageUploaderService = imageUploaderService;
		this.identityService = identityService;
		this.sceneManager = sceneManager;
	}

	@FXML
	public void initialize() {
	}

	public void LoadFeed(int communityId) {
		try {
			Optional<CommunityModel> optionalCommunity = communityService.getCommunity(communityId);
			if (optionalCommunity.isEmpty()) {
				LOGGER.log(Level.SEVERE, "Community not found with ID " + communityId);
				throw new Exception("Community not found with ID " + communityId);
			}
			community = optionalCommunity.get();
			nameLabel.setText(community.name());
			posts = communityService.getCommunityPosts(communityId);
			events = eventService.getCommunityEvents(communityId);
			displayEvents();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error populating Community Feed: " + e.getMessage());
			System.err.println("Error populating Community Feed: " + e.getMessage());
		}
	}

	@FXML
	private void eventsTabClicked() {
		eventsTab.setDisable(true);
		postsTab.setDisable(false);
		displayEvents();
	}

	@FXML
	private void postsTabClicked() {
		postsTab.setDisable(true);
		eventsTab.setDisable(false);
		displayPosts();
	}

	private void displayEvents() {
		// clear previous list
		listContainer.getChildren().clear();
		VBox card;
		for (EventModel event : events) {
			card = createEventCard(event);
			listContainer.getChildren().add(card);
		}
	}

	private void displayPosts() {
		// clear previous list
		listContainer.getChildren().clear();

		VBox card;
		for (CommunityPostModel post : posts) {
			card = createCommunityPostCard(post);
			listContainer.getChildren().add(card);
		}
	}

	private VBox createEventCard(EventModel event) {
		VBox vbox = new VBox();

		vbox.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1px; " +
				"-fx-padding: 10px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
		vbox.setAlignment(Pos.TOP_LEFT);
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

	private VBox createCommunityPostCard(CommunityPostModel post) {
		VBox vbox = new VBox();

		vbox.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1px; " +
				"-fx-padding: 10px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
		vbox.setAlignment(Pos.TOP_LEFT);
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
