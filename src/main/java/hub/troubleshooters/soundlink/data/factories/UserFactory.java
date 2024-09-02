package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.User;

import java.sql.SQLException;
import java.util.Optional;

public class UserFactory implements ModelFactory<User> {

    private final DatabaseConnection connection;

    @Inject
    public UserFactory(DatabaseConnection connection) {
        this.connection = connection;
    }

    @Override
    public void save(User model) throws SQLException {
        final String sql = "UPDATE Users SET Username = ?, HashedPassword = ?, Created = ?, LastLogin = ?, Permission = ? WHERE Id = ?;";
        connection.executeUpdate(sql, statement -> {
            statement.setString(1, model.getUsername());
            statement.setString(2, model.getHashedPassword());
            statement.setDate(3, new java.sql.Date(model.getCreated().getTime()));
            statement.setDate(4, new java.sql.Date(model.getLastLogin().getTime()));
            statement.setInt(5, model.getPermission());
            statement.setInt(6, model.getId());
        }, rowsAffected -> {
            if (rowsAffected != 1) {
                throw new SQLException("Failed to update user. Rows affected: " + rowsAffected);
            }
        });
    }

    @Override
    public void delete(int id) throws SQLException {
        final String sql = "DELETE FROM Users WHERE Id = ?";
        connection.executeUpdate(sql, statement -> {
            statement.setInt(1, id);
        }, rowsAffected -> {
            if (rowsAffected != 1) {
                throw new SQLException("Failed to delete user. Rows affected: " + rowsAffected);
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
                        executor.getDate("LastLogin"),
                        executor.getInt("Permission")
                );
            }
            return null;
        });
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    public Optional<User> get(String username) throws SQLException {
        final String sql = "SELECT * FROM Users WHERE Username = ?";
        var user = connection.executeQuery(sql, statement -> statement.setString(1, username), executor -> {
            if (executor.next()) {
                return new User(
                        executor.getInt("Id"),
                        executor.getString("Username"),
                        executor.getString("HashedPassword"),
                        executor.getDate("Created"),
                        executor.getDate("LastLogin"),
                        executor.getInt("Permission")
                );
            }
            return null;
        });
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

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
