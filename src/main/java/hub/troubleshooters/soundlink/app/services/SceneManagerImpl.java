package hub.troubleshooters.soundlink.app.services;

import hub.troubleshooters.soundlink.app.SoundLinkApplication;
import hub.troubleshooters.soundlink.app.areas.shared.SharedController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.io.IOException;
import java.util.Objects;

public class SceneManagerImpl implements SceneManager {
    private final Stage primaryStage;
    private final Injector injector;

    private Parent root = null;
    private SharedController sharedController = null;

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
            root = loader.load();
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

    public void switchToOutletScene(String fxmlFileName) {
        try {
            var loader = new FXMLLoader(SoundLinkApplication.class.getResource(fxmlFileName));
            loader.setControllerFactory(injector::getInstance);
            Parent outletContent = loader.load();
            if (sharedController == null || root == null || !Objects.equals(root.getId(), "root")) {
                initializeSharedView();
            }
            sharedController.setOutlet(outletContent);
        } catch (IOException e) {
            e.printStackTrace(); // TODO: replace with proper error handling
        }
    }

    private void initializeSharedView() {
        try {
            var loader = new FXMLLoader(SoundLinkApplication.class.getResource("areas/shared/shared_view.fxml"));
            loader.setControllerFactory(injector::getInstance);
            root = loader.load();
            sharedController = loader.getController();
            var scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // TODO: replace with proper error handling
        }
    }
}
