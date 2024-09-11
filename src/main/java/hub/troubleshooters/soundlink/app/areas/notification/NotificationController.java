package hub.troubleshooters.soundlink.app.areas.shared;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class NotificationController {

    private final IntegerProperty notificationCount = new SimpleIntegerProperty(0);

    @FXML
    private Button notificationButton;

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
        notificationCount.set(0);
    }
}
