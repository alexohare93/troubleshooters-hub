package hub.troubleshooters.soundlink.data;

import java.sql.*;

public class SQLiteDatabaseConnection implements DatabaseConnection {
    private final String connectionString;

    public SQLiteDatabaseConnection(String connectionString) {
        this.connectionString = connectionString;
    }

    public <T> T executeQuery(String sql, StatementPreparer preparer, QueryExecutor<T> executor) throws SQLException {
        var connection = DriverManager.getConnection(connectionString);
        var statement = connection.prepareStatement(sql);
        preparer.prepare(statement);

        var resultSet = statement.executeQuery();
        var result = executor.execute(resultSet);

        connection.close();
        return result;
    }

    public void executeUpdate(String sql, StatementPreparer preparer, UpdateExecutor executor) throws SQLException {
        var connection = DriverManager.getConnection(connectionString);
        var statement = connection.prepareStatement(sql);
        preparer.prepare(statement);

        var rowsAffected = statement.executeUpdate();
        executor.execute(rowsAffected);
        connection.close();
    }
}