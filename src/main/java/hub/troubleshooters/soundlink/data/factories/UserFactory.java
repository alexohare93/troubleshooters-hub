package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.User;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Factory class responsible for handling database operations related to {@link User} models.
 * This class provides methods for creating, retrieving, and updating users in the database.
 */
public class UserFactory extends ModelFactory<User> {

    /**
     * Constructs a new {@code UserFactory} with the specified database connection.
     *
     * @param connection The database connection to be used by the factory.
     */
    @Inject
    public UserFactory(DatabaseConnection connection) {
        super(connection, "Users");
    }

    /**
     * Updates an existing {@link User} in the database.
     *
     * @param model The user model to update.
     * @throws SQLException If the update fails or affects an incorrect number of rows.
     */
    @Override
    public void save(User model) throws SQLException {
        final String sql = "UPDATE Users SET Username = ?, HashedPassword = ?, Created = ?, LastLogin = ? WHERE Id = ?;";
        connection.executeUpdate(sql, statement -> {
            statement.setString(1, model.getUsername());
            statement.setString(2, model.getHashedPassword());
            statement.setDate(3, new java.sql.Date(model.getCreated().getTime()));
            statement.setDate(4, new java.sql.Date(model.getLastLogin().getTime()));
            statement.setInt(5, model.getId());
        }, rowsAffected -> {
            if (rowsAffected != 1) {
                throw new SQLException("Failed to update user. Rows affected: " + rowsAffected);
            }
        });
    }

    /**
     * Retrieves a {@link User} by its unique ID.
     *
     * @param id The ID of the user to retrieve.
     * @return An {@code Optional} containing the user if found, or an empty {@code Optional} if not.
     * @throws SQLException If an error occurs during the query.
     */
    @Override
    public Optional<User> get(int id) throws SQLException {
        final String sql = "SELECT * FROM Users WHERE Id = ?";
        var user = connection.executeQuery(sql, statement -> statement.setInt(1, id), executor -> {
            if (executor.next()) {
                return new User(
                    executor.getInt("Id"),
                    executor.getString("Username"),
                    executor.getString("HashedPassword"),
                    executor.getDate("Created"),
                    executor.getDate("LastLogin")
                );
            }
            return null;
        });
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    /**
     * Gets the user with the given username or an empty Optional.
     * @param username the username of the user to get
     * @return the user with the given username, if it exists
     * @throws SQLException if an error occurs while getting the user
     */
    public Optional<User> get(String username) throws SQLException {
        final String sql = "SELECT * FROM Users WHERE Username = ?";
        var user = connection.executeQuery(sql, statement -> statement.setString(1, username), executor -> {
            if (executor.next()) {
                return new User(
                        executor.getInt("Id"),
                        executor.getString("Username"),
                        executor.getString("HashedPassword"),
                        executor.getDate("Created"),
                        executor.getDate("LastLogin")
                );
            }
            return null;
        });
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    /**
     * Creates a new user with the given username and hashed password.
     * @param username the username of the new user
     * @param hashedPassword the hashed password of the new user
     * @throws SQLException if an error occurs while creating the user
     */
    public User create(String username, String hashedPassword) throws SQLException {
        final var sql = "INSERT INTO Users (Username, HashedPassword) VALUES (?, ?)";
        connection.executeUpdate(sql, preparedStatement -> {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);
        }, affectedRows -> {
            if (affectedRows != 1) {
                throw new SQLException("Failed to create new user: " + username);
            }
        });
        return get(username).get(); // should not fail due to just being created.
    }
}
