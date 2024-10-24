package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Image;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Factory class responsible for handling database operations related to {@link Image} models.
 * This class provides methods for creating, retrieving, and updating images in the database.
 */
public class ImageFactory extends ModelFactory<Image>{

    /**
     * Constructs a new {@code ImageFactory} with the specified database connection.
     *
     * @param connection The database connection to be used by the factory.
     */
    @Inject
    public ImageFactory(DatabaseConnection connection) {
        super(connection, "Images");
    }

    /**
     * Updates an existing {@link Image} in the database.
     *
     * @param model The image model to update.
     * @throws SQLException If the update fails or affects an incorrect number of rows.
     */
    @Override
    public void save(Image model) throws SQLException {
        final String sql = "UPDATE Images SET FileName = ? WHERE Id = ?;";
        connection.executeUpdate(sql, statement -> {
            statement.setString(1, model.getFileName());
            statement.setInt(2, model.getId());
        }, rowsAffected -> {
            if (rowsAffected != 1) {
                throw new SQLException("Failed to update image. Rows affected: " + rowsAffected);
            }
        });
    }

    /**
     * Retrieves an {@link Image} by its unique ID.
     *
     * @param id The ID of the image to retrieve.
     * @return An {@code Optional} containing the image if found, or an empty {@code Optional} if not.
     * @throws SQLException If an error occurs during the query.
     */
    @Override
    public Optional<Image> get(int id) throws SQLException {
        final String sql = "SELECT * FROM Images WHERE Id = ?";
        var image = connection.executeQuery(sql, statement -> statement.setInt(1, id), executor -> {
            if (executor.next()) {
                return new Image(
                        executor.getInt("Id"),
                        executor.getString("FileName")
                );
            }
            return null;
        });
        if (image == null) {
            return Optional.empty();
        }
        return Optional.of(image);
    }

    /**
     * Retrieves an {@link Image} by its file name.
     *
     * @param fileName The file name of the image to retrieve.
     * @return An {@code Optional} containing the image if found, or an empty {@code Optional} if not.
     * @throws SQLException If an error occurs during the query.
     */
    public Optional<Image> get(String fileName) throws SQLException {
        final String sql = "SELECT * FROM Images WHERE FileName = ?";
        var image = connection.executeQuery(sql, statement -> statement.setString(1, fileName), executor -> {
            if (executor.next()) {
                return new Image(
                        executor.getInt("Id"),
                        executor.getString("FileName")
                );
            }
            return null;
        });
        if (image == null) {
            return Optional.empty();
        }
        return Optional.of(image);
    }

    /**
     * Creates a new {@link Image} with the specified file name.
     *
     * @param fileName The file name of the new image.
     * @throws SQLException If an error occurs during the image creation process.
     */
    public void create(String fileName) throws SQLException {
        final var sql = "INSERT INTO Images (FileName) VALUES (?)";
        connection.executeUpdate(sql, preparedStatement -> {
            preparedStatement.setString(1, fileName);
        }, affectedRows -> {
            if (affectedRows != 1) {
                throw new SQLException("Failed to create new image: " + fileName);
            }
        });
    }
}
