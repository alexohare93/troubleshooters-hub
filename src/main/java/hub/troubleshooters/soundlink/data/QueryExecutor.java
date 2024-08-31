package hub.troubleshooters.soundlink.data;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface QueryExecutor<T> {
    T execute(ResultSet resultSet) throws SQLException;
}