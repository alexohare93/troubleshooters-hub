module hub.troubleshooters.soundlink {
    requires javafx.fxml;
    requires com.google.guice;
    requires java.sql;
    requires bcrypt;
    requires atlantafx.base;


    opens hub.troubleshooters.soundlink.app to javafx.fxml;

    exports hub.troubleshooters.soundlink.app;
    exports hub.troubleshooters.soundlink.core.auth;
    exports hub.troubleshooters.soundlink.core;
    exports hub.troubleshooters.soundlink.data;
    exports hub.troubleshooters.soundlink.core.data.models;
    exports hub.troubleshooters.soundlink.app.areas.login;
    opens hub.troubleshooters.soundlink.app.areas.login to javafx.fxml;
    exports hub.troubleshooters.soundlink.app.areas.home;
    opens hub.troubleshooters.soundlink.app.areas.home to javafx.fxml;
    exports hub.troubleshooters.soundlink.app.services;
    exports hub.troubleshooters.soundlink.data.factories;
    exports hub.troubleshooters.soundlink.data.models;
}