package hub.troubleshooters.soundlink.app.areas.event;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.components.IntegerTextField;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.auth.Scope;
import hub.troubleshooters.soundlink.core.events.models.CreateEventModel;
import hub.troubleshooters.soundlink.core.events.services.EventService;
import hub.troubleshooters.soundlink.data.models.Community;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class CreateEventController {

    private final IdentityService identityService;
    private final EventService eventService;
    private final SceneManager sceneManager;

    private List<Community> communities;

    @FXML
    private ChoiceBox<String> communityChoiceBox;

    @FXML private TextField nameTextField;
    @FXML private TextArea descriptionTextArea;
    @FXML private TextField locationTextField;
    @FXML private DatePicker publishDatePicker;
    @FXML private IntegerTextField capacityTextField;

    @FXML private Label errorLabel;
    @FXML private Tooltip errorTooltip;

    @Inject
    public CreateEventController(IdentityService identityService, EventService eventService, SceneManager sceneManager) {
        this.identityService = identityService;
        this.eventService = eventService;
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        // set communities options to be communities where the user has event.write permission
        errorLabel.setVisible(false);
        errorTooltip.setShowDelay(Duration.millis(200));
        communities = identityService.getCommunities();
        communityChoiceBox.setItems(FXCollections.observableArrayList(communities.stream()
                .filter(community -> identityService.isAuthorized(community, Scope.EVENT_WRITE))
                .map(Community::getName)
                .toList()
        ));

        publishDatePicker.setValue(LocalDate.now().plusDays(1));    // default to tomorrow
    }

    @FXML
    protected void onCreateButtonClick() {
        // create model
        var name = nameTextField.getText();
        var description = descriptionTextArea.getText();
        var location = locationTextField.getText();
        var localPublishDate = publishDatePicker.getValue();
        var communityOption = communities.stream().filter(c -> c.getName().equals(communityChoiceBox.getValue())).findFirst();

        var publishDate = Date.from(localPublishDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        int communityId = communityOption.map(Community::getId).orElse(0);  // id of 0 will always result in a validation error due to autoincrement
        var capacity = capacityTextField.getText().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(capacityTextField.getText());   // should never throw due to the applied TextFormatter

        var createEventModel = new CreateEventModel(name, description, publishDate, location, capacity, communityId);

        // send to event service to validate and save
        var result = eventService.createEvent(createEventModel);
        if (result.isSuccess()) {
            sceneManager.navigate(Routes.HOME);
            sceneManager.alert(new Alert(Alert.AlertType.INFORMATION, "Event created successfully.", ButtonType.OK));
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
