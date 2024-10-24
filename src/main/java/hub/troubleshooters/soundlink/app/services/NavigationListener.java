package hub.troubleshooters.soundlink.app.services;

/**
 * An interface that allows subscription to the onNavigationState event found in SceneManager.
 * @see SceneManager
 */
@FunctionalInterface
public interface NavigationListener {
    /**
     * Listener called on {@code NavigationStateChange} event.
     */
    void onNavigationStateChange();
}
