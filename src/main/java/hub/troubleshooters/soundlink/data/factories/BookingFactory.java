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

/**
 * Factory class responsible for managing the persistence of {@link Booking} objects in the database.
 * Provides methods to retrieve, create, update, and delete bookings, as well as to query booking information.
 */
public class BookingFactory extends ModelFactory<Booking> {

	/**
	 * Constructs a {@code BookingFactory} with the specified database connection.
	 *
	 * @param connection The database connection to be used for operations.
	 */
	@Inject
	public BookingFactory(DatabaseConnection connection) {
		super(connection, "Bookings");
	}

	/**
	 * Saves an existing booking by updating its permission level in the database.
	 *
	 * @param booking The {@link Booking} object to be updated.
	 * @throws SQLException If an error occurs while updating the booking.
	 */
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

	/**
	 * Retrieves a booking by its ID from the database.
	 *
	 * @param id The ID of the booking to retrieve.
	 * @return An {@link Optional} containing the booking if found, or {@code Optional.empty()} if not found.
	 * @throws SQLException If an error occurs during the query operation.
	 */
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
	 * Retrieves a booking by the combination of event ID and user ID.
	 *
	 * @param eventId The ID of the event.
	 * @param userId The ID of the user.
	 * @return An {@link Optional} containing the booking if found, or {@code Optional.empty()} if not found.
	 * @throws SQLException If an error occurs during the query operation.
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
	 * Retrieves all bookings for a given user.
	 *
	 * @param user The {@link User} whose bookings are to be retrieved.
	 * @return A list of {@link Booking} objects associated with the user.
	 * @throws SQLException If an error occurs during the query operation.
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
	 * Retrieves all bookings for a given event.
	 *
	 * @param event The event whose bookings are to be retrieved.
	 * @return A list of {@link Booking} objects associated with the event.
	 * @throws SQLException If an error occurs during the query operation.
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
	 * Creates a new booking with the specified event ID, user ID, and permission level.
	 *
	 * @param eventId The ID of the event.
	 * @param userId The ID of the user.
	 * @param permission The permission level for the booking.
	 * @throws SQLException If an error occurs during the creation process.
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
	 * Deletes a booking for a given user and event.
	 *
	 * @param userId The ID of the user.
	 * @param eventId The ID of the event.
	 * @throws SQLException If an error occurs during the deletion process.
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

	/**
	 * Counts the number of bookings for a given event.
	 *
	 * @param eventId The ID of the event for which to count bookings.
	 * @return The number of bookings for the event.
	 * @throws SQLException If there is an error executing the query.
	 */
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

	/**
	 * Retrieves the display name of a user by their user ID.
	 *
	 * @param userId The ID of the user for which to retrieve the display name.
	 * @return The display name of the user.
	 * @throws SQLException If the user profile is not found or there is an error executing the query.
	 */
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
