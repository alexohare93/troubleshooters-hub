package hub.troubleshooters.soundlink.data.models;

/**
 * Represents an image in the system, containing information such as the image ID and file name.
 */
public class Image {
    private final int id;
    private String fileName;

    /**
     * Constructs a new {@code Image} object with the specified ID and file name.
     *
     * @param id The unique ID of the image.
     * @param fileName The name of the image file.
     */
    public Image(int id, String fileName) {
        this.id = id;
        this.fileName = fileName;
    }

    /**
     * Gets the unique ID of the image.
     *
     * @return The ID of the image.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the file name of the image.
     *
     * @return The file name of the image.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the file name of the image.
     *
     * @param fileName The file name to set.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}