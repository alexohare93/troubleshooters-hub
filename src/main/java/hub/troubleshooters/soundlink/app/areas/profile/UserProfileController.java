package hub.troubleshooters.soundlink.app.areas.profile;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;

public class UserProfileController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField bioField;
    @FXML
    private TextField preferencesField;
    @FXML
    private TextField postsField;
    @FXML
    private TextField eventsField;
    @FXML
    private TextField communitiesField;

    @FXML
    private ImageView userImageView;
    @FXML
    private Button saveNameButton;
    @FXML
    private Button saveBioButton;
    @FXML
    private Button savePreferencesButton;
    @FXML
    private Button savePostsButton;
    @FXML
    private Button saveEventsButton;
    @FXML
    private Button saveCommunitiesButton;

    @FXML
    public void initialize() {
    }

    @FXML
    public void editName() {
        nameField.setEditable(true);
        saveNameButton.setVisible(true);
    }

    @FXML
    public void saveName() {
        nameField.setEditable(false);
        saveNameButton.setVisible(false);
    }

    @FXML
    public void editBio() {
        bioField.setEditable(true);
        saveBioButton.setVisible(true);
    }

    @FXML
    public void saveBio() {
        bioField.setEditable(false);
        saveBioButton.setVisible(false);
    }

    @FXML
    public void editPreferences() {
        preferencesField.setEditable(true);
        savePreferencesButton.setVisible(true);
    }

    @FXML
    public void savePreferences() {
        preferencesField.setEditable(false);
        savePreferencesButton.setVisible(false);
    }

    @FXML
    public void editPosts() {
        postsField.setEditable(true);
        savePostsButton.setVisible(true);
    }

    @FXML
    public void savePosts() {
        postsField.setEditable(false);
        savePostsButton.setVisible(false);
    }

    @FXML
    public void editEvents() {
        eventsField.setEditable(true);
        saveEventsButton.setVisible(true);
    }

    @FXML
    public void saveEvents() {
        eventsField.setEditable(false);
        saveEventsButton.setVisible(false);
    }

    @FXML
    public void editCommunities() {
        communitiesField.setEditable(true);
        saveCommunitiesButton.setVisible(true);
    }

    @FXML
    public void saveCommunities() {
        communitiesField.setEditable(false);
        saveCommunitiesButton.setVisible(false);
    }

    @FXML
    public void changeImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            userImageView.setImage(image);
        }
    }
}

