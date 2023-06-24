package demo.codes;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;



public class Client extends Application {


        public void start(Stage Primarystage) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("client.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Primarystage.setTitle("Client");
            Primarystage.setScene(scene);
            Primarystage.show();
        }



        public static void main(String[] args) throws IOException {
            launch();

        }


    @FXML
    private Button filechooser;

    @FXML
    void ChooseFile(ActionEvent event) {
        FileChooser fc = new FileChooser();

        File file = fc.showOpenDialog(new Stage());


        }
    }



