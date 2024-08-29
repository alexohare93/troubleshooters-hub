package hub.troubleshooters.soundlink.core.auth;

public interface LoginService {
    /**
     * Authenticates the user provided they enter the correct username and password.
     * @param username the username specified
     * @param password the plaintext password specified.
     * @return An AuthResult which may contain an error if authentication failed. Make sure to check .isSuccess() on
     * this result.
     */
    AuthResult login(String username, String password);
    boolean isLoggedIn();
    void logout();
}
