package hub.troubleshooters.soundlink.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A functional interface used for preparing {@link PreparedStatement} instances before executing SQL queries or updates.
 *
 * <p>This interface defines a single method, {@code prepare}, which is intended to set the necessary parameters
 * for a {@link PreparedStatement}. Implementations of this interface can define how the statement should be prepared
 * based on the context (e.g., setting parameters for a query).</p>
 *
 * <p>This interface is typically used in conjunction with the {@link SQLiteDatabaseConnection} methods like
 * {@code executeQuery} and {@code executeUpdate}, where a prepared statement is required to execute parameterized
 * SQL queries or updates.</p>
 *
 * <pre>
 * Example usage:
 * {@code
 * String sql = "SELECT * FROM Users WHERE username = ?";
 * StatementPreparer preparer = statement -> statement.setString(1, "john_doe");
 * }
 * </pre>
 *
 * @see java.sql.PreparedStatement
 * @see SQLiteDatabaseConnection#executeQuery
 * @see SQLiteDatabaseConnection#executeUpdate
 */
@FunctionalInterface
public interface StatementPreparer {
    /**
     * Prepares a {@link PreparedStatement} by setting necessary parameters before executing a query or update.
     *
     * @param statement The {@link PreparedStatement} to prepare.
     * @throws SQLException If an SQL error occurs while preparing the statement.
     */
    void prepare(PreparedStatement statement) throws SQLException;
}