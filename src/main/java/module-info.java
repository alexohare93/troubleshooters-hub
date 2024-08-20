module hub.troubleshooters.soundlink {
    requires javafx.controls;
    requires javafx.fxml;


    opens hub.troubleshooters.soundlink to javafx.fxml;
    exports hub.troubleshooters.soundlink;
}