package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.User;

import java.sql.SQLException;
import java.util.Optional;

public class UserFactory extends ModelFactory<User> {

    @Inject
    public UserFactory(DatabaseConnection connection) {
        super(connection, "Users");
    }

    @Override
    public void save(User model) throws SQLException {
        final String sql = "UPDATE Users SET Username = ?, HashedPassword = ?, Created = ?, LastLogin = ? WHERE Id = ?;";
        connection.executeUpdate(sql, statement -> {
            var param = 1;
            statement.setString(param++, model.getUsername());
            statement.setString(param++, model.getHashedPassword());
            statement.setDate(param++, new java.sql.Date(model.getCreated().getTime()));
            statement.setDate(param++, new java.sql.Date(model.getLastLogin().getTime()));
            statement.setInt(param, model.getId());
        }, rowsAffected -> {
            if (rowsAffected != 1) {
                throw new SQLException("Failed to update user. Rows affected: " + rowsAffected);
            }
        });
    }

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
    public void create(String username, String hashedPassword) throws SQLException {
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
}
