package hub.troubleshooters.soundlink.app.services;

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
     * @see hub.troubleshooters.soundlink.app.areas.Routes
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

    File openFileDialog();

    void navigateToEventDetailsView(int eventId);

    void navigateToCommunityDetailsView(int communityId);

    void navigateToCommunityFeedView(int communityId);
}
