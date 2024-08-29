module hub.troubleshooters.soundlink {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.guice;
    requires java.sql;


    opens hub.troubleshooters.soundlink.app to javafx.fxml;

    exports hub.troubleshooters.soundlink.app;
    exports hub.troubleshooters.soundlink.core.auth;
    exports hub.troubleshooters.soundlink.core;
}