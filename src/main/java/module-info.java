module hub.troubleshooters.soundlink {
    requires javafx.fxml;
    requires com.google.guice;
    requires java.sql;
    requires bcrypt;
    requires atlantafx.base;
    requires jsr305;


    opens hub.troubleshooters.soundlink.app to javafx.fxml;

    exports hub.troubleshooters.soundlink.app;
    exports hub.troubleshooters.soundlink.core.auth;
    exports hub.troubleshooters.soundlink.core;
    exports hub.troubleshooters.soundlink.data;
    exports hub.troubleshooters.soundlink.app.areas.login;
    opens hub.troubleshooters.soundlink.app.areas.login to javafx.fxml;
    exports hub.troubleshooters.soundlink.app.areas.home;
    opens hub.troubleshooters.soundlink.app.areas.home to javafx.fxml;
    exports hub.troubleshooters.soundlink.app.services;
    exports hub.troubleshooters.soundlink.data.factories;
    exports hub.troubleshooters.soundlink.data.models;
    exports hub.troubleshooters.soundlink.app.areas.shared;
    opens hub.troubleshooters.soundlink.app.areas.shared to javafx.fxml;
    opens hub.troubleshooters.soundlink.app.areas.events to javafx.fxml, com.google.guice;
    exports hub.troubleshooters.soundlink.core.validation;
    exports hub.troubleshooters.soundlink.core.auth.services;
    exports hub.troubleshooters.soundlink.core.auth.validation;
    exports hub.troubleshooters.soundlink.core.events.validation to com.google.guice, javafx.fxml;
    exports hub.troubleshooters.soundlink.core.communities.validation to com.google.guice, javafx.fxml;
    exports hub.troubleshooters.soundlink.core.events.services to com.google.guice, javafx.fxml;
    exports hub.troubleshooters.soundlink.core.communities.services to com.google.guice, javafx.fxml;
    exports hub.troubleshooters.soundlink.core.communities.models;
    exports hub.troubleshooters.soundlink.core.events.models;
    exports hub.troubleshooters.soundlink.app.components to javafx.fxml;
    exports hub.troubleshooters.soundlink.core.auth.models;
    exports hub.troubleshooters.soundlink.core.images;

    exports hub.troubleshooters.soundlink.app.areas.profile;
    opens hub.troubleshooters.soundlink.app.areas.profile to javafx.fxml, com.google.guice;

    opens hub.troubleshooters.soundlink.app.areas.communities to com.google.guice, javafx.fxml;
}