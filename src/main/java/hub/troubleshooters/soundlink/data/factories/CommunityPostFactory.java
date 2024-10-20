package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.data.models.CommunityPost;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommunityPostFactory extends ModelFactory<CommunityPost> {
	@Inject
	public CommunityPostFactory(DatabaseConnection connection) {
		super(connection, "CommunityPosts");
	}

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
	 * Gets all Community Posts from a given community
	 * @param communityId
	 * @return
	 * @throws SQLException
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
	 * Creates a CommunityPost
	 * @param communityId
	 * @param userId
	 * @param title
	 * @param content
	 * @throws SQLException
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
