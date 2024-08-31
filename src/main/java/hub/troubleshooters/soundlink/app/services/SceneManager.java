package hub.troubleshooters.soundlink.app.services;

public interface SceneManager {
    void switchToScene(String fxmlFileName, String sceneName, int width, int height);
    void switchToScene(String fxmlFileName);
}
