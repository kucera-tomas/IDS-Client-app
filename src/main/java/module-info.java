module org.example.idsclientapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens org.example.idsclientapp to javafx.fxml;
    exports org.example.idsclientapp;
}