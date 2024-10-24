package hub.troubleshooters.soundlink.data;

import java.sql.*;

/**
 * A class that implements the {@link DatabaseConnection} interface, providing methods to execute SQL queries and updates
 * using an SQLite database.
 * This class manages the connection to the SQLite database and provides mechanisms for query execution and updates
 * with flexible statement preparation and result processing.
 */
public class SQLiteDatabaseConnection implements DatabaseConnection {
    private final String connectionString;

    /**
     * Constructs a new {@code SQLiteDatabaseConnection} with the specified connection string.
     *
     * @param connectionString The SQLite connection string to be used for establishing database connections.
     */
    public SQLiteDatabaseConnection(String connectionString) {
        this.connectionString = connectionString;
    }

    /**
     * Executes a SQL query on the SQLite database. The provided SQL query is prepared and executed,
     * and the {@link QueryExecutor} is used to process the result set returned by the query.
     *
     * <p> The caller is responsible for providing both the SQL query and the statement preparation,
     * which is performed using the {@link StatementPreparer}. The result set is processed using the {@link QueryExecutor},
     * which allows flexible handling of the query results.</p>
     *
     * @param <T>      The type of result returned by the query executor.
     * @param sql      The SQL query to be executed.
     * @param preparer The statement preparer used to set parameters in the prepared statement.
     * @param executor The query executor used to process the {@link ResultSet} returned by the query.
     * @return A result of type {@code T}, which is the output of the {@link QueryExecutor}.
     * @throws SQLException If any SQL error occurs during query execution.
     */
    public <T> T executeQuery(String sql, StatementPreparer preparer, QueryExecutor<T> executor) throws SQLException {
        var connection = DriverManager.getConnection(connectionString);
        var statement = connection.prepareStatement(sql);
        preparer.prepare(statement);

        var resultSet = statement.executeQuery();
        var result = executor.execute(resultSet);

        connection.close();
        return result;
    }

    /**
     * Executes a SQL update on the SQLite database. The provided SQL update query is prepared and executed,
     * and the number of affected rows is passed to the {@link UpdateExecutor}.
     *
     * <p>This method is used for executing SQL operations that modify the database (e.g., {@code INSERT},
     * {@code UPDATE}, {@code DELETE} statements). The caller is responsible for providing the SQL query
     * and a statement preparer, which prepares the statement with necessary parameters.</p>
     *
     * @param sql      The SQL update query to be executed.
     * @param preparer The statement preparer used to set parameters in the prepared statement.
     * @param executor The update executor used to handle the number of rows affected by the update.
     * @throws SQLException If any SQL error occurs during the update execution.
     */
    public void executeUpdate(String sql, StatementPreparer preparer, UpdateExecutor executor) throws SQLException {
        var connection = DriverManager.getConnection(connectionString);
        var statement = connection.prepareStatement(sql);
        preparer.prepare(statement);

        var rowsAffected = statement.executeUpdate();
        executor.execute(rowsAffected);
        connection.close();
    }
}