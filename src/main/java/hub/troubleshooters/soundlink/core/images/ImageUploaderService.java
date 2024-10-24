package hub.troubleshooters.soundlink.core.images;

import hub.troubleshooters.soundlink.data.models.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.sql.SQLException;

/**
 * Service interface for managing image uploads and retrieving image file paths.
 *
 * <p>This interface provides methods for uploading images, retrieving default or sample images,
 * and generating full protocol paths for images and files.</p>
 */
public interface ImageUploaderService {

    /**
     * Uploads an image file to the designated image storage directory and creates a corresponding
     * database entry for the image.
     *
     * @param file The image file to be uploaded.
     * @return The uploaded image's metadata.
     * @throws IOException If an error occurs during file operations.
     * @throws SQLException If an error occurs during database operations.
     */
    Image upload(File file) throws IOException, SQLException;

    /**
     * Retrieves a sample banner image file for a given community ID.
     *
     * <p>Sample banner images are stored in a designated directory and are selected based on the
     * community ID.</p>
     *
     * @param id The community ID used to select the sample image.
     * @return The sample banner image file.
     * @throws InvalidPathException If the file path is invalid.
     */
    File getSampleBannerImageFile(int id) throws InvalidPathException;

    /**
     * Retrieves the default profile image file.
     *
     * @return The default profile image file.
     */
    File getDefaultProfileImageFile();

    /**
     * Generates a full file protocol path (e.g., "file:///") for the provided file.
     *
     * @param file The file for which the full protocol path is generated.
     * @return The full protocol path as a string.
     */
    String getFullProtocolPath(File file);

    /**
     * Generates a full file protocol path (e.g., "file:///") for the provided image.
     *
     * @param img The image for which the full protocol path is generated.
     * @return The full protocol path as a string.
     */
    String getFullProtocolPath(Image img);
}