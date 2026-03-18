module com.machinist.machinist {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens com.machinist.machinist to javafx.fxml;
    exports com.machinist.machinist;
}