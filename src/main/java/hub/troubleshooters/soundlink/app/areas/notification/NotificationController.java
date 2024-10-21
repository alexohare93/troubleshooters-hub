package hub.troubleshooters.soundlink.app.areas.notification;

import hub.troubleshooters.soundlink.app.areas.Routes;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.google.inject.Inject;

public class NotificationController {

    private final IntegerProperty notificationCount = new SimpleIntegerProperty(0);
    private final SceneManager sceneManager;  // Inject SceneManager to handle navigation

    @FXML
    private Button notificationButton;

    @Inject
    public NotificationController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        bindNotificationButton();
    }

    private void bindNotificationButton() {
        notificationButton.textProperty().bind(notificationCount.asString("Notifications (%d)"));

        notificationCount.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() > 0) {
                notificationButton.setStyle("-fx-background-color: #ffd700; -fx-text-fill: black;");
            } else {
                notificationButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
            }
        });
    }

    public void incrementNotifications() {
        notificationCount.set(notificationCount.get() + 1);
    }

    public void clearNotifications() {
        if (notificationCount.get() > 0) {
            notificationCount.set(notificationCount.get() - 1);
        }
    }

    @FXML
    private void onNotificationButtonClick(ActionEvent event) {
        // Navigate to the admin approval page when the notification button is clicked
        sceneManager.navigate(Routes.ADMIN);
    }
}
