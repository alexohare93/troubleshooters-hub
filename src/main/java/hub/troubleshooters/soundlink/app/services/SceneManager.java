package hub.troubleshooters.soundlink.app.services;

import hub.troubleshooters.soundlink.app.areas.Routes;
import javafx.scene.control.Alert;

import java.io.File;
import java.util.function.Consumer;

public interface SceneManager {
    /**
     * Switches the root scene to the scene specified by the fxml file name. Erases all history and future.
     * @param fxmlFileName The name of the fxml file
     * @param sceneName The string that will appear in the title bar of the window
     * @param width The width of the window
     * @param height The height of the window
     */
    void switchToScene(String fxmlFileName, String sceneName, int width, int height);

    /**
     * Switches the root scene to the scene specified by the fxml file name
     * @param fxmlFileName The name of the fxml file
     */
    void switchToScene(String fxmlFileName);

    /**
     * Switches the root scene to the shared view and sets the outlet scene to the scene specified by the fxml file name
     * @param fxmlFileName The name of the fxml file to act as the outlet scene
     */
    void switchToOutletScene(String fxmlFileName);

    <T> void switchToOutletScene(String fxmlFileName, Consumer<T> controllerConsumer);

    /**
     * Sends an alert dialogue to the front stage and waits for interaction.
     * @param alert The alert you want displayed.
     */
    void alert(Alert alert);

    /**
     * Navigates back in the outlet history if it exists; else, do nothing.
     */
    void navigateBack();

    /**
     * Navigates to the new route by pushing as an outlet scene. Rewrites the future if it existed.
     * @param route The name of the new route you are navigating to.
     * @see Routes
     */
    void navigate(String route);

    /**
     * Navigates forward in the outlet future if it exists; else, do nothing.
     */
    void navigateForward();

    /**
     * Checks if there is history present in the outlet.
     * @return True if there is history.
     */
    boolean hasHistory();

    /**
     * Checks if there is future present in the outlet.
     * @return True if there is future.
     */
    boolean hasFuture();

    /**
     * Adds a listener to be notified if navigation has occurred within the app.
     * Used by the back/foward navigation buttons to enable/disable them.
     * @param listener The listener method that is called when the subscribed class is notified
     */
    void addNavigationListener(NavigationListener listener);


    /**
     * Opens the File dialog for file upload to the application.
     * @return The {@link File} the user uploads.
     */
    File openFileDialog();

    /**
     * Navigates the scene to the event details view
     * @param eventId The eventId for the event that the event details view will display
     */
    void navigateToEventDetailsView(int eventId);

    /**
     * Navigates the scene to the event details view
     * @param communityId The communityId for the community that the community details view will display
     */
    void navigateToCommunityDetailsView(int communityId);

    /**
     * Listener triggered on notification button click.
     * @param communityId The communityId of the community the notification was in reference to.
     */
    void onNotificationButtonClick(int communityId);

    /**
     * Navigates the scene to the community search view
     */
    void navigateToSearchCommunityView();

    /**
     * Navigates the scene to the community feed view
     * @param communityId The communityId for the community that the community feed view will display
     */
    void navigateToCommunityFeedView(int communityId);

    /**
     * Navigates the scene to the event search view
     */
    void navigateToSearchEventView();
}
