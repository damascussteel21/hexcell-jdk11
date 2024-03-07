module com.jramos.hexcelljdk11 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.jramos.hexcelljdk11 to javafx.fxml;
    exports com.jramos.hexcelljdk11;
}