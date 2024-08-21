package hub.troubleshooters.soundlink.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.google.inject.Guice;


import java.io.IOException;

public class SoundLinkApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // set up DI injector
        var injector = Guice.createInjector(new AppModule());

        // create FXML loader and set controller factory
        var fxmlLoader = new FXMLLoader(SoundLinkApplication.class.getResource("home-view.fxml"));
        fxmlLoader.setControllerFactory(new GuiceControllerFactory(injector));

        var scene = new Scene(fxmlLoader.load(), 960, 540);
        stage.setTitle("SoundLink");
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}