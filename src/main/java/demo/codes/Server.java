package demo.codes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server extends Application {
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Server.class.getResource("server.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        launch();
    }

    private static final int PORT = 5000;
    private List<PrintWriter> clientList;

    public Server() {
        clientList = new ArrayList<>();
    }

    public void start() throws IOException {
        try (ServerSocket listener = new ServerSocket(PORT)) {
            System.out.println("Server is running...");
            while (true) {
                new Handler(listener.accept()).start();
            }
        }
    }

    private void broadcast(String message) {
        for (PrintWriter writer : clientList) {
            writer.println(message);
        }
    }

    private class Handler extends Thread {
        private Socket socket;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                clientList.add(out);

                Scanner in = new Scanner(socket.getInputStream());
                while (in.hasNextLine()) {
                    String input = in.nextLine();
                    System.out.println("Received message: " + input);
                    broadcast(input);
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    clientList.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
    }

}
