package hub.troubleshooters.soundlink.data.models;

public class Image {
    private final int id;
    private String fileName;

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
}
