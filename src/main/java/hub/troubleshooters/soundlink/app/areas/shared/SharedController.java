package hub.troubleshooters.soundlink.app.areas.shared;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.auth.IdentityService;
import hub.troubleshooters.soundlink.core.auth.LoginService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.Pane;

public class SharedController {
    private final LoginService loginService;
    private final IdentityService identityService;
    private final SceneManager sceneManager;

    @FXML
    protected MenuButton usernameMenuButton;

    @FXML
    private Pane outlet;

    @Inject
    public SharedController(LoginService loginService, IdentityService identityService, SceneManager sceneManager) {
        this.loginService = loginService;
        this.identityService = identityService;
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        usernameMenuButton.setText(identityService.getUserContext().getUser().username());
    }

    public void setOutlet(Parent content) {
        outlet.getChildren().clear();
        outlet.getChildren().add(content);
    }

    @FXML
    protected void onLogoutButtonPressed() {
        loginService.logout();
        sceneManager.switchToScene("areas/login/login-view.fxml");
    }

    @FXML
    protected void onEventsButtonPressed() {
        sceneManager.switchToOutletScene("areas/home/test-view.fxml");
    }

    @FXML
    protected void onHomeButtonPressed() {
        sceneManager.switchToOutletScene("areas/home/home-view.fxml");
    }
}
