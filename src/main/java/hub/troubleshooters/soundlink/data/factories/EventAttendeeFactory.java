package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.EventAttendee;
import hub.troubleshooters.soundlink.data.models.User;
import hub.troubleshooters.soundlink.data.models.Event;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventAttendeeFactory extends ModelFactory<EventAttendee> {
	@Inject
	public EventAttendeeFactory(DatabaseConnection connection) {
		super(connection, "EventAttendees");
	}

	@Override
	public void save(EventAttendee eventAttendee) throws SQLException {
		final String sql = "UPDATE EventAttendees SET Permission = ? WHERE id = ?;";
		connection.executeUpdate(sql, statement -> {
			statement.setInt(1, eventAttendee.getPermission());
			statement.setInt(2, eventAttendee.getId());
		}, rowsAffected -> {
			if (rowsAffected != 1) {
				throw new SQLException("Failed to update EventAttendee. Rows affected: " + rowsAffected);
			}
		});
	}

	@Override
	public Optional<EventAttendee> get(int id) throws SQLException {
		final String sql = "SELECT * FROM EventAttendees WHERE Id = ?;";
		var eventAttendee = connection.executeQuery(sql, statement -> statement.setInt(1, id), executor -> {
			if (executor.next()) {
				return new EventAttendee(
						executor.getInt("Id"),
						executor.getInt("EventId"),
						executor.getInt("UserId"),
						executor.getDate("Created"),
						executor.getInt("Permission")

				);
			}
			return null;
		});
		if (eventAttendee == null) return Optional.empty();
		return Optional.of(eventAttendee);
	}

	/**
	 * Gets a EventAttendee object by the unique userId and eventId
	 * @param eventId
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
	public Optional<EventAttendee> get(int eventId, int userId) throws SQLException {
		final String sql = "SELECT * FROM EventAttendees WHERE EventId = ? AND UserId = ?;";
		var eventAttendee = connection.executeQuery(sql, statement ->{
			statement.setInt(1, eventId);
			statement.setInt(2, userId);
		}, executor -> {
			if (executor.next()) {
				return new EventAttendee(
						executor.getInt("Id"),
						executor.getInt("EventId"),
						executor.getInt("UserId"),
						executor.getDate("Created"),
						executor.getInt("Permission")

				);
			}
			return null;
		});
		if (eventAttendee == null) return Optional.empty();
		return Optional.of(eventAttendee);
	}

	/**
	 * Gets all EventAttendees for a given user
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public List<EventAttendee> get(User user) throws SQLException {
		final String sql = "SELECT * FROM EventAttendees WHERE UserId = ?;";
		return connection.executeQuery(sql, statement -> statement.setInt(1, user.getId()), executor -> {
			List<EventAttendee> list = new ArrayList<>();
			while (executor.next()) {
				list.add( new EventAttendee(
						executor.getInt("Id"),
						executor.getInt("EventId"),
						executor.getInt("UserId"),
						executor.getDate("Created"),
						executor.getInt("Permission")

				));
			}
			return list;
		});
	}

	/**
	 * Gets all EventAttendees for a given event
	 * @param event
	 * @return
	 * @throws SQLException
	 */
	public List<EventAttendee> get(Event event) throws SQLException {
		final String sql = "SELECT * FROM EventAttendees WHERE EventId = ?;";
		return connection.executeQuery(sql, statement -> statement.setInt(1, event.getId()), executor -> {
			List<EventAttendee> list = new ArrayList<>();
			while (executor.next()) {
				list.add(new EventAttendee(
						executor.getInt("Id"),
						executor.getInt("EventId"),
						executor.getInt("UserId"),
						executor.getDate("Created"),
						executor.getInt("Permission")

				));
			}
			return list;
		});
	}

	/**
	 * Creates a EventAttendee
	 * @param eventId
	 * @param userId
	 * @param permission
	 * @throws SQLException
	 */
	public void create(int eventId, int userId, int permission) throws SQLException {
		final String sql = "INSERT INTO EventAttendees (EventId, UserId, Permission) VALUES (?, ?, ?);";
		connection.executeUpdate(sql, statement -> {
			statement.setInt(1, eventId);
			statement.setInt(2, userId);
			statement.setInt(3, permission);
		}, rowsAffected -> {
			if (rowsAffected != 1)
				throw new SQLException("Failed to create EventAttendee. Rows affected" + rowsAffected);
		});
	}
}
