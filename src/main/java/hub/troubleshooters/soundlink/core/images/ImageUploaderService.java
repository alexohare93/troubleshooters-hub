package hub.troubleshooters.soundlink.core.images;

import hub.troubleshooters.soundlink.data.models.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.sql.SQLException;

public interface ImageUploaderService {
    Image upload(File file) throws IOException, SQLException;
    File getImageFile(Image image) throws InvalidPathException;
    File getSampleBannerImageFile(int id) throws InvalidPathException;
}
