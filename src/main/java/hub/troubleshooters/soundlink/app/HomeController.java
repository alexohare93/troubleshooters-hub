package hub.troubleshooters.soundlink.app;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.auth.LoginService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class HomeController {

    @FXML
    private Label welcomeText;

    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    private final LoginService loginService;

    @Inject
    public HomeController(LoginService loginService) {
        this.loginService = loginService;
    }

    @FXML
    protected void onLoginButtonClick() {
        var loginResult = loginService.login(usernameInput.getText(), passwordInput.getText());
        if (loginResult.isSuccess()) {
            welcomeText.setText("Logged in successfully!");
        } else {
            welcomeText.setText("Login failed! Internal error: " + loginResult.getError().getMessage());
        }
    }
}