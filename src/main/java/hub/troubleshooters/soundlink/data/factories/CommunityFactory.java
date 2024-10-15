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
        final String sql = "UPDATE Communities SET Name = ?, Description = ?, Genre = ?, Created = ?, BannerImageId = ? WHERE Id = ?";

        // Convert Date to String with correct DATETIME format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(community.getCreated());

        // Log the values being passed to debug any issues
        LOGGER.info("Saving community - ID: " + community.getId() +
                ", Name: " + community.getName() +
                ", Description: " + community.getDescription() +
                ", Genre: " + community.getGenre() +
                ", Created: " + formattedDate +
                ", BannerImageId: " + community.getBannerImageId().orElse(null));

        connection.executeUpdate(sql, statement -> {
            statement.setString(1, community.getName());
            statement.setString(2, community.getDescription());
            statement.setString(3, community.getGenre());
            statement.setString(4, formattedDate);
            statement.setObject(5, community.getBannerImageId().orElse(null)); // setObject so that we can set null
            statement.setInt(6, community.getId());
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
                        bannerId == 0 ? null : bannerId
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
                var bannerId = executor.getInt("BannerImageId");
                result.add(new Community(
                        executor.getInt("Id"),
                        executor.getString("Name"),
                        executor.getString("Description"),
                        executor.getString("Genre"),
                        executor.getDate("Created"),
                        bannerId == 0 ? null : bannerId
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
                var bannerId = executor.getInt("BannerImageId");
                result.add(new Community(
                        executor.getInt("Id"),
                        executor.getString("Name"),
                        executor.getString("Description"),
                        executor.getString("Genre"),
                        executor.getDate("Created"),
                        bannerId == 0 ? null : bannerId
                ));
            }
            return result;
        });
    }
    /**
     * Deletes a Community
     * @param community the community object to be deleted
     * @throws SQLException if the deletion of the community fails
     */
    public void delete(Community community) throws SQLException {
        final String sql = "DELETE FROM Communities WHERE Id = ?";
        connection.executeUpdate(sql, statement -> {
            statement.setInt(1, community.getId());  // Only set the ID for deletion
        }, rowsAffected -> {
            if (rowsAffected != 1) {
                throw new SQLException("Failed to delete community. Rows Affected: " + rowsAffected);
            }
        });
    }

}