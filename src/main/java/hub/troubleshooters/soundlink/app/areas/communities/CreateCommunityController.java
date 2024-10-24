package hub.troubleshooters.soundlink.app.areas.communities;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.communities.services.CommunityService;
import hub.troubleshooters.soundlink.core.events.services.EventService;
import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.core.communities.models.CreateCommunityModel;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.io.File;
import java.util.List;

/**
 * Controller class responsible for handling the creation of a new community.
 * This class manages user input for creating a new community, including providing a name, description, genre,
 * and optional banner image, as well as the option to make the community private.
 * It also handles validation and navigation after successful or unsuccessful creation.
 */
public class CreateCommunityController {

    private final IdentityService identityService;
    private final CommunityService communityService;
    private final SceneManager sceneManager;

    private List<Community> communities;
    private File bannerImageFile;

    @FXML private TextField nameTextField;
    @FXML private TextArea descriptionTextArea;
    @FXML private TextField genreTextField;
    @FXML private Label fileNameLabel;
    @FXML private Button clearImageButton;

    @FXML private Label errorLabel;
    @FXML private Tooltip errorTooltip;
    @FXML private CheckBox privateCommunityCheckBox;

    /**
     * Constructs a new {@code CreateCommunityController} with the necessary services.
     *
     * @param identityService The service responsible for managing user identity and permissions.
     * @param eventService The service responsible for managing event-related operations.
     * @param sceneManager The manager responsible for handling scene navigation.
     * @param communityService The service responsible for managing community-related operations.
     */
    @Inject
    public CreateCommunityController(IdentityService identityService, EventService eventService, SceneManager sceneManager, CommunityService communityService) {
        this.identityService = identityService;
        this.communityService = communityService;
        this.sceneManager = sceneManager;
    }

    /**
     * Initializes the controller. Called automatically after the FXML file is loaded.
     * Sets the initial state for the UI components and hides error labels and tooltips.
     */
    @FXML
    public void initialize() {
        // set communities options to be communities where the user has event.write permission
        errorLabel.setVisible(false);
        errorTooltip.setShowDelay(Duration.millis(200));
    }

    /**
     * Handles the event when the upload button is clicked. Opens a file dialog to select an image to use as the community's banner image.
     * If a file is selected, updates the UI to reflect the file selection.
     */
    @FXML
    protected void onUploadButtonClick() {
        // open file dialog
        var file = sceneManager.openFileDialog();
        if (file == null) return;

        fileNameLabel.setText(file.getName());
        bannerImageFile = file;
        clearImageButton.setVisible(true);
    }

    /**
     * Handles the event when the clear image button is clicked. Clears the selected banner image and updates the UI accordingly.
     */
    @FXML
    protected void onClearImageButtonClick() {
        bannerImageFile = null;
        clearImageButton.setVisible(false);
        fileNameLabel.setText("No file selected");
    }

    /**
     * Handles the event when the create button is clicked. Attempts to create a new community based on user input.
     * If the creation is successful, navigates to the home screen and displays a success alert.
     * If validation fails, displays the validation errors.
     */
    @FXML
    protected void onCreateButtonClick() {
        // create model
        var name = nameTextField.getText();
        var description = descriptionTextArea.getText();
        var genre = genreTextField.getText();
        boolean isPrivate = privateCommunityCheckBox.isSelected();

        // set id to 0 because id is auto-incremented by the db, so don't need to provide one when inserting a new record
        var createCommunityModel = new CreateCommunityModel(0, name, description, genre, null, bannerImageFile, isPrivate);

        // send to community service to validate and save
        var result = communityService.createCommunity(createCommunityModel);

        if (result.isSuccess()) {
            sceneManager.navigate(Routes.HOME);
            sceneManager.alert(new Alert(Alert.AlertType.INFORMATION, "Community created successfully.", ButtonType.OK));
            return;
        }

        // validation errors
        errorLabel.setVisible(true);
        errorTooltip.setText(result.getErrors().stream()
                .map(err -> "• " + err.getMessage() + "\n")
                .reduce("", (a, b) -> a + b)
        );
    }
}
