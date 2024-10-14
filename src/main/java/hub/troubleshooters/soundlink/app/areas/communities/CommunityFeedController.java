package hub.troubleshooters.soundlink.app.areas.communities;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.communities.services.CommunityService;
import hub.troubleshooters.soundlink.core.events.services.EventService;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import hub.troubleshooters.soundlink.core.communities.models.CommunityModel;
import hub.troubleshooters.soundlink.data.models.CommunityPost;
import hub.troubleshooters.soundlink.data.models.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
	private List<Event> events;
	private List<CommunityPost> posts;
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
	public void initialise() {

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

	@FXML
	private void displayEvents() {
		// clear previous list
		listContainer.getChildren().clear();
		VBox card;
		for (Event event : events) {
			card = createEventCard(event);
			listContainer.getChildren().add(card);
		}
	}

	@FXML
	private void displayPosts() {
		// clear previous list
		listContainer.getChildren().clear();

		VBox card;
		for (CommunityPost post : posts) {
			card = createCommunityPostCard(post);
			listContainer.getChildren().add(card);
		}
	}

	private VBox createEventCard(Event event) {
		VBox vbox = new VBox();

		vbox.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 10px; -fx-border-radius: 10px;");
		vbox.setSpacing(10.0);
		vbox.setPrefWidth(250);
		vbox.setPrefHeight(100);

		Label eventNameLabel = new Label(event.getName());
		eventNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fa8072;");

		// Need to allow this to wrap
		Label descriptionLabel = new Label("Description: " + event.getDescription());
		descriptionLabel.setStyle("-fx-padding: 5px 0px;");

		Label venueLabel = new Label("Genre: " + event.getVenue());
		venueLabel.setStyle("-fx-padding: 5px 0px;");

		Label scheduledLabel = new Label("Scheduled: " + event.getScheduled().toString());
		scheduledLabel.setStyle("-fx-padding: 5px 0px;");

		Button detailsButton = new Button("Details");
		detailsButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: white;");

		// Handle sign-up logic on button click
		detailsButton.setOnAction(e -> sceneManager.navigateToEventDetailsView(event.getId()));

		vbox.getChildren().addAll(eventNameLabel, descriptionLabel, venueLabel, scheduledLabel, detailsButton);
		return vbox;
	}

	private VBox createCommunityPostCard(CommunityPost post) {
		VBox vbox = new VBox();

		vbox.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 10px; -fx-border-radius: 10px;");
		vbox.setSpacing(10.0);
		vbox.setPrefWidth(250);
		vbox.setPrefHeight(100);

		Label titleLabel = new Label(post.getTitle());
		titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fa8072;");

		Label userLabel = new Label("By: " + post.getUserid());
		userLabel.setStyle("-fx-padding: 5px 0px;");

		// Need to allow this to wrap
		Label contentLabel = new Label(post.getContent());
		contentLabel.setStyle("-fx-padding: 5px 0px;");


		Label postedLabel = new Label("Posted: " + post.getCreated().toString());
		postedLabel.setStyle("-fx-padding: 5px 0px;");

		vbox.getChildren().addAll(titleLabel, userLabel, contentLabel, postedLabel);
		return vbox;
	}
}
