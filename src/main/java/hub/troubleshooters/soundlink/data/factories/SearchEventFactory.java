package hub.troubleshooters.soundlink.data.factories;

import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.SearchEvent;
import com.google.inject.Inject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class SearchEventFactory extends ModelFactory<SearchEvent> {

    @Inject
    public SearchEventFactory(DatabaseConnection connection) {
        super(connection, "Event");
    }

// TODO: either remove this or adjust the save method so it potentially saves users' search history

    @Override
    public void save(SearchEvent model) throws SQLException {
        final String sql = "INSERT INTO Events (CommunityId, Name, Description, Venue, Capacity, Scheduled, Created) VALUES (?, ?, ?, ?, ?, ?, ?)";
        connection.executeUpdate(sql, statement -> {
            statement.setInt(1, model.getCommunityId());
            statement.setString(2, model.getName());
            statement.setString(3, model.getDescription());
            statement.setString(4, model.getVenue());
            statement.setInt(5, model.getCapacity());
            statement.setDate(6, new java.sql.Date(model.getScheduledDate().getTime()));
            statement.setDate(7, new java.sql.Date(new Date().getTime())); // Assuming Created is current date
        }, rowsAffected -> {
            if (rowsAffected != 1) {
                throw new SQLException("Failed to save event. Rows Affected: " + rowsAffected);
            }
        });
    }

    @Override
    public Optional<SearchEvent> get(int id) throws SQLException {
        final String sql = "SELECT * FROM Events WHERE Id = ?";
        var searchEvent = connection.executeQuery(sql, statement -> statement.setInt(1, id), executor -> {
            if (executor.next()) {
                return new SearchEvent(
                        executor.getInt("Id"),
                        executor.getInt("CommunityId"),
                        executor.getString("Name"),
                        executor.getString("Description"),
                        executor.getString("Venue"),
                        executor.getInt("Capacity"),
                        executor.getDate("Scheduled")
                );
            }
            return null;
        });
        return Optional.ofNullable(searchEvent);
    }

    /**
     * Finds all events from communities the user is a member of.
     * @param userId The user's ID
     * @return A list of events the user is a member of
     * @throws SQLException if a database error occurs
     */
    public List<SearchEvent> findUserCommunityEvents(int userId) throws SQLException {
        final String sql = "SELECT e.Id, e.Name, e.Description, e.Scheduled, e.Venue, e.Capacity, e.CommunityId " +
                "FROM Events e " +
                "JOIN CommunityMembers cm ON cm.CommunityId = e.CommunityId " +
                "WHERE cm.UserId = ? AND e.Scheduled >= CURRENT_TIMESTAMP";
        return connection.executeQuery(sql, statement -> statement.setInt(1, userId), executor -> {
            List<SearchEvent> searchevents = new ArrayList<>();
            while (executor.next()) {
                searchevents.add(new SearchEvent(
                        executor.getInt("Id"),
                        executor.getInt("CommunityId"),
                        executor.getString("Name"),
                        executor.getString("Description"),
                        executor.getString("Venue"),
                        executor.getInt("Capacity"),
                        executor.getDate("Scheduled")
                ));
            }
            return searchevents;
        });
    }

    /**
     * Finds all public events where the user is not a member of the community.
     * @param userId The user's ID
     * @return A list of public events the user is not a member of
     * @throws SQLException if a database error occurs
     */
    public List<SearchEvent> findPublicCommunityEvents(int userId) throws SQLException {
        final String sql = "SELECT e.Id, e.Name, e.Description, e.Scheduled, e.Venue, e.Capacity, e.CommunityId " +
                "FROM Events e " +
                "JOIN Communities c ON e.CommunityId = c.Id " +
                "WHERE c.Id NOT IN (SELECT CommunityId FROM CommunityMembers WHERE UserId = ?) " +
                "AND e.Scheduled >= CURRENT_TIMESTAMP";
        return connection.executeQuery(sql, statement -> statement.setInt(1, userId), executor -> {
            List<SearchEvent> publicEvents = new ArrayList<>();
            while (executor.next()) {
                publicEvents.add(new SearchEvent(
                        executor.getInt("Id"),
                        executor.getInt("CommunityId"),
                        executor.getString("Name"),
                        executor.getString("Description"),
                        executor.getString("Venue"),
                        executor.getInt("Capacity"),
                        executor.getDate("Scheduled")
                ));
            }
            return publicEvents;
        });
    }
}
