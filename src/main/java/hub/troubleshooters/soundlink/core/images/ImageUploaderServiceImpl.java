package hub.troubleshooters.soundlink.core.images;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.factories.ImageFactory;
import hub.troubleshooters.soundlink.data.models.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public class ImageUploaderServiceImpl implements ImageUploaderService {
    private final ImageFactory imageFactory;
    private final String imageDirectory = System.getProperty("user.home") + "/.soundlink/images/";  // images are stored in ~/.soundlink/images

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
}
