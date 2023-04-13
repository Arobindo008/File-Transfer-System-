package demo.codes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class serverHandler extends Server {
    @FXML
    private Button startserver;

    @FXML
    void Server_Start(ActionEvent event) throws IOException {
        new Server().start();
    }

}
