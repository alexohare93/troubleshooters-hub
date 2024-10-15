package hub.troubleshooters.soundlink.data.models;

public class Image {
    private final int id;
    private String fileName;

    private static final String BASE_IMAGE_PATH = "/path/to/images/";

    public Image(int id, String fileName) {
        this.id = id;
        this.fileName = fileName;
    }

    public int getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return BASE_IMAGE_PATH + fileName;
    }
}
