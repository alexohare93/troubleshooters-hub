package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.data.models.CommunityPost;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Factory class responsible for managing the persistence of {@link CommunityPost} objects in the database.
 * This class provides methods to retrieve, update, and manage community posts.
 */
public class CommunityPostFactory extends ModelFactory<CommunityPost> {

	/**
	 * Constructs a {@code CommunityPostFactory} with the specified database connection.
	 *
	 * @param connection The database connection to be used for operations.
	 */
	@Inject
	public CommunityPostFactory(DatabaseConnection connection) {
		super(connection, "CommunityPosts");
	}

	/**
	 * Retrieves a {@link CommunityPost} from the database by its ID.
	 *
	 * @param id The ID of the community post to retrieve.
	 * @return An {@link Optional} containing the community post if found, or {@code Optional.empty()} if not found.
	 * @throws SQLException If an error occurs during the query operation.
	 */
	@Override
	public Optional<CommunityPost> get(int id) throws SQLException {
		final String sql = "SELECT * FROM CommunityPosts WHERE Id = ?;";
		var communityPost = connection.executeQuery(sql, statement -> statement.setInt(1, id), executor -> {
			if (executor.next()) {
				return new CommunityPost(
						executor.getInt("Id"),
						executor.getInt("CommunityId"),
						executor.getInt("UserId"),
						executor.getString("Title"),
						executor.getString("Content"),
						executor.getDate("Created")
				);
			}
			return null;
		});
		if (communityPost == null) return Optional.empty();
		return Optional.of(communityPost);
	}

	/**
	 * Retrieves all community posts for a given community.
	 *
	 * @param communityId The ID of the community whose posts are to be retrieved.
	 * @return A list of {@link CommunityPost} objects belonging to the specified community.
	 * @throws SQLException If an error occurs during the query operation.
	 */
	public List<CommunityPost> getPosts(int communityId) throws SQLException {
		final String sql = "SELECT * FROM CommunityPosts WHERE CommunityId = ?;";
		return connection.executeQuery(sql, statement -> statement.setInt(1, communityId), executor -> {
			List<CommunityPost> list = new ArrayList<>();
			while (executor.next()) {
				list.add(new CommunityPost(
						executor.getInt("Id"),
						executor.getInt("CommunityId"),
						executor.getInt("UserId"),
						executor.getString("Title"),
						executor.getString("Content"),
						executor.getDate("Created")
				));
			}
			return list;
		});
	}

	/**
	 * Saves the updated details of a community post in the database.
	 *
	 * @param communityPost The {@link CommunityPost} object to be updated.
	 * @throws SQLException If an error occurs during the update operation.
	 */
	@Override
	public void save(CommunityPost communityPost) throws SQLException {
		final String sql = "UPDATE CommunityPosts SET Title = ?, Content = ? WHERE Id = ?;";
		connection.executeUpdate(sql, statement -> {
			statement.setString(1, communityPost.getTitle());
			statement.setString(2, communityPost.getContent());
			statement.setInt(3, communityPost.getId());
		}, rowsAffected -> {
			if (rowsAffected != 1)
				throw new SQLException("Failed to update Community Post. Rows affected " + rowsAffected);
		});
	}

	/**
	 * Creates a new {@link CommunityPost} in the database.
	 *
	 * @param communityId The ID of the community the post belongs to.
	 * @param userId The ID of the user creating the post.
	 * @param title The title of the community post.
	 * @param content The content of the community post.
	 * @throws SQLException If an error occurs during the insert operation.
	 */
	public void create(int communityId, int userId, String title, String content) throws SQLException{
		final String sql = "INSERT INTO CommunityPosts (CommunityId, UserId, Title, Content) VALUES (?, ?, ?, ?);";
		connection.executeUpdate(sql, statement -> {
			statement.setInt(1, communityId);
			statement.setInt(2, userId);
			statement.setString(3, title);
			statement.setString(4, content);
		}, rowsAffected -> {
			if (rowsAffected != 1)
				throw new SQLException("Failed to insert Community Post. Rows affected " + rowsAffected);
		});
	}
}
