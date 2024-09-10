package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.CommunityUser;
import hub.troubleshooters.soundlink.data.models.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommunityUserFactory extends ModelFactory<CommunityUser> {

    @Inject
    public CommunityUserFactory(DatabaseConnection connection) {
        super(connection, "CommunityUsers");
    }

    @Override
    public void save(CommunityUser model) throws SQLException {
        final String sql = "UPDATE CommunityUsers SET Permission = ? WHERE Id = ?;";
        connection.executeUpdate(sql, statement -> {
            statement.setInt(1, model.getPermission());
            statement.setInt(2, model.getId());
        }, rowsAffected -> {
            if (rowsAffected != 1) {
                throw new SQLException("Failed to update communityUser. Rows affected: " + rowsAffected);
            }
        });
    }

    @Override
    public Optional<CommunityUser> get(int id) throws SQLException {
        final String sql = "SELECT * FROM CommunityUsers WHERE Id = ?;";
        var communityUser = connection.executeQuery(sql, statement -> statement.setInt(1, id), executor -> {
            if (executor.next()) {
                return new CommunityUser(
                    executor.getInt("Id"),
                    executor.getInt("CommunityId"),
                    executor.getInt("UserId"),
                    executor.getDate("Created"),
                    executor.getInt("Permission")
                );
            }
            return null;
        });
        if (communityUser == null) {
            return Optional.empty();
        }
        return Optional.of(communityUser);
    }

    /**
     * Gets all community memberships for a given user
     * @param user
     * @return
     * @throws SQLException
     */
    public List<CommunityUser> get(User user) throws SQLException {
        final String sql = "SELECT * FROM CommunityUsers WHERE UserId = ?;";
        return connection.executeQuery(sql, statement -> statement.setInt(1, user.getId()), executor -> {
            var commUsers = new ArrayList<CommunityUser>();
            while (executor.next()) {
                commUsers.add(new CommunityUser(
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
    // TODO: add get(int userId, int communityId)
}
