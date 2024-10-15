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

public class CommunityMemberFactory extends ModelFactory<CommunityMember> {

    @Inject
    public CommunityMemberFactory(DatabaseConnection connection) {
        super(connection, "CommunityMembers");
    }

    private static final Logger LOGGER = Logger.getLogger(CommunityMemberFactory.class.getName());

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
     * Gets a CommunityMember object by the unique userId and communityId
     * @param userId
     * @param communityId
     * @return
     * @throws SQLException
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
     * Gets all community memberships for a given user
     * @param user
     * @return
     * @throws SQLException
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
     * Creates a CommunityMember
     * @param communityId
     * @param userId
     * @param permission
     * @throws SQLException
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
