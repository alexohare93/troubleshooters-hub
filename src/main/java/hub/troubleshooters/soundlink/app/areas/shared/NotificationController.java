package hub.troubleshooters.soundlink.app;

import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainController {

    @FXML
    private Button notificationButton;

    private int notificationCount = 0; // Variable to track the number of notifications

    // This method is called when the notification button is clicked
    public void handleNotifications(ActionEvent event) {
        if (notificationCount > 0) {
            // Display notifications (you can replace this with actual notification details)
            System.out.println("You have " + notificationCount + " new notifications.");
            
            // After viewing, clear the notifications
            clearNotifications();
        } else {
            // No notifications, display a message or do nothing
            System.out.println("No new notifications.");
        }
    }

    // Method to simulate receiving new notifications
    public void addNotification() {
        notificationCount++;
        updateNotificationButton();
    }

    // Method to clear notifications after the user views them
    private void clearNotifications() {
        notificationCount = 0; // Reset the count
        updateNotificationButton(); // Update the button to reflect that notifications are cleared
    }

    // Method to update the button text based on the number of notifications
    private void updateNotificationButton() {
        if (notificationCount > 0) {
            // Update button text to show the number of notifications
            notificationButton.setText("Notifications (" + notificationCount + ")");
            notificationButton.setStyle("-fx-background-color: #ffd700; -fx-text-fill: black;"); // Change button style to indicate new notifications
        } else {
            // Reset button text and style when there are no notifications
            notificationButton.setText("Notifications");
            notificationButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        }
    }

    // Method to quietly notify the user (could be called periodically)
    public void checkForEventReminders() {
        // This can run in the background or when the app starts, and updates the button appearance quietly
        Platform.runLater(() -> {
            if (checkForUpcomingEvents()) {
                addNotification(); // Simulate receiving a notification
            }
        });
    }

    // Method to simulate event checking (replace with your actual logic)
    private boolean checkForUpcomingEvents() {
        // Simulate an upcoming event (you'd replace this with actual event-checking logic)
        return true; // Simulates that an event is upcoming
    }
}
