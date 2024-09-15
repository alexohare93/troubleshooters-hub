package hub.troubleshooters.soundlink.app.areas.event;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.auth.IdentityService;
import hub.troubleshooters.soundlink.core.auth.Scope;
import hub.troubleshooters.soundlink.data.models.Community;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.util.List;

public class CreateEventController {

    private final IdentityService identityService;

    private List<Community> communities;

    @FXML
    private ChoiceBox<String> communityChoiceBox;

    @Inject
    public CreateEventController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @FXML
    public void initialize() {
        // set communities options to be communities where the user has event.write permission
        communities = identityService.getCommunities();
        communityChoiceBox.setItems(FXCollections.observableArrayList(communities.stream()
                .filter(community -> identityService.isAuthorized(community, Scope.EVENT_WRITE))
                .map(Community::getName)
                .toList()
        ));
    }
}
