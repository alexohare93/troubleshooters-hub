module hub.troubleshooters.soundlink {
    requires javafx.controls;
    requires javafx.fxml;


    opens hub.troubleshooters.soundlink.app to javafx.fxml;
    exports hub.troubleshooters.soundlink.app;
}