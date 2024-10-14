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

public class ImageUploaderServiceImpl implements ImageUploaderService {
    private final ImageFactory imageFactory;
    private final String imageDirectory = System.getProperty("user.home") + "/.soundlink/images/";  // images are stored in ~/.soundlink/images
    private final String sampleImageDirectory = System.getProperty("user.home") + "/.soundlink/default-banner-images/";  // images are stored in ~/.soundlink/default-banner-images

    @Inject
    public ImageUploaderServiceImpl(ImageFactory imageFactory) {
        this.imageFactory = imageFactory;

        // make sure the directory exists
        File directory = new File(imageDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public Image upload(File file) throws IOException, SQLException {
        var fileName = file.getName();
        var destination = Path.of(imageDirectory, fileName);

        // copy the file to program's data dir
        Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

        // ToDo: really should make the 'create' method return the newly created row
        imageFactory.create(fileName);
        return imageFactory.get(fileName).get();    // unwrap should be safe unless something internal is horribly wrong
    }

    public File getImageFile(Image image) throws InvalidPathException {
        var fileName = image.getFileName();
        var path = Path.of(imageDirectory, fileName);

        return path.toFile();
    }

    public File getSampleBannerImageFile(int id) throws InvalidPathException {
        id %= 7;    // there are only 7 sample banner images in the folder directory. Can update this if images are added or removed
                    // it's a bit hacky, but was quick to implement.
        var fileName = id + ".jpg";
        var path = Path.of(sampleImageDirectory, fileName);

        return path.toFile();
    }
}
