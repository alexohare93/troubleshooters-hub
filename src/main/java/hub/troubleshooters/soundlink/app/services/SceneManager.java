package hub.troubleshooters.soundlink.app.services;

public interface SceneManager {
    /**
     * Switches the root scene to the scene specified by the fxml file name
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
}
