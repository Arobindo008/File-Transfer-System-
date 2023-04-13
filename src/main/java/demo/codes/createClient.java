package demo.codes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class createClient extends clientHandler {
    @FXML
    private Button connectClient;

    @FXML
    private TextField nameTxt;

    @FXML
    void connectClient(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("client.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Client");
        stage.setScene(scene);
        stage.show();

    }


    @FXML
    void setName(ActionEvent event) {

    }
}
