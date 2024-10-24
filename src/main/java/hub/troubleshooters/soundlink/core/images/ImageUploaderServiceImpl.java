package hub.troubleshooters.soundlink.core.images;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.factories.ImageFactory;
import hub.troubleshooters.soundlink.data.models.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

/**
 * Service implementation for uploading and retrieving images within the system.
 *
 * <p>This service manages image uploads, stores them in a local directory, and provides methods for retrieving
 * sample images and their corresponding file paths.</p>
 */
public class ImageUploaderServiceImpl implements ImageUploaderService {
    private final ImageFactory imageFactory;
    private final String imageDirectory = System.getProperty("user.dir") + "/app_data/images/";  // images are stored in app_data/images
    private final String sampleBannerImageDirectory = System.getProperty("user.dir") + "/app_data/default-banner-images/";  // images are stored in app_data/default-banner-images
    private final String sampleProfileImageDirectory = System.getProperty("user.dir") + "/app_data/default-profile-images/";  // images are stored in app_data/default-banner-images

    /**
     * Constructs an ImageUploaderServiceImpl with an {@link ImageFactory}.
     * It ensures that the image storage directory exists.
     *
     * @param imageFactory The factory for handling image database operations.
     */
    @Inject
    public ImageUploaderServiceImpl(ImageFactory imageFactory) {
        this.imageFactory = imageFactory;

        // make sure the directory exists
        File directory = new File(imageDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Uploads the given image file to the application's image directory.
     *
     * <p>The image is stored in the {@code app_data/images/} directory, and a corresponding entry
     * is created in the database.</p>
     *
     * @param file The image file to upload.
     * @return The uploaded image's metadata.
     * @throws IOException   If an error occurs during file upload.
     * @throws SQLException  If an error occurs during database operations.
     */
    public Image upload(File file) throws IOException, SQLException {
        var fileName = file.getName();
        var destination = Path.of(imageDirectory, fileName);

        // copy the file to program's data dir
        Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

        // ToDo: really should make the 'create' method return the newly created row
        imageFactory.create(fileName);
        return imageFactory.get(fileName).get();    // unwrap should be safe unless something internal is horribly wrong
    }

    /**
     * Retrieves a sample banner image based on the provided community ID.
     *
     * <p>This method is designed to provide one of the seven available sample banner images, selected
     * based on the community ID.</p>
     *
     * @param id The ID of the community.
     * @return The sample banner image file.
     * @throws InvalidPathException If the file path is invalid.
     */
    public File getSampleBannerImageFile(int id) throws InvalidPathException {
        id %= 7;    // there are only 7 sample banner images in the folder directory. Can update this if images are added or removed
                    // it's a bit hacky, but was quick to implement.
        var fileName = id + ".jpg";
        var path = Path.of(sampleBannerImageDirectory, fileName);

        return path.toFile();
    }

    /**
     * Retrieves the default profile image file.
     *
     * @return The default profile image file.
     */
    public File getDefaultProfileImageFile() {
        var path = Path.of(sampleProfileImageDirectory, "1.png");
        return path.toFile();
    }

    /**
     * Returns the full file protocol path (e.g., "file:///...") for the provided file.
     *
     * @param file The file for which the protocol path is to be retrieved.
     * @return The full protocol path as a string.
     */
    public String getFullProtocolPath(File file) {
        return "file:///" + file.getAbsolutePath();
    }

    /**
     * Returns the full file protocol path (e.g., "file:///...") for the provided image.
     *
     * @param img The image for which the protocol path is to be retrieved.
     * @return The full protocol path as a string.
     */
    public String getFullProtocolPath(Image img) {
        return getFullProtocolPath(getImageFile(img));
    }

    /**
     * Retrieves the image file from the file system based on the provided image metadata.
     *
     * @param image The image metadata.
     * @return The corresponding image file.
     * @throws InvalidPathException If the file path is invalid.
     */
    private File getImageFile(Image image) throws InvalidPathException {
        var fileName = image.getFileName();
        var path = Path.of(imageDirectory, fileName);

        return path.toFile();
    }
}
