package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.UserProfile;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Factory class responsible for handling database operations related to {@link UserProfile} models.
 * This class provides methods for creating, retrieving, and updating user profiles in the database.
 */
public class UserProfileFactory extends ModelFactory<UserProfile> {

    /**
     * Constructs a new {@code UserProfileFactory} with the specified database connection.
     *
     * @param connection The database connection to be used by the factory.
     */
    @Inject
    public UserProfileFactory(DatabaseConnection connection) {
        super(connection, "UserProfiles");
    }


    /**
     * Updates an existing {@link UserProfile} in the database.
     *
     * @param model The user profile model to update.
     * @throws SQLException If the update fails or affects an incorrect number of rows.
     */
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

    /**
     * Retrieves a {@link UserProfile} by its unique ID.
     *
     * @param id The ID of the profile to retrieve.
     * @return An {@code Optional} containing the user profile if found, or an empty {@code Optional} if not.
     * @throws SQLException If an error occurs during the query.
     */
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

    /**
     * Retrieves a {@link UserProfile} by the associated user ID.
     *
     * @param userId The ID of the user whose profile is to be retrieved.
     * @return An {@code Optional} containing the user profile if found, or an empty {@code Optional} if not.
     * @throws SQLException If an error occurs during the query.
     */
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

    /**
     * Creates a new {@link UserProfile} for the specified user.
     *
     * @param userId The ID of the user for whom to create the profile.
     * @param displayName The display name to associate with the new profile.
     * @return The newly created user profile.
     * @throws SQLException If an error occurs during the profile creation.
     */
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
