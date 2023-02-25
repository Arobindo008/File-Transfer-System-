module com.example.fileshareproject {
    requires javafx.controls;
    requires javafx.fxml;



    opens com.example.fileshareproject to javafx.fxml;
    exports com.example.fileshareproject;

}