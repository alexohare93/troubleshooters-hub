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
  public void save(Community community) throws SQLException {
    final String sql =
        "UPDATE Communities SET Name = ?, Description = ?, Genre = ?, Created = ? WHERE Id = ?";
    connection.executeUpdate(
        sql,
        statement -> {
          statement.setString(1, community.getName());
          statement.setString(2, community.getDescription());
          statement.setString(3, community.getGenre());
          statement.setDate(4, new java.sql.Date(community.getCreated().getTime()));
          statement.setInt(5, community.getId());
        },
        rowsAffected -> {
          if (rowsAffected != 1) {
            throw new SQLException("Failed to update community. Rows Affected: " + rowsAffected);
          }
        });
  }

  @Override
  public Optional<Community> get(int id) throws SQLException {
    final String sql = "SELECT * FROM Communities WHERE Id = ?";
    var community =
        connection.executeQuery(
            sql,
            statement -> statement.setInt(1, id),
            executor -> {
              if (executor.next()) {
                return new Community(
                    executor.getInt("Id"),
                    executor.getString("Name"),
                    executor.getString("Description"),
                    executor.getString("Genre"),
                    executor.getDate("Created"));
              }
              return null;
            });
    if (community == null) return Optional.empty();
    return Optional.of(community);
  }

  /**
   * Creates new Community
   *
   * @param community the community object to be inserted
   * @throws SQLException if the creation of the community fails
   */
  public void create(Community community) throws SQLException {
    final String sql =
        "INSERT INTO Communities (Id, Name, Description, Genre, Created) VALUES (?, ?, ?, ?, ?)";
    connection.executeUpdate(
        sql,
        statement -> {
          statement.setInt(1, community.getId());
          statement.setString(2, community.getName());
          statement.setString(3, community.getDescription());
          statement.setString(4, community.getGenre());
          statement.setDate(5, new java.sql.Date(community.getCreated().getTime()));
        },
        rowsAffected -> {
          if (rowsAffected != 1) {
            throw new SQLException("Failed to update community. Rows Affected: " + rowsAffected);
          }
        });
  }
}
