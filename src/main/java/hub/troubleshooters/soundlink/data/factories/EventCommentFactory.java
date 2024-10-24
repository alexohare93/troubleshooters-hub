package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Event;
import hub.troubleshooters.soundlink.data.models.EventComment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Factory class responsible for handling database operations related to {@link EventComment} models.
 * This class provides methods for creating, retrieving, and updating event comments in the database.
 */
public class EventCommentFactory extends ModelFactory<EventComment> {

	/**
	 * Constructs a new {@code EventCommentFactory} with the specified database connection.
	 *
	 * @param connection The database connection to be used by the factory.
	 */
	@Inject
	public EventCommentFactory(DatabaseConnection connection) {
		super(connection, "EventComments");
	}

	/**
	 * Retrieves an {@link EventComment} by its unique ID.
	 *
	 * @param id The ID of the event comment to retrieve.
	 * @return An {@code Optional} containing the event comment if found, or an empty {@code Optional} if not.
	 * @throws SQLException If an error occurs during the query.
	 */
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
	 * Retrieves all {@link EventComment}s for a given event.
	 *
	 * @param event The event for which comments are retrieved.
	 * @return A list of event comments.
	 * @throws SQLException If an error occurs during the query.
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

	/**
	 * Retrieves all {@link EventComment}s by event ID.
	 *
	 * @param eventId The ID of the event for which comments are retrieved.
	 * @return A list of event comments.
	 * @throws SQLException If an error occurs during the query.
	 */
	public List<EventComment> getByEventId(int eventId) throws SQLException {
		return get(new Event(eventId, 0, null, null, null, 0, null, null, null));
	}

	/**
	 * Updates an existing {@link EventComment} in the database.
	 *
	 * @param eventComment The event comment model to update.
	 * @throws SQLException If the update fails or affects an incorrect number of rows.
	 */
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
	 * Creates a new {@link EventComment}.
	 *
	 * @param eventId The ID of the event to which the comment is associated.
	 * @param userId The ID of the user who made the comment.
	 * @param content The content of the comment.
	 * @throws SQLException If an error occurs during the creation of the comment.
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
