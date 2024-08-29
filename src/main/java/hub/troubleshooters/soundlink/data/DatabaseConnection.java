package hub.troubleshooters.soundlink.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * An interface which can be safely injected into required services to interface with the DB.
 */
public interface DatabaseConnection {
    /**
     * Safely executes a prepared statement by locally initializing and disposing of the database connection.
     * @param query the prepared statement
     * @param executor typically a lambda function which can map the results into a usable model
     * @return a model of what data you're querying
     * @param <T> a model of what data you're querying
     * @throws SQLException if the statement fails in some way
     */
    <T> T executeQuery(PreparedStatement query, QueryExecutor<T> executor) throws SQLException;
    void executeUpdate(PreparedStatement query, UpdateExecutor executor) throws SQLException;
}