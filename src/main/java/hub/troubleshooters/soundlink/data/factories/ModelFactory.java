package hub.troubleshooters.soundlink.data.factories;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Contains CRUD operations on models with the exception for "create", since
 * creating depends heavily on table structure (e.g. required columns, etc.).
 * @param <T> the model mapping representing the SQL table
 */
public interface ModelFactory<T> {
    void save(T model) throws SQLException;
    Optional<T> get(int id) throws SQLException;
    void delete(int id) throws SQLException;
}
