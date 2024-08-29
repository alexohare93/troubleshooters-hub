package hub.troubleshooters.soundlink.core.auth;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;

import java.sql.Statement;
import java.util.Optional;

public class LoginServiceImpl implements LoginService {

    private boolean loggedIn = false;
    private DatabaseConnection connection;

    @Inject
    public LoginServiceImpl(DatabaseConnection connection) {
        this.connection = connection;
    }

    /**
     * Authenticates the user provided they enter the correct username and password.
     * @param username the username specified
     * @param password the plaintext password specified.
     * @return An AuthResult which may contain an error if authentication failed. Make sure to check .isSuccess() on
     * this result.
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
