package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Booking;
import hub.troubleshooters.soundlink.data.models.User;
import hub.troubleshooters.soundlink.data.models.Event;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingFactory extends ModelFactory<Booking> {
	@Inject
	public BookingFactory(DatabaseConnection connection) {
		super(connection, "Bookings");
	}

	@Override
	public void save(Booking booking) throws SQLException {
		final String sql = "UPDATE Bookings SET Permission = ? WHERE id = ?;";
		connection.executeUpdate(sql, statement -> {
			statement.setInt(1, booking.getPermission());
			statement.setInt(2, booking.getId());
		}, rowsAffected -> {
			if (rowsAffected != 1) {
				throw new SQLException("Failed to update Booking. Rows affected: " + rowsAffected);
			}
		});
	}

	@Override
	public Optional<Booking> get(int id) throws SQLException {
		final String sql = "SELECT * FROM Bookings WHERE Id = ?;";
		var booking = connection.executeQuery(sql, statement -> statement.setInt(1, id), executor -> {
			if (executor.next()) {
				return new Booking(
						executor.getInt("Id"),
						executor.getInt("EventId"),
						executor.getInt("UserId"),
						executor.getDate("Created"),
						executor.getInt("Permission")

				);
			}
			return null;
		});
		if (booking == null) return Optional.empty();
		return Optional.of(booking);
	}

	/**
	 * Gets a Booking object by the unique userId and eventId
	 * @param eventId
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
	public Optional<Booking> get(int eventId, int userId) throws SQLException {
		final String sql = "SELECT * FROM Bookings WHERE EventId = ? AND UserId = ?;";
		var booking = connection.executeQuery(sql, statement ->{
			statement.setInt(1, eventId);
			statement.setInt(2, userId);
		}, executor -> {
			if (executor.next()) {
				return new Booking(
						executor.getInt("Id"),
						executor.getInt("EventId"),
						executor.getInt("UserId"),
						executor.getDate("Created"),
						executor.getInt("Permission")

				);
			}
			return null;
		});
		if (booking == null) return Optional.empty();
		return Optional.of(booking);
	}

	/**
	 * Gets all Bookings for a given user
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public List<Booking> get(User user) throws SQLException {
		final String sql = "SELECT * FROM Bookings WHERE UserId = ?;";
		return connection.executeQuery(sql, statement -> statement.setInt(1, user.getId()), executor -> {
			List<Booking> list = new ArrayList<>();
			while (executor.next()) {
				list.add( new Booking(
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
	 * Gets all Bookings for a given event
	 * @param event
	 * @return
	 * @throws SQLException
	 */
	public List<Booking> get(Event event) throws SQLException {
		final String sql = "SELECT * FROM Bookings WHERE EventId = ?;";
		return connection.executeQuery(sql, statement -> statement.setInt(1, event.getId()), executor -> {
			List<Booking> list = new ArrayList<>();
			while (executor.next()) {
				list.add(new Booking(
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
	 * Creates a Booking
	 * @param eventId
	 * @param userId
	 * @param permission
	 * @throws SQLException
	 */
	public void create(int eventId, int userId, int permission) throws SQLException {
		final String sql = "INSERT INTO Bookings (EventId, UserId, Permission) VALUES (?, ?, ?);";
		connection.executeUpdate(sql, statement -> {
			statement.setInt(1, eventId);
			statement.setInt(2, userId);
			statement.setInt(3, permission);
		}, rowsAffected -> {
			if (rowsAffected != 1)
				throw new SQLException("Failed to create Booking. Rows affected" + rowsAffected);
		});
	}

	/**
	 * Delete a Booking for a given user
	 * @param eventId
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
	public void delete( int userId, int eventId) throws SQLException {
		final String sql = "DELETE FROM Bookings WHERE UserId = ? AND EventId = ?;";
		connection.executeUpdate(sql, statement -> {
			statement.setInt(1, userId);
			statement.setInt(2, eventId);
		}, rowsAffected -> {
			if (rowsAffected != 1) {
				throw new SQLException("Failed to delete booking. Rows Affected: " + rowsAffected);
			}
		});
	}

	public int countBookingsForEvent(int eventId) throws SQLException {
		final String sql = "SELECT COUNT(*) FROM Bookings WHERE EventId = ?;";

		return connection.executeQuery(sql, statement -> {
			statement.setInt(1, eventId);
		}, executor -> {
			if (executor.next()) {
				return executor.getInt(1);
			} else {
				return 0;
			}
		});
	}

	public String getDisplayNameById(int userId) throws SQLException {
		final String sql = "SELECT DisplayName FROM UserProfiles WHERE UserId = ?";

		return connection.executeQuery(sql, statement -> {
			statement.setInt(1, userId);
		}, executor -> {
			if (executor.next()) {
				return executor.getString("DisplayName");
			} else {
				throw new SQLException("User profile not found for UserId: " + userId);
			}
		});
	}
}
