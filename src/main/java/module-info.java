module com.example.machinistapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;

    opens com.example.machinistapp to javafx.fxml;
    exports com.example.machinistapp;
}
