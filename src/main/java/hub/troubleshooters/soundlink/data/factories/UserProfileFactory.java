package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.UserProfile;

import java.sql.SQLException;
import java.util.Optional;

public class UserProfileFactory extends ModelFactory<UserProfile> {

    @Inject
    public UserProfileFactory(DatabaseConnection connection) {
        super(connection, "UserProfiles");
    }


    @Override
    public void save(UserProfile model) throws SQLException {
        final String sql = "UPDATE UserProfiles SET DisplayName = ?, Bio = ?, ProfileImageId = ? WHERE Id = ?;";
        connection.executeUpdate(sql, statement -> {
            statement.setString(1, model.getDisplayName());
            statement.setString(2, model.getBio());
            statement.setObject(3, model.getProfileImageId().orElse(null));
            statement.setInt(4, model.getId());
        }, rowsAffected -> {
            if (rowsAffected != 1) {
                throw new SQLException("Failed to update profile. Rows affected: " + rowsAffected);
            }
        });
    }

    @Override
    public Optional<UserProfile> get(int id) throws SQLException {
        final String sql = "SELECT * FROM UserProfiles WHERE Id = ?";
        var profile = connection.executeQuery(sql, statement -> statement.setInt(1, id), executor -> {
            if (executor.next()) {
                var imageId = executor.getInt("ProfileImageId");
                return new UserProfile(
                        executor.getInt("Id"),
                        executor.getInt("UserId"),
                        executor.getString("DisplayName"),
                        executor.getString("Bio"),
                        imageId == 0 ? null : imageId

                );
            }
            return null;
        });
        if (profile == null) {
            return Optional.empty();
        }
        return Optional.of(profile);
    }

    public Optional<UserProfile> getByUserId(int userId) throws SQLException {
        final String sql = "SELECT * FROM UserProfiles WHERE UserId = ?";
        var profile = connection.executeQuery(sql, statement -> statement.setInt(1, userId), executor -> {
            if (executor.next()) {
                var imageId = executor.getInt("ProfileImageId");
                return new UserProfile(
                        executor.getInt("Id"),
                        executor.getInt("UserId"),
                        executor.getString("DisplayName"),
                        executor.getString("Bio"),
                        imageId == 0 ? null : imageId

                );
            }
            return null;
        });
        if (profile == null) {
            return Optional.empty();
        }
        return Optional.of(profile);
    }

    public UserProfile create(int userId, String displayName) throws SQLException {
        final var sql = "INSERT INTO UserProfiles (UserId, DisplayName, Bio) VALUES (?, ?, '')";
        connection.executeUpdate(sql, preparedStatement -> {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, displayName);
        }, affectedRows -> {
            if (affectedRows != 1) {
                throw new SQLException("Failed to create new user: " + displayName);
            }
        });
        return getByUserId(userId).get();   // shouldn't fail since just made, but could
    }
}
