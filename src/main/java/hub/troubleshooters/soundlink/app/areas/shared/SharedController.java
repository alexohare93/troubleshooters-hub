package hub.troubleshooters.soundlink.app.areas.shared;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.services.NavigationListener;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.auth.services.LoginService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;

public class SharedController implements NavigationListener {
    private final LoginService loginService;
    private final IdentityService identityService;
    private final SceneManager sceneManager;

    private final BooleanProperty hasHistoryProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty hasFutureProperty = new SimpleBooleanProperty(false);

    @FXML private Pane root;
    @FXML private MenuButton usernameMenuButton;
    @FXML private Pane outlet;
    @FXML private Button backButton;
    @FXML private Button forwardButton;

    @Inject
    public SharedController(LoginService loginService, IdentityService identityService, SceneManager sceneManager) {
        this.loginService = loginService;
        this.identityService = identityService;
        this.sceneManager = sceneManager;

        sceneManager.addNavigationListener(this);
    }

    @FXML
    public void initialize() {
        usernameMenuButton.setText(identityService.getUserContext().getUser().getUsername());
        backButton.disableProperty().bind(hasHistoryProperty.not());
        forwardButton.disableProperty().bind(hasFutureProperty.not());
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
    protected void onHomeButtonPressed() {
        sceneManager.navigate(Routes.HOME);
    }

    @FXML
    protected void createEventsPressed()  {sceneManager.navigate(Routes.CREATEEVENTS);}

    @FXML
    protected void searchEventsPressed()  {sceneManager.navigate(Routes.SEARCHEVENTS);}

    @FXML
    protected void createCommunitiesPressed()  {sceneManager.navigate(Routes.CREATECOMMUNITIES);}

    @FXML
    protected void searchCommunitiesPressed()  {sceneManager.navigate(Routes.SEARCHCOMMUNITIES);}

    @FXML
    protected void onProfileButtonPressed()  {sceneManager.navigate(Routes.PROFILE);}

    @FXML
    protected void onBackButtonPressed() {
        sceneManager.navigateBack();
    }

    @FXML
    protected void onForwardButtonPressed() {
        sceneManager.navigateForward();
    }

    @Override
    public void onNavigationStateChange() {
        updateNavigationState();
    }

    private void updateNavigationState() {
        hasHistoryProperty.set(sceneManager.hasHistory());
        hasFutureProperty.set(sceneManager.hasFuture());
    }
}
