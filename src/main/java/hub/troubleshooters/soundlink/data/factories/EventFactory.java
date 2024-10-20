package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
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

public class EventFactory extends ModelFactory<Event> {

	@Inject
	public EventFactory(DatabaseConnection connection) {
		super(connection, "Events");
	}

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
	 * Creates new Event
	 * @throws SQLException if an error occurs while creating the event
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

	// TODO: DRY; these factories are already getting pretty unmaintainable
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

	    // Fetches all events from the database
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
	        final String sql = "SELECT e.Id, e.Name, e.Description, e.Scheduled, e.Venue, e.Capacity, e.CommunityId, e.Created, e.BannerImageId " +
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
	        final String sql = "SELECT e.Id, e.Name, e.Description, e.Scheduled, e.Venue, e.Capacity, e.CommunityId, e.Created " +
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
	}
