package hub.troubleshooters.soundlink.data;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A functional interface that defines a single abstract method for executing operations on a {@link ResultSet}.
 * This interface is typically used to process the results of a database query and return an object of type {@code T}.
 *
 * @param <T> The type of object returned by the execution of the query.
 */
@FunctionalInterface
public interface QueryExecutor<T> {
    /**
     * Executes an operation on the provided {@link ResultSet} and returns a value of type {@code T}.
     *
     * @param resultSet The {@link ResultSet} object containing the query results.
     * @return A value of type {@code T}, which is the result of processing the {@link ResultSet}.
     * @throws SQLException If an SQL error occurs while processing the {@link ResultSet}.
     */
    T execute(ResultSet resultSet) throws SQLException;
}