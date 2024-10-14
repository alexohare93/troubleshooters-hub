package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Event;
import hub.troubleshooters.soundlink.data.models.EventComment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventCommentFactory extends ModelFactory<EventComment> {
	@Inject
	public EventCommentFactory(DatabaseConnection connection) {
		super(connection, "EventComments");
	}

	@Override
	public Optional<EventComment> get(int id) throws SQLException {
		final String sql = "SELECT * FROM EventComments WHERE Id = ?;";
		var eventComment = connection.executeQuery(sql, statement -> statement.setInt(1, id), executor -> {
			if (executor.next()) {
				return new EventComment(
						executor.getInt("Id"),
						executor.getInt("EventId"),
						executor.getInt("UserId"),
						executor.getString("Content"),
						executor.getDate("Created")
				);
			}
			return null;
		});
		if (eventComment == null) return Optional.empty();
		return Optional.of(eventComment);
	}

	/**
	 * Gets all Event Comments from a given event
	 * @param event
	 * @return
	 * @throws SQLException
	 */
	public List<EventComment> get(Event event) throws SQLException {
		final String sql = "SELECT * FROM EventComments WHERE EventId = ?;";
		return connection.executeQuery(sql, statement -> statement.setInt(1, event.getId()), executor -> {
			List<EventComment> list = new ArrayList<>();
			while (executor.next()) {
				list.add(new EventComment(
						executor.getInt("Id"),
						executor.getInt("EventId"),
						executor.getInt("UserId"),
						executor.getString("Content"),
						executor.getDate("Created")
				));
			}
			return list;
		});
	}

	public List<EventComment> getByEventId(int eventId) throws SQLException {
		return get(new Event(eventId, 0, null, null, null, 0, null, null, null));
	}

	@Override
	public void save(EventComment eventComment) throws SQLException {
		final String sql = "UPDATE EventComments SET Content = ? WHERE Id = ?;";
		connection.executeUpdate(sql, statement -> {
			statement.setString(1, eventComment.getContent());
			statement.setInt(2, eventComment.getId());
		}, rowsAffected -> {
			if (rowsAffected != 1)
				throw new SQLException("Failed to update Event Comment. Rows affected " + rowsAffected);
		});
	}

	/**
	 * Creates a EventComment
	 * @param eventId
	 * @param userId
	 * @param content
	 * @throws SQLException
	 */
	public void create(int eventId, int userId, String content) throws SQLException{
		final String sql = "INSERT INTO EventComments (EventId, UserId, Content) VALUES (?, ?, ?);";
		connection.executeUpdate(sql, statement -> {
			statement.setInt(1, eventId);
			statement.setInt(2, userId);
			statement.setString(3, content);
		}, rowsAffected -> {
			if (rowsAffected != 1)
				throw new SQLException("Failed to insert Event Comment. Rows affected " + rowsAffected);
		});
	}
}
