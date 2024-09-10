package hub.troubleshooters.soundlink.app;

import atlantafx.base.theme.PrimerLight;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.services.SceneManagerImpl;
import javafx.application.Application;
import javafx.stage.Stage;
import com.google.inject.Guice;


import java.io.IOException;

public class SoundLinkApplication extends Application {
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

    public static void main(String[] args) {
        launch();
    }
}