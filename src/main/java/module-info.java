module com.angeldiaz.musicorganizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires mp3agic;


    opens com.angeldiaz.musicorganizer to javafx.fxml;
    exports com.angeldiaz.musicorganizer;
}