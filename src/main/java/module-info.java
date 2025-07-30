module com.example.taskproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.taskproject to javafx.fxml;
    exports com.example.taskproject;

}