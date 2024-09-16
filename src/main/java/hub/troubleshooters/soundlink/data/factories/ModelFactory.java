package hub.troubleshooters.soundlink.data.factories;

import hub.troubleshooters.soundlink.data.DatabaseConnection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Contains CRUD operations on models with the exception for "create", since creating depends
 * heavily on table structure (e.g. required columns, etc.).
 *
 * @param <T> the model mapping representing the SQL table
 */
public abstract class ModelFactory<T> {

  protected final DatabaseConnection connection;
  protected final String tableName;

  protected ModelFactory(DatabaseConnection connection, String tableName) {
    this.connection = connection;
    this.tableName = tableName;
  }

  /**
   * Saves the model to the database.
   *
   * @param model the model to save
   * @throws SQLException if an error occurs while saving the model
   */
  abstract void save(T model) throws SQLException;

  /**
   * Gets the model in the database with the given ID or an empty Optional.
   *
   * @param id the ID of the model to update
   * @return the model with the given ID, if it exists
   * @throws SQLException if an error occurs while getting the model
   */
  abstract Optional<T> get(int id) throws SQLException;

  /**
   * Deletes the model in the database with the given ID.
   *
   * @param id the ID of the model to delete
   * @throws SQLException if an error occurs while deleting the model
   */
  void delete(int id) throws SQLException {
    final var sql = "DELETE FROM " + tableName + " WHERE id = " + id;
    connection.executeUpdate(
        sql,
        statement -> {
          statement.setInt(1, id);
        },
        rowsAffected -> {
          if (rowsAffected != 1) {
            throw new SQLException(
                "Failed to delete from " + tableName + ". Rows affected: " + rowsAffected);
          }
        });
  }
}
