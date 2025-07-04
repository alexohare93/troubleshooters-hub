package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.data.models.Event;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Factory class responsible for handling database operations related to {@link Event} models.
 * This class provides methods for creating, retrieving, updating, and searching for events in the database.
 */
public class EventFactory extends ModelFactory<Event> {

	/**
	 * Constructs a new {@code EventFactory} with the specified database connection.
	 *
	 * @param connection The database connection to be used by the factory.
	 */
	@Inject
	public EventFactory(DatabaseConnection connection) {
		super(connection, "Events");
	}

	/**
	 * Updates an existing {@link Event} in the database.
	 *
	 * @param event The event model to update.
	 * @throws SQLException If the update fails or affects an incorrect number of rows.
	 */
	@Override
	public void save(Event event) throws SQLException {
		final String sql = "UPDATE Events SET CommunityId = ?, Name = ?, Description = ?, Venue = ?, Capacity = ?, Scheduled = ?, Created = ?, BannerImageId = ? WHERE Id = ?";
		connection.executeUpdate(sql, statement -> {
			statement.setInt(1, event.getCommunityId());
			statement.setString(2, event.getName());
			statement.setString(3, event.getDescription());
			statement.setString(4, event.getVenue());
			statement.setInt(5, event.getCapacity());
			statement.setDate(6, new java.sql.Date(event.getScheduled().getTime()));
			statement.setDate(7, new java.sql.Date(event.getCreated().getTime()));
			statement.setObject(8, event.getBannerImageId().orElse(null));	// setObject so that we can set null
			statement.setInt(9, event.getId());
		}, rowsAffected -> {
			if (rowsAffected != 1) {
				throw new SQLException("Failed to update event. Rows Affected: " + rowsAffected);
			}
		});
	}

	/**
	 * Retrieves an {@link Event} by its unique ID.
	 *
	 * @param id The ID of the event to retrieve.
	 * @return An {@code Optional} containing the event if found, or an empty {@code Optional} if not.
	 * @throws SQLException If an error occurs during the query.
	 */
	@Override
	public Optional<Event> get(int id) throws SQLException {
		final String sql = "SELECT * FROM Events WHERE Id = ?";
		var event = connection.executeQuery(sql, statement -> statement.setInt(1, id), executor -> {
			if (executor.next()) {
				var bannerId = executor.getInt("BannerImageId");
				return new Event(
						executor.getInt("Id"),
						executor.getInt("CommunityId"),
						executor.getString("Name"),
						executor.getString("Description"),
						executor.getString("Venue"),
						executor.getInt("Capacity"),
						executor.getDate("Scheduled"),
						executor.getDate("Created"),
						bannerId == 0 ? null : bannerId
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
	 * Creates a new {@link Event} with a banner image.
	 *
	 * @param name         The name of the event.
	 * @param description  The description of the event.
	 * @param communityId  The ID of the community organizing the event.
	 * @param venue        The venue where the event will take place.
	 * @param capacity     The maximum capacity for the event.
	 * @param scheduled    The scheduled date of the event.
	 * @param bannerImageId The ID of the banner image associated with the event.
	 * @throws SQLException If an error occurs during the creation of the event.
	 */
	public void create(String name, String description, int communityId, String venue, int capacity, Date scheduled, int bannerImageId) throws SQLException {
		final String sql = "INSERT INTO Events (CommunityId, Name, Description, Venue, Capacity, Scheduled, BannerImageId) VALUES (?, ?, ?, ? ,?, ?, ?)";
		connection.executeUpdate(sql, statement -> {
			statement.setInt(1, communityId);
			statement.setString(2, name);
			statement.setString(3, description);
			statement.setString(4, venue);
			statement.setInt(5, capacity);
			statement.setDate(6, new java.sql.Date(scheduled.getTime()));
			statement.setInt(7, bannerImageId);
		}, rowsAffected -> {
			if (rowsAffected != 1) {
				throw new SQLException("Failed to create event. Rows Affected: " + rowsAffected);
			}
		});
	}

	/**
	 * Creates a new {@link Event} without a banner image.
	 *
	 * @param name        The name of the event.
	 * @param description The description of the event.
	 * @param communityId The ID of the community organizing the event.
	 * @param venue       The venue where the event will take place.
	 * @param capacity    The maximum capacity for the event.
	 * @param scheduled   The scheduled date of the event.
	 * @throws SQLException If an error occurs during the creation of the event.
	 */
	public void create(String name, String description, int communityId, String venue, int capacity, Date scheduled) throws SQLException {
		final String sql = "INSERT INTO Events (CommunityId, Name, Description, Venue, Capacity, Scheduled) VALUES (?, ?, ?, ? ,?, ?)";
		connection.executeUpdate(sql, statement -> {
			statement.setInt(1, communityId);
			statement.setString(2, name);
			statement.setString(3, description);
			statement.setString(4, venue);
			statement.setInt(5, capacity);
			statement.setDate(6, new java.sql.Date(scheduled.getTime()));
		}, rowsAffected -> {
			if (rowsAffected != 1) {
				throw new SQLException("Failed to create event. Rows Affected: " + rowsAffected);
			}
		});
	}

	/**
	 * Retrieves all events from the database.
	 *
	 * @return A list of all events.
	 * @throws SQLException If an error occurs during the query.
	 */
	    public List<Event> getAllEvents() throws SQLException {
	        final String sql = "SELECT * FROM Events";
	        return connection.executeQuery(sql, statement -> {}, executor -> {
	            List<Event> eventList = new ArrayList<>();
	            while (executor.next()) {
					var bannerId = executor.getInt("BannerImageId");
	                eventList.add(new Event(
	                        executor.getInt("Id"),
	                        executor.getInt("CommunityId"),
	                        executor.getString("Name"),
	                        executor.getString("Description"),
	                        executor.getString("Venue"),
	                        executor.getInt("Capacity"),
	                        executor.getDate("Scheduled"),
	                        executor.getDate("Created"),
							bannerId == 0 ? null : bannerId
	                ));
	            }
	            return eventList;
	        });
	    }

	     /**
	     * Finds all events from communities the user is a member of.
	     * @param userId The user's ID
	     * @return A list of events the user is a member of
	     * @throws SQLException if a database error occurs
	     */
	    public List<Event> findUserCommunityEvents(int userId) throws SQLException {
	        final String sql = "SELECT * " +
	                "FROM Events e " +
	                "JOIN CommunityMembers cm ON cm.CommunityId = e.CommunityId " +
	                "WHERE cm.UserId = ?";
	        return connection.executeQuery(sql, statement -> statement.setInt(1, userId), executor -> {
	            List<Event> searchEvents = new ArrayList<>();
	            while (executor.next()) {
					var bannerId = executor.getInt("BannerImageId");
	                searchEvents.add(new Event(
	                        executor.getInt("Id"),
	                        executor.getInt("CommunityId"),
	                        executor.getString("Name"),
	                        executor.getString("Description"),
	                        executor.getString("Venue"),
	                        executor.getInt("Capacity"),
							executor.getDate("Scheduled"),
							executor.getDate("Created"),
							bannerId == 0 ? null : bannerId
	                ));
	            }
	            return searchEvents;
	        });
	    }
	
	        /**
	     * Finds all public events where the user is not a member of the community.
	     * @param userId The user's ID
	     * @return A list of public events the user is not a member of
	     * @throws SQLException if a database error occurs
	     */
	    public List<Event> findPublicCommunityEvents(int userId) throws SQLException {
	        final String sql = "SELECT * " +
	                "FROM Events e " +
	                "JOIN Communities c ON e.CommunityId = c.Id " +
	                "WHERE c.Id NOT IN (SELECT CommunityId FROM CommunityMembers WHERE UserId = ?) ";
	        return connection.executeQuery(sql, statement -> statement.setInt(1, userId), executor -> {
	            List<Event> publicEvents = new ArrayList<>();
	            while (executor.next()) {
					var bannerId = executor.getInt("BannerImageId");
	                publicEvents.add(new Event(
	                        executor.getInt("Id"),
	                        executor.getInt("CommunityId"),
	                        executor.getString("Name"),
	                        executor.getString("Description"),
	                        executor.getString("Venue"),
	                        executor.getInt("Capacity"),
	                        executor.getDate("Scheduled"),
							executor.getDate("Created"),
							bannerId == 0 ? null : bannerId
	                ));
	            }
	            return publicEvents;
	        });
	    }

		/**
		 * Finds all events within the provided community
		 * @param communityId The communities id
		 * @return A list of the communities events
		 * @throws SQLException If a database error occurs
		 **/
		public List<Event> findCommunityEvents(int communityId) throws SQLException {
				final String sql = "SELECT * FROM Events WHERE CommunityId = ?";
				return connection.executeQuery(sql, statement -> statement.setInt(1, communityId), executor -> {
					List<Event> communityEvents = new ArrayList<>();
					while (executor.next()) {
						var bannerId = executor.getInt("BannerImageId");
						communityEvents.add(new Event(
								executor.getInt("Id"),
								executor.getInt("CommunityId"),
								executor.getString("Name"),
								executor.getString("Description"),
								executor.getString("Venue"),
								executor.getInt("Capacity"),
								executor.getDate("Scheduled"),
								executor.getDate("Created"),
								bannerId == 0 ? null : bannerId
						));
					}
					return communityEvents;
				});
		}

	/**
	 * Deletes an Event
	 * @param event the community object to be deleted
	 * @throws SQLException if the deletion of the community fails
	 */
	public void delete(Event event) throws SQLException {
		final String sql = "DELETE FROM Events WHERE Id = ?";
		connection.executeUpdate(sql, statement -> {
			statement.setInt(1, event.getId());
		}, rowsAffected -> {
			if (rowsAffected != 1) {
				throw new SQLException("Failed to delete event. Rows Affected: " + rowsAffected);
			}
		});
	}
}
