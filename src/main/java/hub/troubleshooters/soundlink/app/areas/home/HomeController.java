package hub.troubleshooters.soundlink.app.areas.home;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.auth.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeController {

    @FXML private Label welcomeText;

    private final LoginService loginService;
    private final IdentityService identityService;
    private final SceneManager sceneManager;

    @Inject
    public HomeController(LoginService loginService, IdentityService identityService, SceneManager sceneManager) {
        this.loginService = loginService;
        this.identityService = identityService;
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        welcomeText.setText("Welcome " + identityService.getUserContext().getUser().username());
    }

    @FXML
    protected void onLogoutButtonClick() {
        loginService.logout();
        sceneManager.switchToScene("areas/login/login-view.fxml");
    }
}