package hub.troubleshooters.soundlink.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * An interface which can be safely injected into required services to interface with the DB.
 */
public interface DatabaseConnection {
    /**
     * Safely executes a prepared query statement by locally initializing and disposing of the database connection.
     * @param sql the unformatted SQL
     * @param preparer typically a lambda function which can add arguments to the SQL statement
     * @param executor typically a lambda function which can map the results into a usable model
     * @return a model of what data you're querying
     * @param <T> a model of what data you're querying
     * @throws SQLException if the statement fails in some way
     */
    <T> T executeQuery(String sql, StatementPreparer preparer, QueryExecutor<T> executor) throws SQLException;

    /**
     * Safely executes a prepared update statement by locally initializing and disposing of the database connection.
     * @param sql the unformatted SQL
     * @param preparer typically a lambda function which can add arguments to the SQL statement
     * @param executor typically a lambda function which can map the results into a usable model
     * @throws SQLException if the statement fails in some way
     */
    void executeUpdate(String sql, StatementPreparer preparer, UpdateExecutor executor) throws SQLException;
}