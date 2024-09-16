package hub.troubleshooters.soundlink.core.auth.services;

import hub.troubleshooters.soundlink.core.auth.models.LoginModel;
import hub.troubleshooters.soundlink.core.auth.models.RegisterModel;
import hub.troubleshooters.soundlink.core.auth.validation.AuthResult;

public interface LoginService {
    /**
     * Authenticates the user provided they enter the correct username and password.
     * @param model the login model
     * @return An AuthResult which may contain an error if authentication failed. Make sure to check .isSuccess() on
     * this result.
     */
    AuthResult login(LoginModel model);

    /**
     * Registers a new user with the given username and password.
     * @param model the register model
     * @return An AuthResult which may contain an error if registration failed. Make sure to check .isSuccess() on
     * this result.
     */
    AuthResult register(RegisterModel model);

    /**
     * Logs out the current user.
     */
    void logout();
}
