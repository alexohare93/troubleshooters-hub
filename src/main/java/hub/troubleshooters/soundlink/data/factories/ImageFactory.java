package hub.troubleshooters.soundlink.data.factories;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Image;

import java.sql.SQLException;
import java.util.Optional;

public class ImageFactory extends ModelFactory<Image>{

    @Inject
    public ImageFactory(DatabaseConnection connection) {
        super(connection, "Images");
    }


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
