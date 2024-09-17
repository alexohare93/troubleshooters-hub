package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.CommunityMember;
import hub.troubleshooters.soundlink.data.models.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommunityMemberFactory extends ModelFactory<CommunityMember> {

    @Inject
    public CommunityMemberFactory(DatabaseConnection connection) {
        super(connection, "CommunityMembers");
    }

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
    public Optional<CommunityMember> get(int userId, int communityId) throws SQLException {
        final String sql = "SELECT * FROM CommunityMembers WHERE UserId = ? AND CommunityId = ?;";
        var communityMember = connection.executeQuery(sql, statement -> {
                statement.setInt(1, userId);
                statement.setInt(2, communityId);
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
}
