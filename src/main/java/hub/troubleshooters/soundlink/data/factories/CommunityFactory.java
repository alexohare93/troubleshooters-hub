package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Community;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Factory class responsible for creating, updating, retrieving, and deleting {@link Community} objects
 * from the database. This class interacts with the database to manage community-related data.
 */
public class CommunityFactory extends ModelFactory<Community> {

    private static final Logger LOGGER = Logger.getLogger(CommunityFactory.class.getName());

    /**
     * Constructs a {@code CommunityFactory} with the specified database connection.
     *
     * @param connection The database connection to be used for operations.
     */
    @Inject
    public CommunityFactory(DatabaseConnection connection) {
        super(connection, "Communities");
    }

    /**
     * Saves an existing community to the database by updating its details.
     *
     * @param community The community object to be updated.
     * @throws SQLException If there is an error during the update operation.
     */
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

    /**
     * Retrieves a community from the database by its ID.
     *
     * @param id The ID of the community to retrieve.
     * @return An {@link Optional} containing the community if found, or {@code Optional.empty()} if not found.
     * @throws SQLException If there is an error during the query operation.
     */
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

    /**
     * Retrieves a list of communities by their IDs.
     *
     * @param ids A list of community IDs to retrieve.
     * @return A list of {@link Community} objects matching the provided IDs.
     * @throws SQLException If there is an error during the query operation.
     */
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

    /**
     * Inserts a new community into the database.
     *
     * @param community The community object to be inserted.
     * @throws SQLException If there is an error during the insert operation.
     */
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

    /**
     * Retrieves a list of all communities from the database.
     *
     * @return A list of all {@link Community} objects in the database.
     * @throws SQLException If there is an error during the query operation.
     */
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

    /**
     * Deletes a community from the database by its ID.
     *
     * @param id The ID of the community to be deleted.
     * @throws SQLException If the deletion of the community fails.
     */
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