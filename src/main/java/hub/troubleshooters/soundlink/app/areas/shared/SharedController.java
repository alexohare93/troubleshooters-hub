package hub.troubleshooters.soundlink.app.areas.shared;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.auth.services.LoginService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;

public class SharedController {
    private final LoginService loginService;
    private final IdentityService identityService;
    private final SceneManager sceneManager;

    @FXML
    private Pane root;

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
    private HBox navBar;

    @FXML
    private Button eventButton, communityButton, profileButton, settingsButton;

    @FXML
    public void initialize() {
        usernameMenuButton.setText(identityService.getUserContext().getUser().getUsername());
        navBar.widthProperty().addListener((obs, oldVal, newVal) -> {
            adjustFontSize(newVal.doubleValue());
        });
    }

    private void adjustFontSize(double width) {
        double fontSize = width / 30;
        eventButton.setStyle("-fx-font-size: " + fontSize + "px;");
        communityButton.setStyle("-fx-font-size: " + fontSize + "px;");
        profileButton.setStyle("-fx-font-size: " + fontSize + "px;");
        settingsButton.setStyle("-fx-font-size: " + fontSize + "px;");
    }

    public void setOutlet(Parent content) {
        outlet.getChildren().clear();
        outlet.getChildren().add(content);
    }

    @FXML
    protected void onLogoutButtonPressed() {
        loginService.logout();
        sceneManager.switchToScene(Routes.LOGIN);
    }

    @FXML
    protected void onEventsButtonPressed() {
        sceneManager.switchToOutletScene("areas/events/search_event_view.fxml");
    }

    @FXML
    protected void onHomeButtonPressed() {
        sceneManager.switchToOutletScene(Routes.HOME);
    }

    @FXML
    protected void createEventsPressed()  {sceneManager.switchToOutletScene("areas/events/create_event_view.fxml");}

    @FXML
    protected void searchEventsPressed()  {sceneManager.switchToOutletScene("areas/events/search_event_view.fxml");}

    @FXML
    protected void createCommunitiesPressed()  {sceneManager.switchToOutletScene("areas/communities/create_community_view.fxml");}

    @FXML
    protected void searchCommunitiesPressed()  {sceneManager.switchToOutletScene("areas/communities/search_community_view.fxml");}

    @FXML
    protected void onProfileButtonPressed()  {sceneManager.switchToOutletScene("areas/user/user_profile_view.fxml");}
}
