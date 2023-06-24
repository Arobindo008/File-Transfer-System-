package demo.codes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class clientHandler {
    @FXML
    private Button connectClient;

    @FXML
    private Button filechooser;

    @FXML
    void ChooseFile(ActionEvent event) {

    }

    @FXML
    void setConnect(ActionEvent event) throws IOException {
        try (Socket socket = new Socket("localhost", 5000)) {
            System.out.println("Connected to server...");

            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Enter message: ");
                String input = scanner.nextLine();
                out.println(input);

                String response = in.nextLine();
                System.out.println("Received response: " + response);
            }
        }
    }
}
