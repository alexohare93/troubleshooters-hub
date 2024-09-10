package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Community;

import java.sql.SQLException;
import java.util.Optional;

public class CommunityFactory extends ModelFactory<Community> {

    @Inject
    public CommunityFactory(DatabaseConnection connection) {
        super(connection, "Communities");
    }

    @Override
    public void save(Community model) throws SQLException {
        final String sql = "UPDATE Communities SET Name = ?, Created = ? WHERE Id = ?;";
        this.connection.executeUpdate(sql, statement -> {
            statement.setString(1, model.getName());
            statement.setDate(2, new java.sql.Date(model.getCreated().getTime()));
        }, rowsAffected -> {
            if (rowsAffected != 1) {
                throw new SQLException("Failed to update community. Rows affected: " + rowsAffected);
            }
        });
    }

    @Override
    public Optional<Community> get(int id) throws SQLException {
        final String sql = "SELECT * FROM Communities WHERE Id = ?;";
        var community = connection.executeQuery(sql, statement -> statement.setInt(1, id), executor -> {
            if (executor.next()) {
                return new Community(
                    executor.getInt("Id"),
                    executor.getString("Name"),
                    executor.getDate("Created")
                );
            }
            return null;
        });
        if (community == null) {
            return Optional.empty();
        }
        return Optional.of(community);
    }
}
