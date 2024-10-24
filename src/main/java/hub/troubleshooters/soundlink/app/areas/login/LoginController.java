package hub.troubleshooters.soundlink.app.areas.login;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.auth.models.LoginModel;
import hub.troubleshooters.soundlink.core.auth.models.RegisterModel;
import hub.troubleshooters.soundlink.core.auth.services.LoginService;
import javafx.fxml.*;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Paint;

/**
 * Controller for the login view. Handles login attempts and routes the user on a successful login.
 */
public class LoginController {

    @FXML private Label statusLabel;
    @FXML private TextField usernameInput;
    @FXML private PasswordField passwordInput;

    private final LoginService loginService;
    private final SceneManager sceneManager;

    /**
     * Constructs a new {@code LoginController}.
     * @param loginService The service responsible for handling user logins.
     * @param sceneManager The service responsible for handling the application's scene.
     */
    @Inject
    public LoginController(LoginService loginService, SceneManager sceneManager) {
        this.loginService = loginService;
        this.sceneManager = sceneManager;
    }

    /**
     * Initializes the controller, called automatically by FXML.
     * Configures Enter keystroke actions for input fields.
     */
    @FXML
    public void initialize() {
        // Adds form submission on ENTER key pressed for convenience
        passwordInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) onLoginButtonClick();
        });
        usernameInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) onLoginButtonClick();
        });
    }

    /**
     * Sends a login request, routes user on success and displays on failure.
     */
    @FXML
    protected void onLoginButtonClick() {
        var loginResult = loginService.login(new LoginModel(usernameInput.getText(), passwordInput.getText()));
        if (loginResult.isSuccess()) {
            sceneManager.switchToOutletScene(Routes.HOME);
        } else {
            logError(loginResult.getError().getMessage());
        }
    }

    /**
     * Creates a user, routes user on success and displays an error on failure.
     */
    @FXML
    protected void onRegisterButtonClick() {
        var result = loginService.register(new RegisterModel(usernameInput.getText(), passwordInput.getText()));
        if (result.isSuccess()) {
            log("Successfully registered");
        } else {
            logError(result.getError().getMessage());
        }
    }

    /**
     * Logs a message to the status label. Colored black.
     * @param message message to be logged
     */
    private void log(String message) {
        statusLabel.setText(message);
        statusLabel.setTextFill(Paint.valueOf("black"));
    }

    /**
     * Logs an error message to the status label. Colored red.
     * @param message message to be logged
     */
    private void logError(String message) {
        statusLabel.setText(message);
        statusLabel.setTextFill(Paint.valueOf("red"));
    }
}