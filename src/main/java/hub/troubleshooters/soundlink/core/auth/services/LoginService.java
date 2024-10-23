package hub.troubleshooters.soundlink.core.auth.services;

import hub.troubleshooters.soundlink.core.auth.models.LoginModel;
import hub.troubleshooters.soundlink.core.auth.models.RegisterModel;
import hub.troubleshooters.soundlink.core.auth.validation.AuthResult;

/**
 * Interface responsible for handling user authentication and registration operations.
 * This service provides methods for logging in, registering new users, and logging out the current user.
 */
public interface LoginService {

    /**
     * Authenticates the user based on the provided login model, which contains the username and password.
     *
     * @param model The {@link LoginModel} containing the user's login credentials.
     * @return An {@link AuthResult} that indicates whether authentication was successful.
     *         If authentication fails, the result will contain an error. Be sure to check {@code .isSuccess()}
     *         on the returned result to determine success or failure.
     */
    AuthResult login(LoginModel model);

    /**
     * Registers a new user with the system based on the provided registration model,
     * which contains the username and password.
     *
     * @param model The {@link RegisterModel} containing the user's registration details.
     * @return An {@link AuthResult} that indicates whether registration was successful.
     *         If registration fails, the result will contain an error. Be sure to check {@code .isSuccess()}
     *         on the returned result to determine success or failure.
     */
    AuthResult register(RegisterModel model);

    /**
     * Logs out the currently authenticated user, clearing their session.
     */
    void logout();
}