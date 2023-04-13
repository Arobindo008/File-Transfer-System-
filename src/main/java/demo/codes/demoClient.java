package demo.codes;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class demoClient extends Application {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    private TextArea messageArea;
    private PrintWriter out;
    private String name;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create UI elements
        TextField nameField = new TextField();
        nameField.setPromptText("Enter name");
        messageArea = new TextArea();
        messageArea.setEditable(false);
        TextField inputField = new TextField();
        inputField.setOnAction(e -> sendMessage(inputField.getText()));
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage(inputField.getText()));

        // Create layout
        VBox layout = new VBox();
        layout.getChildren().addAll(nameField, messageArea, new HBox(inputField, sendButton));

        // Set up connection to server
        Socket socket = new Socket(HOST, PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        new Thread(new Reader(socket)).start();

        // Show UI
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Get name from user
        nameField.setOnAction(e -> {
            name = nameField.getText();
            out.println(name + " has joined the chat");
        });
    }

    private void sendMessage(String message) {
        out.println(name + ": " + message);
        messageArea.appendText(name + ": " + message + "\n");
    }




    private class Reader implements Runnable {
        private BufferedReader in;

        public Reader(Socket socket) throws IOException {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        public void run() {
            try {
                String input;
                while ((input = in.readLine()) != null) {
                    String finalInput = input;
                    Platform.runLater(() -> messageArea.appendText(finalInput + "\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
