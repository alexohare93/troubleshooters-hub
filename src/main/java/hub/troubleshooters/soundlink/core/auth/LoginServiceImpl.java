package hub.troubleshooters.soundlink.core.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class LoginServiceImpl implements LoginService {

    private boolean loggedIn = false;
    private final DatabaseConnection connection;

    @Inject
    public LoginServiceImpl(DatabaseConnection connection) {
        this.connection = connection;
    }

    @Override
    public AuthResult login(String username, String password) {
        var sql = "SELECT HashedPassword FROM Users WHERE Username = ?";
        AuthResult authResult;
        try {
            authResult = connection.executeQuery(sql, preparedStatement -> {
                preparedStatement.setString(1, username);
            }, resultSet -> {
                if (resultSet.next()) {
                    var savedHashedPassword = resultSet.getString("HashedPassword");
                    if (verifyPassword(password, savedHashedPassword)) {
                        loggedIn = true;
                        return new AuthResult(); // successful login
                    } else {
                        // incorrect password
                        return new AuthResult(new AuthException("Incorrect username or password"));
                    }
                } else {
                    // incorrect username
                    return new AuthResult(new AuthException("Incorrect username or password"));
                }
            });
        } catch (SQLException e) {
            authResult = new AuthResult(new AuthException("Internal server error. Please contact SoundLink support."));
        }

        return authResult;
    }

    @Override
    public boolean isLoggedIn() {
        return loggedIn;
    }

    @Override
    public void logout() {
        loggedIn = false;
    }

    private boolean verifyPassword(String password, String hashedPassword) {
        var result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
        return result.verified;
    }
}
