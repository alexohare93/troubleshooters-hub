package hub.troubleshooters.soundlink.app;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.auth.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

public class HomeController {

    @FXML private Label welcomeText;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private TextField usernameInput;
    @FXML private PasswordField passwordInput;

    private final LoginService loginService;
    private final IdentityService identityService;

    @Inject
    public HomeController(LoginService loginService, IdentityService identityService) {
        this.loginService = loginService;
        this.identityService = identityService;
    }

    @FXML
    protected void onLoginButtonClick() {
        if (identityService.getUserContext() == null) {
            login();
        } else {
            logout();
        }
    }

    @FXML
    protected void onRegisterButtonClick() {
        if (usernameInput.getText() == null || passwordInput.getText() == null) {
            logError("Username and password are required");
        }
        var result = loginService.register(usernameInput.getText(), passwordInput.getText());
        if (result.isSuccess()) {
            log("Successfully registered");
        } else {
            logError(result.getError().getMessage());
        }
    }

    @FXML
    protected void onTestAuthButtonClick() {
        if (!identityService.isAuthorized(Scope.COMMUNITY_WRITE, Scope.COMMUNITY_READ)) {
            logError("Authorization failed");
            return;
        }
        log("Authorization succeeded");
    }

    private void login() {
        var loginResult = loginService.login(usernameInput.getText(), passwordInput.getText());
        if (loginResult.isSuccess()) {
            log("Logged in successfully!");
            loginButton.setText("Logout");  // typically you'd set a watched property and set this externally
            registerButton.setDisable(true);
            usernameInput.setDisable(true);
            passwordInput.setDisable(true);
        } else {
            logError(loginResult.getError().getMessage());
        }
    }

    private void logout() {
        loginService.logout();
        log("Logged out");
        loginButton.setText("Login");   // typically you'd set a watched property and set this externally
        registerButton.setDisable(false);
        usernameInput.setDisable(false);
        passwordInput.setDisable(false);
    }

    private void logError(String error) {
        welcomeText.setText(error);
        welcomeText.setTextFill(Paint.valueOf("red"));
    }

    private void log(String message) {
        welcomeText.setText(message);
        welcomeText.setTextFill(Paint.valueOf("black"));
    }
}