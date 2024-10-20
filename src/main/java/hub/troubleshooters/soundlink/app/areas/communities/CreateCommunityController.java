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

    @Inject
    public CreateCommunityController(IdentityService identityService, EventService eventService, SceneManager sceneManager, CommunityService communityService) {
        this.identityService = identityService;
        this.communityService = communityService;
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        // set communities options to be communities where the user has event.write permission
        errorLabel.setVisible(false);
        errorTooltip.setShowDelay(Duration.millis(200));
    }

    @FXML
    protected void onUploadButtonClick() {
        // open file dialog
        var file = sceneManager.openFileDialog();
        if (file == null) return;

        fileNameLabel.setText(file.getName());
        bannerImageFile = file;
        clearImageButton.setVisible(true);
    }

    @FXML
    protected void onClearImageButtonClick() {
        bannerImageFile = null;
        clearImageButton.setVisible(false);
        fileNameLabel.setText("No file selected");
    }

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
