package hub.troubleshooters.soundlink.core.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.data.models.UserModel;
import hub.troubleshooters.soundlink.data.DatabaseConnection;

import java.sql.SQLException;
import java.util.Optional;

public class LoginServiceImpl implements LoginService {

    private final DatabaseConnection connection;
    private final IdentityService identityService;

    @Inject
    public LoginServiceImpl(DatabaseConnection connection, IdentityService identityService) {
        this.connection = connection;
        this.identityService = identityService;
    }

    @Override
    public AuthResult login(String username, String password) {
        try {
            var hashedPassword = fetchHashedPassword(username);
            if (hashedPassword.isEmpty() || !verifyPassword(password, hashedPassword.get())) {
                return new AuthResult(new AuthException("Incorrect username or password"));
            }

            var userModel = getUserModel(username);
            if (userModel.isPresent()) {
                setIdentityContext(userModel.get());
                updateLastLoggedIn(username);
                return new AuthResult(); // successful login
            } else {
                return new AuthResult(new AuthException("Error retrieving user."));
            }

        } catch (SQLException e) {
            return new AuthResult(new AuthException("Internal server error. Please contact SoundLink support. Error: " + e.getMessage()));
        }
    }

    @Override
    public void logout() {
        identityService.setUserContext(null);
    }

    @Override
    public AuthResult register(String username, String password) {
        try {
            if (userExists(username)) {
                return new AuthResult(new AuthException("User already exists"));
            }
            var hashedPassword = hashPassword(password);
            createUser(username, hashedPassword);
            return new AuthResult(); // registration successful
        } catch (SQLException e) {
            return new AuthResult(new AuthException("Internal server error. Please contact SoundLink support. Error: " + e.getMessage()));
        }
    }

    private Optional<String> fetchHashedPassword(String username) throws SQLException {
        final var sql = "SELECT HashedPassword FROM Users WHERE Username = ?";
        return connection.executeQuery(sql, preparedStatement -> {
            preparedStatement.setString(1, username);
        }, resultSet -> {
            if (resultSet.next()) {
                return Optional.of(resultSet.getString("HashedPassword"));
            } else {
                return Optional.empty();
            }
        });
    }

    private void setIdentityContext(UserModel userModel) {
        identityService.setUserContext(new UserContext(userModel));
    }

    private void updateLastLoggedIn(String username) throws SQLException {
        final var sql = "UPDATE Users SET LastLoggedIn = ? WHERE Username = ?";
        connection.executeUpdate(sql, preparedStatement -> {
            preparedStatement.setTimestamp(1, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            preparedStatement.setString(2, username);
        }, affectedRows -> {
            if (affectedRows != 1) {
                throw new SQLException("Failed to update last logged in time for user: " + username);
            }
        });
    }

    private boolean userExists(String username) throws SQLException {
        return getUserModel(username).isPresent();
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    private void createUser(String username, String hashedPassword) throws SQLException {
        final var sql = "INSERT INTO Users (Username, HashedPassword) VALUES (?, ?)";
        connection.executeUpdate(sql, preparedStatement -> {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);
        }, affectedRows -> {
            if (affectedRows != 1) {
                throw new SQLException("Failed to create new user: " + username);
            }
        });
    }

    private Optional<UserModel> getUserModel(String username) throws SQLException {
        final var sql = "SELECT Id, Username, Created, LastLoggedIn, Permission FROM Users WHERE Username = ?";
        try {
            return connection.executeQuery(sql, preparedStatement -> {
                preparedStatement.setString(1, username);
            }, resultSet -> {
                if (resultSet.next()) {
                    int id = resultSet.getInt("Id");
                    var user = resultSet.getString("Username");
                    var created = resultSet.getTimestamp("Created");
                    var lastLoggedIn = resultSet.getTimestamp("LastLoggedIn");
                    var permission = resultSet.getInt("Permission");
                    var userModel = new UserModel(id, user, created, lastLoggedIn, permission);
                    return Optional.of(userModel);
                } else {
                    return Optional.empty(); // no user found
                }
            });
        } catch (SQLException e) {
            throw new SQLException("Internal server error. Please contact SoundLink support.", e);
        }
    }

    private boolean verifyPassword(String password, String hashedPassword) {
        var result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
        return result.verified;
    }
}
