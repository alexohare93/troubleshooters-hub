package hub.troubleshooters.soundlink.core.images;

import hub.troubleshooters.soundlink.data.models.Image;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public interface ImageUploaderService {
    Image upload(File file) throws IOException, SQLException;
}
