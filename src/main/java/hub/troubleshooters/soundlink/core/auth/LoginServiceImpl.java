package hub.troubleshooters.soundlink.core.auth;

import java.util.Optional;

public class LoginServiceImpl implements LoginService {

    private boolean loggedIn = false;

    /**
     * Authenticates the user provided they enter the correct username and password.
     * @param username
     * @param password
     * @return An AuthResult
     */
    @Override
    public AuthResult login(String username, String password) {
        if (!username.equals("test") || !password.equals("123")) {
            return new AuthResult(new AuthException("Incorrect username or password"));
        }

        loggedIn = true;
        return new AuthResult();
    }

    @Override
    public boolean isLoggedIn() {
        return loggedIn;
    }

    @Override
    public void logout() {
        loggedIn = false;
    }
}
