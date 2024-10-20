package hub.troubleshooters.soundlink.app.areas.home;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.auth.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeController {
    private final SceneManager sceneManager;

    @Inject
    public HomeController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    protected void onBrowseCommunitiesButtonClick() {
        sceneManager.navigate(Routes.SEARCH_COMMUNITIES);
    }
}