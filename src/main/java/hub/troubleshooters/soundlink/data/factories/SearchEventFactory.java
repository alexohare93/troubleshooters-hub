package hub.troubleshooters.soundlink.data.factories;

import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.SearchEvent;
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

    public ObservableList<String> searchEventNames(String searchTerm) throws SQLException {
        String sql = "SELECT DISTINCT name FROM Events WHERE name LIKE ? LIMIT 10";
        List<String> results = connection.executeQuery(sql, statement -> {
            statement.setString(1, "%" + searchTerm + "%");
        }, resultSet -> {
            ObservableList<String> names = FXCollections.observableArrayList();
            while (resultSet.next()) {
                names.add(resultSet.getString("name"));
            }
            return names;
        });
        return FXCollections.observableArrayList(results);
    }

    public ObservableList<String> searchEventDescriptions(String searchTerm) throws SQLException {
        String sql = "SELECT DISTINCT description FROM Events WHERE description LIKE ? LIMIT 10";
        List<String> results = connection.executeQuery(sql, statement -> {
            statement.setString(1, "%" + searchTerm + "%");
        }, resultSet -> {
            ObservableList<String> descriptions = FXCollections.observableArrayList();
            while (resultSet.next()) {
                descriptions.add(resultSet.getString("description"));
            }
            return descriptions;
        });
        return FXCollections.observableArrayList(results);
    }

    public ObservableList<String> searchEventVenues(String searchTerm) throws SQLException {
        String sql = "SELECT DISTINCT venue FROM Events WHERE venue LIKE ? LIMIT 10";
        List<String> results = connection.executeQuery(sql, statement -> {
            statement.setString(1, "%" + searchTerm + "%");
        }, resultSet -> {
            ObservableList<String> venues = FXCollections.observableArrayList();
            while (resultSet.next()) {
                venues.add(resultSet.getString("venue"));
            }
            return venues;
        });
        return FXCollections.observableArrayList(results);
    }

    public ObservableList<String> searchEventCapacities(String searchTerm) throws SQLException {
        String sql = "SELECT DISTINCT CAST(capacity AS CHAR) AS capacity FROM Events WHERE CAST(capacity AS CHAR) LIKE ? LIMIT 10";
        List<String> results = connection.executeQuery(sql, statement -> {
            statement.setString(1, "%" + searchTerm + "%");
        }, resultSet -> {
            ObservableList<String> capacities = FXCollections.observableArrayList();
            while (resultSet.next()) {
                capacities.add(resultSet.getString("capacity"));
            }
            return capacities;
        });
        return FXCollections.observableArrayList(results);
    }

    /**
     * Searches for events based on the scheduled date.
     * @param scheduled The date selected by the user.
     * @return A list of events matching the selected date.
     * @throws SQLException If the query fails.
     */
    public ObservableList<SearchEvent> searchEventsByScheduledDate(LocalDate scheduled) throws SQLException {
        String sql = "SELECT * FROM Events WHERE DATE(scheduled) = ?";

        return connection.executeQuery(sql, statement -> {
            statement.setDate(1, java.sql.Date.valueOf(scheduled));  // Convert LocalDate to java.sql.Date
        }, resultSet -> {
            ObservableList<SearchEvent> events = FXCollections.observableArrayList();
            while (resultSet.next()) {
                events.add(new SearchEvent(
                        resultSet.getInt("Id"),  // Assuming column is "Id"
                        resultSet.getInt("CommunityId"),  // Assuming column is "CommunityId"
                        resultSet.getString("Name"),
                        resultSet.getString("Description"),
                        resultSet.getString("Venue"),
                        resultSet.getInt("Capacity"),
                        resultSet.getDate("Scheduled")  // Assuming this is java.sql.Date
                ));
            }
            return events;
        });
    }

    public ObservableList<SearchEvent> searchEvents(String name, String description, String venue, String capacity, LocalDate scheduledDate) throws SQLException {
        String sql = "SELECT * FROM Events WHERE 1=1";
        if (name != null && !name.isEmpty()) {
            sql += " AND name LIKE ?";
        }
        if (description != null && !description.isEmpty()) {
            sql += " AND description LIKE ?";
        }
        if (venue != null && !venue.isEmpty()) {
            sql += " AND venue LIKE ?";
        }
        if (capacity != null && !capacity.isEmpty()) {
            sql += " AND capacity = ?";
        }
        if (scheduledDate != null) {
            sql += " AND DATE(scheduledDate) = ?";
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
                statement.setString(paramIndex++, capacity);
            }
            if (scheduledDate != null) {
                statement.setDate(paramIndex++, java.sql.Date.valueOf(scheduledDate));
            }
        }, resultSet -> {
            ObservableList<SearchEvent> events = FXCollections.observableArrayList();
            while (resultSet.next()) {
                events.add(new SearchEvent(
                        resultSet.getInt("id"),
                        resultSet.getInt("communityId"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("venue"),
                        resultSet.getInt("capacity"),
                        resultSet.getDate("scheduled")
                ));
            }
            return events;
        });
    }
}
