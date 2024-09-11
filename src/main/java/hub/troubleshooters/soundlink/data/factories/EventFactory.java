package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Event;

import java.sql.SQLException;
import java.util.Optional;

public class EventFactory extends ModelFactory<Event> {

	@Inject
	public EventFactory(DatabaseConnection connection) {
		super(connection, "Events");
	}

	@Override
	public void save(Event event) throws SQLException {
		final String sql = "UPDATE Events SET CommunityId = ?, Name = ?, Description = ?, Venue = ?, Capacity = ?, Scheduled = ?, Created = ? WHERE Id = ?";
		connection.executeUpdate(sql, statement -> {
			statement.setInt(1, event.getCommunityId());
			statement.setString(2, event.getName());
			statement.setString(3, event.getDescription());
			statement.setString(4, event.getVenue());
			statement.setInt(5, event.getCapacity());
			statement.setDate(6, new java.sql.Date(event.getScheduled().getTime()));
			statement.setDate(7, new java.sql.Date(event.getCreated().getTime()));
			statement.setInt(8, event.getId());
		}, rowsAffected -> {
			if (rowsAffected != 1) {
				throw new SQLException("Failed to update event. Rows Affected: " + rowsAffected);
			}
		});
	}

	@Override
	public Optional<Event> get(int id) throws SQLException {
		final String sql = "SELECT * FROM Events WHERE Id = ?";
		var event = connection.executeQuery(sql, statement -> statement.setInt(1, id), executor -> {
			if (executor.next()) {
				return new Event(
						executor.getInt("Id"),
						executor.getInt("CommunityId"),
						executor.getString("Name"),
						executor.getString("Description"),
						executor.getString("Venue"),
						executor.getInt("Capacity"),
						executor.getDate("Scheduled"),
						executor.getDate("Created")
				);
			}
			return null;
		});
		if (event == null) {
			return Optional.empty();
		}
		return Optional.of(event);
	}

	/**
	 * Creates new Event
	 * @param event the event object to be inserted
	 * @throws SQLException if an error occurs while creating the event
	 */
	public void create(Event event) throws SQLException {
		final String sql = "INSERT INTO Events (CommunityId, Name, Description, Venue, Capacity, Scheduled, Created) VALUES (?, ?, ?, ? ,? ,?, ?)";
		connection.executeUpdate(sql, statement -> {
			statement.setInt(1, event.getCommunityId());
			statement.setString(2, event.getName());
			statement.setString(3, event.getDescription());
			statement.setString(4, event.getVenue());
			statement.setInt(5, event.getCapacity());
			statement.setDate(6, new java.sql.Date(event.getScheduled().getTime()));
			statement.setDate(7, new java.sql.Date(event.getCreated().getTime()));
		}, rowsAffected -> {
			if (rowsAffected != 1) {
				throw new SQLException("Failed to create event. Rows Affected: " + rowsAffected);
			}
		});
	}
}
