package hub.troubleshooters.soundlink.app.areas.communities;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import hub.troubleshooters.soundlink.app.UserDataStore;


public class CreateCommunityController {

    @FXML
    private TextField communityNameField;

    @FXML
    public void onCreateCommunityButtonPressed() {

        String communityName = "Test Community";

        UserDataStore userDataStore = UserDataStore.getInstance();

        userDataStore.addUserCommunity(communityName);

    }
}
