package hub.troubleshooters.soundlink.data.factories;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Contains CRUD operations on models with the exception for "create", since
 * creating depends heavily on table structure (e.g. required columns, etc.).
 * @param <T> the model mapping representing the SQL table
 */
public interface ModelFactory<T> {
    /**
     * Saves the model to the database.
     * @param model the model to save
     * @throws SQLException if an error occurs while saving the model
     */
    void save(T model) throws SQLException;

    /**
     * Gets the model in the database with the given ID or an empty Optional.
     * @param id the ID of the model to update
     * @return the model with the given ID, if it exists
     * @throws SQLException if an error occurs while getting the model
     */
    Optional<T> get(int id) throws SQLException;

    /**
     * Deletes the model in the database with the given ID.
     * @param id the ID of the model to delete
     * @throws SQLException if an error occurs while deleting the model
     */
    void delete(int id) throws SQLException;
}
