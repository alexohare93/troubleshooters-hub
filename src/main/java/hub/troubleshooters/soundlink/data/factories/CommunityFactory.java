package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Community;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommunityFactory extends ModelFactory<Community> {

    @Inject
    public CommunityFactory(DatabaseConnection connection) {
        super(connection, "Communities");
    }

    @Override
    public void save(Community community) throws SQLException {
        final String sql = "UPDATE Communities SET Name = ?, Description = ?, Genre = ?, Created = ? WHERE Id = ?";
        connection.executeUpdate(sql, statement -> {
            statement.setString(1, community.getName());
            statement.setString(2, community.getDescription());
            statement.setString(3, community.getGenre());
            statement.setDate(4, new java.sql.Date(community.getCreated().getTime()));
            statement.setInt(5, community.getId());
        }, rowsAffected -> {
            if (rowsAffected != 1) {
                throw new SQLException("Failed to update community. Rows Affected: " + rowsAffected);
            }
        });
    }

    @Override
    public Optional<Community> get(int id) throws SQLException {
        final String sql = "SELECT * FROM Communities WHERE Id = ?";
        var community = connection.executeQuery(sql, statement -> statement.setInt(1, id), executor -> {
            if (executor.next()) {
                return new Community(
                        executor.getInt("Id"),
                        executor.getString("Name"),
                        executor.getString("Description"),
                        executor.getString("Genre"),
                        executor.getDate("Created")
                );
            }
            return null;
        });
        if (community == null) return Optional.empty();
        return Optional.of(community);
    }

    /**
     * Returns a list of communities given a list of IDs
     * @param ids a list of community IDs
     * @return a list of communities
     * @throws SQLException if there was an underlying SQL error
     */
    public List<Community> get(List<Integer> ids) throws SQLException {
        final String sql = "SELECT * FROM Communities WHERE Id IN (?)";
        // transforming int array to comma-delimited string
        var sb = new StringBuilder();
        for (int id : ids) {
            sb.append(id);
            sb.append(',');
        }
        if (!sb.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);  // remove final comma
        }
        return connection.executeQuery(sql, statement -> statement.setString(1, sb.toString()), executor -> {
            var result = new ArrayList<Community>();
            while (executor.next()) {
                result.add(new Community(
                        executor.getInt("Id"),
                        executor.getString("Name"),
                        executor.getString("Description"),
                        executor.getString("Genre"),
                        executor.getDate("Created")
                ));
            }
            return result;
        });
    }

    /**
     * Creates new Community
     * @param community the community object to be inserted
     * @throws SQLException if the creation of the community fails
     */
    public void create(Community community) throws SQLException {
        final String sql = "INSERT INTO Communities (Name, Description, Genre) VALUES (?, ?, ?)";
        connection.executeUpdate(sql, statement -> {
            statement.setString(1, community.getName());
            statement.setString(2, community.getDescription());
            statement.setString(3, community.getGenre());
        }, rowsAffected -> {
            if (rowsAffected != 1) {
                throw new SQLException("Failed to update community. Rows Affected: " + rowsAffected);
            }
        });
    }

    public List<Community> getAllCommunities() throws SQLException {
        final String sql = "SELECT * FROM Communities";
        return connection.executeQuery(sql, statement -> {}, executor -> {
            var result = new ArrayList<Community>();
            while (executor.next()) {
                result.add(new Community(
                        executor.getInt("Id"),
                        executor.getString("Name"),
                        executor.getString("Description"),
                        executor.getString("Genre"),
                        executor.getDate("Created")
                ));
            }
            return result;
        });
    }
}