package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Community;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;


public class CommunityFactory extends ModelFactory<Community> {

    private static final Logger LOGGER = Logger.getLogger(CommunityFactory.class.getName());

    @Inject
    public CommunityFactory(DatabaseConnection connection) {
        super(connection, "Communities");
    }

    @Override
    public void save(Community community) throws SQLException {
        final String sql = "UPDATE Communities SET Name = ?, Description = ?, Genre = ?, Created = ?, BannerImageId = ?, isPrivate = ? WHERE Id = ?";
        connection.executeUpdate(sql, statement -> {
            statement.setString(1, community.getName());
            statement.setString(2, community.getDescription());
            statement.setString(3, community.getGenre());
            statement.setDate(4, new java.sql.Date(community.getCreated().getTime()));
            statement.setObject(5, community.getBannerImageId().orElse(null)); // Handle null for BannerImageId
            statement.setBoolean(6, community.isPrivate()); // Set the isPrivate field
            statement.setInt(7, community.getId());
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
                var bannerId = executor.getInt("BannerImageId");
                return new Community(
                        executor.getInt("Id"),
                        executor.getString("Name"),
                        executor.getString("Description"),
                        executor.getString("Genre"),
                        executor.getDate("Created"),
                        bannerId == 0 ? null : bannerId,
                        executor.getBoolean("isPrivate") // Get the isPrivate field
                );
            }
            return null;
        });
        if (community == null) return Optional.empty();
        return Optional.of(community);
    }

    public List<Community> get(List<Integer> ids) throws SQLException {
        final String sql = "SELECT * FROM Communities WHERE Id IN (?)";
        var sb = new StringBuilder();
        for (int id : ids) {
            sb.append(id).append(',');
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return connection.executeQuery(sql, statement -> statement.setString(1, sb.toString()), executor -> {
            var result = new ArrayList<Community>();
            while (executor.next()) {
                var bannerId = executor.getInt("BannerImageId");
                result.add(new Community(
                        executor.getInt("Id"),
                        executor.getString("Name"),
                        executor.getString("Description"),
                        executor.getString("Genre"),
                        executor.getDate("Created"),
                        bannerId == 0 ? null : bannerId,
                        executor.getBoolean("isPrivate")
                ));
            }
            return result;
        });
    }

    public void create(Community community) throws SQLException {
        final String sql = "INSERT INTO Communities (Name, Description, Genre, Created, BannerImageId, isPrivate) VALUES (?, ?, ?, ?, ?, ?)";
        connection.executeUpdate(sql, statement -> {
            statement.setString(1, community.getName());
            statement.setString(2, community.getDescription());
            statement.setString(3, community.getGenre());
            if (community.getCreated() != null) {
                statement.setDate(4, new java.sql.Date(community.getCreated().getTime()));
            } else {
                statement.setNull(4, java.sql.Types.DATE);
            }
            statement.setObject(5, community.getBannerImageId().orElse(null));
            statement.setBoolean(6, community.isPrivate());
        }, rowsAffected -> {
            if (rowsAffected != 1) {
                throw new SQLException("Failed to insert community. Rows Affected: " + rowsAffected);
            }
        });
    }

    public List<Community> getAllCommunities() throws SQLException {
        final String sql = "SELECT * FROM Communities";
        return connection.executeQuery(sql, statement -> {}, executor -> {
            var result = new ArrayList<Community>();
            while (executor.next()) {
                var bannerId = executor.getInt("BannerImageId");
                result.add(new Community(
                        executor.getInt("Id"),
                        executor.getString("Name"),
                        executor.getString("Description"),
                        executor.getString("Genre"),
                        executor.getDate("Created"),
                        bannerId == 0 ? null : bannerId,
                        executor.getBoolean("isPrivate")
                ));
            }
            return result;
        });
    }

    public void delete(int id) throws SQLException {
        final String sql = "DELETE FROM Communities WHERE Id = ?";
        connection.executeUpdate(sql, statement -> {
            statement.setInt(1, id);
        }, rowsAffected -> {
            if (rowsAffected != 1) {
                throw new SQLException("Failed to delete community. Rows Affected: " + rowsAffected);
            }
        });
    }
}
