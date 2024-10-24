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
import javafx.scene.control.Button;

/**
 * Controller for the shared view of most pages in the application. Manages routing for button on the navbar.
 */
public class SharedController implements NavigationListener {
    private final LoginService loginService;
    private final IdentityService identityService;
    private final SceneManager sceneManager;

    private final BooleanProperty hasHistoryProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty hasFutureProperty = new SimpleBooleanProperty(false);

    @FXML private Pane root;
    @FXML private MenuButton usernameMenuButton;
    @FXML private Pane outlet;
//    @FXML private Button backButton;
//    @FXML private Button forwardButton;

    /**
     * Construct a new {@code SharedController}.
     * @param loginService Service responsible for user logins.
     * @param identityService Service responsible for user identities.
     * @param sceneManager Service responsible for the application's scene.
     */
    @Inject
    public SharedController(LoginService loginService, IdentityService identityService, SceneManager sceneManager) {
        this.loginService = loginService;
        this.identityService = identityService;
        this.sceneManager = sceneManager;

        sceneManager.addNavigationListener(this);
    }

    /**
     * Initializes the controller, called automatically by FXML. Sets the username menu button with the users name.
     */
    @FXML
    public void initialize() {
        usernameMenuButton.setText(identityService.getUserContext().getUser().getUsername());
//        backButton.disableProperty().bind(hasHistoryProperty.not());
//        forwardButton.disableProperty().bind(hasFutureProperty.not());
    }

    /**
     * Updates the outlet javaFX {@link Pane} with new content.
     * @param content A javaFX {@link Parent}. Will be added as a child of the outlet pane.
     */
    public void setOutlet(Parent content) {
        outlet.getChildren().clear();
        outlet.getChildren().add(content);
    }

    /**
     * Logs the user out and routes them to the login screen.
     */
    @FXML
    protected void onLogoutButtonPressed() {
        loginService.logout();
        sceneManager.switchToScene(Routes.LOGIN);
    }

    /**
     * Routes the user to the home view.
     */
    @FXML
    protected void onHomeButtonPressed() {
        sceneManager.navigate(Routes.HOME);
    }

    /**
     * Routes the user to the create event view.
     */
    @FXML
    protected void createEventsPressed()  {
        sceneManager.navigate(Routes.CREATE_EVENT);
    }

    /**
     * Routes the user to the search event view.
     */
    @FXML
    protected void searchEventsPressed()  {
        sceneManager.navigate(Routes.SEARCH_EVENTS);
    }

    /**
     * Routes the user to the create community view.
     */
    @FXML
    protected void createCommunitiesPressed() {
        sceneManager.navigate(Routes.CREATE_COMMUNITY);
    }

    /**
     * Routes the user to the search communities view.
     */
    @FXML
    protected void searchCommunitiesPressed() {
        sceneManager.navigate(Routes.SEARCH_COMMUNITIES);
    }

    /**
     * Routes the user to the user profile view.
     */
    @FXML
    protected void onProfileButtonPressed() {
        sceneManager.navigate(Routes.PROFILE);
    }

//    @FXML
//    protected void onBackButtonPressed() {
//        sceneManager.navigateBack();
//    }
//
//    @FXML
//    protected void onForwardButtonPressed() {
//        sceneManager.navigateForward();
//    }

    /**
     * Routes the user to the admin view.
     */
    @FXML
    protected void onNotificationButtonClick() {
        sceneManager.navigate(Routes.ADMIN);
    }

    /**
     * Updates the navigation state.
     */
    @Override
    public void onNavigationStateChange() {
        updateNavigationState();
    }

    /**
     * Sets the {@code hasHistoryProperty} and {@code hasFutureProperty} from the {@link SceneManager}.
     */
    private void updateNavigationState() {
        hasHistoryProperty.set(sceneManager.hasHistory());
        hasFutureProperty.set(sceneManager.hasFuture());
    }
}
