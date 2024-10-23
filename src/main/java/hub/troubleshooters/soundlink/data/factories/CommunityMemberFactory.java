package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.CommunityMember;
import hub.troubleshooters.soundlink.data.models.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory class responsible for managing the persistence of {@link CommunityMember} objects in the database.
 * This class provides methods for saving and retrieving community members.
 */
public class CommunityMemberFactory extends ModelFactory<CommunityMember> {

    /**
     * Constructs a {@code CommunityMemberFactory} with the specified database connection.
     *
     * @param connection The database connection to be used for operations.
     */
    @Inject
    public CommunityMemberFactory(DatabaseConnection connection) {
        super(connection, "CommunityMembers");
    }

    private static final Logger LOGGER = Logger.getLogger(CommunityMemberFactory.class.getName());

    /**
     * Saves the details of a community member by updating their information in the database.
     *
     * @param model The {@link CommunityMember} object to be updated.
     * @throws SQLException If an error occurs while updating the community member.
     */
    @Override
    public void save(CommunityMember model) throws SQLException {
        final String sql = "UPDATE CommunityMembers SET Permission = ? WHERE Id = ?;";
        connection.executeUpdate(sql, statement -> {
            statement.setInt(1, model.getPermission());
            statement.setInt(2, model.getId());
        }, rowsAffected -> {
            if (rowsAffected != 1) {
                throw new SQLException("Failed to update communityMember. Rows affected: " + rowsAffected);
            }
        });
    }

    /**
     * Retrieves a community member by their ID from the database.
     *
     * @param id The ID of the community member to retrieve.
     * @return An {@link Optional} containing the community member if found, or {@code Optional.empty()} if not found.
     * @throws SQLException If an error occurs while retrieving the community member.
     */
    @Override
    public Optional<CommunityMember> get(int id) throws SQLException {
        final String sql = "SELECT * FROM CommunityMembers WHERE Id = ?;";
        var communityMember = connection.executeQuery(sql, statement -> statement.setInt(1, id), executor -> {
            if (executor.next()) {
                return new CommunityMember(
                    executor.getInt("Id"),
                    executor.getInt("CommunityId"),
                    executor.getInt("UserId"),
                    executor.getDate("Created"),
                    executor.getInt("Permission")
                );
            }
            return null;
        });
        if (communityMember == null) {
            return Optional.empty();
        }
        return Optional.of(communityMember);
    }

    /**
     * Retrieves a {@link CommunityMember} object by the unique combination of community ID and user ID.
     *
     * @param communityId The ID of the community.
     * @param userId The ID of the user.
     * @return An {@link Optional} containing the community member if found, or {@code Optional.empty()} if not found.
     * @throws SQLException If an error occurs during the query operation.
     */
    public Optional<CommunityMember> get(int communityId, int userId) throws SQLException {
        final String sql = "SELECT * FROM CommunityMembers WHERE CommunityId = ? AND UserId = ?;";
        var communityMember = connection.executeQuery(sql, statement -> {
                statement.setInt(1, communityId);
                statement.setInt(2, userId);
            }, executor -> {
            if (executor.next()) {
                return new CommunityMember(
                        executor.getInt("Id"),
                        executor.getInt("CommunityId"),
                        executor.getInt("UserId"),
                        executor.getDate("Created"),
                        executor.getInt("Permission")
                );
            }
            return null;
        });
        if (communityMember == null) {
            return Optional.empty();
        }
        return Optional.of(communityMember);
    }

    /**
     * Retrieves all community memberships for a given user.
     *
     * @param user The {@link User} whose community memberships are to be retrieved.
     * @return A list of {@link CommunityMember} objects representing the user's memberships.
     * @throws SQLException If an error occurs during the query operation.
     */
    public List<CommunityMember> get(User user) throws SQLException {
        final String sql = "SELECT * FROM CommunityMembers WHERE UserId = ?;";
        return connection.executeQuery(sql, statement -> statement.setInt(1, user.getId()), executor -> {
            var commUsers = new ArrayList<CommunityMember>();
            while (executor.next()) {
                commUsers.add(new CommunityMember(
                    executor.getInt("Id"),
                    executor.getInt("CommunityId"),
                    executor.getInt("UserId"),
                    executor.getDate("Created"),
                    executor.getInt("Permission")
                ));
            }
            return commUsers;
        });
    }

    /**
     * Creates a new {@link CommunityMember} entry for the specified user and community with a permission level.
     *
     * @param communityId The ID of the community the user is joining.
     * @param userId The ID of the user joining the community.
     * @param permission The permission level for the community member.
     * @throws SQLException If an error occurs during the creation process or the user is already a member.
     */
    public void create(int communityId, int userId, int permission) throws SQLException {
        // check if the user is already a member of the community.
        // probably not the best area to do this check but was the only one that worked.
        Optional<CommunityMember> existingMember = get(communityId, userId);
        if (existingMember.isPresent()) {
            LOGGER.log(Level.INFO, "User with UserId: " + userId + " is already a member of CommunityId: " + communityId);
            return;
        }
        final String sql = "INSERT INTO CommunityMembers (CommunityId, UserId, Permission) VALUES (?, ?, ?);";
        connection.executeUpdate(sql, statement -> {
            statement.setInt(1, communityId);
            statement.setInt(2, userId);
            statement.setInt(3, permission);
        }, rowsAffected -> {
            if (rowsAffected != 1)
                throw new SQLException("Failed to create Community Member. Rows affected" + rowsAffected);
        });
    }

    /**
     * Deletes a CommunityMember from the database based on communityId and userId.
     * @param communityId the ID of the community.
     * @param userId the ID of the user.
     * @return true if the deletion was successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean delete(int communityId, int userId) throws SQLException {
        final String sql = "DELETE FROM CommunityMembers WHERE CommunityId = ? AND UserId = ?;";
        try {
            // Track the success of the deletion.
            final boolean[] isDeleted = {false};

            connection.executeUpdate(sql, statement -> {
                statement.setInt(1, communityId);
                statement.setInt(2, userId);
            }, rowsAffected -> {
                // Update the isDeleted variable based on the affected rows.
                isDeleted[0] = (rowsAffected == 1);

                // Log if no rows were deleted.
                if (!isDeleted[0]) {
                    LOGGER.log(Level.WARNING, "No CommunityMember found for deletion with CommunityId: "
                            + communityId + " and UserId: " + userId);
                }
            });

            // Return the result of the deletion.
            return isDeleted[0];
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting CommunityMember for CommunityId: "
                    + communityId + " and UserId: " + userId, e);
            throw e;
        }
    }

}
