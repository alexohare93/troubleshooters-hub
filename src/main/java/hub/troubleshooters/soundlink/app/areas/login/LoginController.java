package hub.troubleshooters.soundlink.app.areas.login;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.core.auth.services.LoginService;
import javafx.fxml.*;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

public class LoginController {

    @FXML private Label statusLabel;
    @FXML private TextField usernameInput;
    @FXML private PasswordField passwordInput;

    private final LoginService loginService;
    private final SceneManager sceneManager;

    @Inject
    public LoginController(LoginService loginService, SceneManager sceneManager) {
        this.loginService = loginService;
        this.sceneManager = sceneManager;
    }

    @FXML
    protected void onLoginButtonClick() {
        if (usernameInput.getText().isEmpty() || passwordInput.getText().isEmpty()) {
            logError("Username and password are required");
            return;
        }
        var loginResult = loginService.login(usernameInput.getText(), passwordInput.getText());
        if (loginResult.isSuccess()) {
            sceneManager.switchToOutletScene(Routes.HOME);
        } else {
            logError(loginResult.getError().getMessage());
        }
    }

    @FXML
    protected void onRegisterButtonClick() {
        if (usernameInput.getText().isEmpty() || passwordInput.getText().isEmpty()) {
            logError("Username and password are required");
            return;
        }
        var result = loginService.register(usernameInput.getText(), passwordInput.getText());
        if (result.isSuccess()) {
            log("Successfully registered");
        } else {
            logError(result.getError().getMessage());
        }
    }

    private void log(String message) {
        statusLabel.setText(message);
        statusLabel.setTextFill(Paint.valueOf("black"));
    }

    private void logError(String message) {
        statusLabel.setText(message);
        statusLabel.setTextFill(Paint.valueOf("red"));
    }
}