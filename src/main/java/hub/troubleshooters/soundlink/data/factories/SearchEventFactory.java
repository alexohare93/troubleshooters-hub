package hub.troubleshooters.soundlink.data.factories;

import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.SearchEvent;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;

import com.google.inject.Inject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class SearchEventFactory extends ModelFactory<SearchEvent> {

    private final IdentityService identityService;

    @Inject
    public SearchEventFactory(DatabaseConnection connection, IdentityService identityService) {
        super(connection, "Event");
        this.identityService = identityService;
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
                "WHERE cm.UserId = ?";
        return connection.executeQuery(sql, statement -> statement.setInt(1, userId), executor -> {
            List<SearchEvent> searchEvents = new ArrayList<>();
            while (executor.next()) {
                searchEvents.add(new SearchEvent(
                        executor.getInt("Id"),
                        executor.getInt("CommunityId"),
                        executor.getString("Name"),
                        executor.getString("Description"),
                        executor.getString("Venue"),
                        executor.getInt("Capacity"),
                        executor.getDate("Scheduled")
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
    public List<SearchEvent> findPublicCommunityEvents(int userId) throws SQLException {
        final String sql = "SELECT e.Id, e.Name, e.Description, e.Scheduled, e.Venue, e.Capacity, e.CommunityId " +
                "FROM Events e " +
                "JOIN Communities c ON e.CommunityId = c.Id " +
                "WHERE c.Id NOT IN (SELECT CommunityId FROM CommunityMembers WHERE UserId = ?) ";
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

    public ObservableList<SearchEvent> searchEvents(String name, String description, String venue, String capacity, LocalDate scheduled,String eventType) throws SQLException {
        String sql = "SELECT * FROM Events WHERE 1=1";
        if (name != null && !name.isEmpty()) {
            sql += " AND Name LIKE ?";
        }
        if (description != null && !description.isEmpty()) {
            sql += " AND Description LIKE ?";
        }
        if (venue != null && !venue.isEmpty()) {
            sql += " AND Venue LIKE ?";
        }
        if (capacity != null && !capacity.isEmpty()) {
            sql += " AND Capacity LIKE ?";
        }
        if (scheduled != null) {
            sql += " AND DATE(Scheduled) = ?";
        }
        if (eventType != null) {
            if (eventType.equals("Community Events")) {
                sql += " AND CommunityId IN (SELECT CommunityId FROM CommunityMembers WHERE UserId = ?)";
            } else if (eventType.equals("Public Events")) {
                sql += " AND CommunityId NOT IN (SELECT CommunityId FROM CommunityMembers WHERE UserId = ?)";
            }
        }
        return connection.executeQuery(sql, statement -> {
            int paramIndex = 1;
            if (name != null && !name.isEmpty()) {
                statement.setString(paramIndex++, "%" + name + "%");
            }
            if (description != null && !description.isEmpty()) {
                statement.setString(paramIndex++, "%" + description + "%");
            }
            if (venue != null && !venue.isEmpty()) {
                statement.setString(paramIndex++, "%" + venue + "%");
            }
            if (capacity != null && !capacity.isEmpty()) {
                statement.setString(paramIndex++,"%" + capacity + "%");
            }
            if (scheduled != null) {
                statement.setDate(paramIndex++, java.sql.Date.valueOf(scheduled));
            }
            if (eventType != null && (eventType.equals("Community Events") || eventType.equals("Public Events"))) {
                statement.setInt(paramIndex++, identityService.getUserContext().getUser().getId());
            }
        }, resultSet -> {
            ObservableList<SearchEvent> events = FXCollections.observableArrayList();
            while (resultSet.next()) {
                events.add(new SearchEvent(
                        resultSet.getInt("Id"),
                        resultSet.getInt("CommunityId"),
                        resultSet.getString("Name"),
                        resultSet.getString("Description"),
                        resultSet.getString("Venue"),
                        resultSet.getInt("Capacity"),
                        resultSet.getDate("Scheduled")
                ));
            }
            return events;
        });
    }
}
