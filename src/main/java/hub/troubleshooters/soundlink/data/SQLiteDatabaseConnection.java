package hub.troubleshooters.soundlink.data;

import java.sql.*;

public class SQLiteDatabaseConnection implements DatabaseConnection {
    private final String connectionString;

    public SQLiteDatabaseConnection(String connectionString) {
        this.connectionString = connectionString;
    }

    public <T> T executeQuery(PreparedStatement query, QueryExecutor<T> executor) throws SQLException {
        var connection = DriverManager.getConnection(connectionString);
        var resultSet = query.executeQuery();
        var result = executor.execute(resultSet);
        connection.close();
        return result;
    }

    public void executeUpdate(PreparedStatement query, UpdateExecutor executor) throws SQLException {
        var connection = DriverManager.getConnection(connectionString);
        var rowsAffected = query.executeUpdate();
        executor.execute(rowsAffected);
        connection.close();
    }
}