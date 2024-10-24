package hub.troubleshooters.soundlink.app;

import atlantafx.base.theme.PrimerLight;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.services.SceneManagerImpl;
import javafx.application.Application;
import javafx.stage.Stage;
import com.google.inject.Guice;


import java.io.IOException;

/**
 * Main class for the Soundlink application.
 */
public class SoundLinkApplication extends Application {

    /**
     * Initializes the Guice injector and routes the application to the login screen.
     * @param stage A {@link Stage}, the main view for the application.
     * @throws IOException If there is an IO Error.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // set up DI injector
        var injector = Guice.createInjector(new AppModule(stage));
        var sceneManager = injector.getInstance(SceneManagerImpl.class);

        // set theme
        setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        // switch to initial scene (login screen)
        sceneManager.switchToScene(Routes.LOGIN);
    }

    /**
     * Launches the main event loop of the application.
     * @param args Starting parameters, none are valid.
     */
    public static void main(String[] args) {
        launch();
    }
}