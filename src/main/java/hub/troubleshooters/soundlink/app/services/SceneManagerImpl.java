package hub.troubleshooters.soundlink.app.services;

import hub.troubleshooters.soundlink.app.SoundLinkApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.io.IOException;

public class SceneManagerImpl implements SceneManager {
    private final Stage primaryStage;
    private final Injector injector;

    @Inject
    public SceneManagerImpl(Stage primaryStage, Injector injector) {
        this.primaryStage = primaryStage;
        this.injector = injector;
    }

    @Override
    public void switchToScene(String fxmlFileName, String sceneName, int width, int height) {
        try {
            var loader = new FXMLLoader(SoundLinkApplication.class.getResource(fxmlFileName));
            loader.setControllerFactory(injector::getInstance);
            Parent root = loader.load();
            var scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(sceneName);
            primaryStage.setWidth(width);
            primaryStage.setHeight(height);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // TODO: replace with proper error handling
        }
    }

    @Override
    public void switchToScene(String fxmlFileName) {
        switchToScene(fxmlFileName, "SoundLink", 768, 540);
    }
}
