package hub.troubleshooters.soundlink.app.areas.shared;

import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class NotificationController {

    @FXML
    private Button notificationButton;

    private int notificationCount = 0;

    public void handleNotifications(ActionEvent event) {
        if (notificationCount > 0) {
            System.out.println("You have " + notificationCount + " new notifications.");
            clearNotifications();
        } else {
            System.out.println("No new notifications.");
        }
    }

    public void addNotification() {
        notificationCount++;
        updateNotificationButton();
    }

    private void clearNotifications() {
        notificationCount = 0;
        updateNotificationButton();
    }

    private void updateNotificationButton() {
        if (notificationCount > 0) {
            notificationButton.setText("Notifications (" + notificationCount + ")");
            notificationButton.setStyle("-fx-background-color: #ffd700; -fx-text-fill: black;");
        } else {
            notificationButton.setText("Notifications");
            notificationButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        }
    }

    public void checkForEventReminders() {
        Platform.runLater(() -> {
            if (checkForUpcomingEvents()) {
                addNotification();
            }
        });
    }

    private boolean checkForUpcomingEvents() {
        return true;
    }
}
