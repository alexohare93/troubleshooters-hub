package hub.troubleshooters.soundlink.app.services;

/**
 * An interface that allows subscription to the onNavigationState event found in SceneManager.
 * @see SceneManager
 */
@FunctionalInterface
public interface NavigationListener {
    void onNavigationStateChange();
}
