package com.example.fileshareproject;


import java.io.*;
import java.net.*;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Client extends Application {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String username;
    private TextArea chatLog;
    private Button sendFileButton;
    private Label sendStatusLabel;
    private Button chooseFileButton;
    private Label chosenFileLabel;
    private File chosenFile;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("File Sharing Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Create the chat log area
        chatLog = new TextArea();
        chatLog.setEditable(false);
        chatLog.setWrapText(true);
        root.setCenter(chatLog);

        // Create the file chooser controls
        sendFileButton = new Button("Send File");
        sendStatusLabel = new Label();
        chooseFileButton = new Button("Choose File");
        chosenFileLabel = new Label();

        HBox fileChooserBox = new HBox(10);
        fileChooserBox.setPadding(new Insets(10));
        fileChooserBox.getChildren().addAll(sendFileButton, sendStatusLabel, chooseFileButton, chosenFileLabel);
        root.setBottom(fileChooserBox);

        // Set up the file chooser dialog


        // Set up the event handlers
        sendFileButton.setOnAction(new SendFileHandler());
        chooseFileButton.setOnAction(new ChooseFileHandler());

        // Set up the client socket
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Connect to Server");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter server IP address:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String ip = result.get();
            try {
                socket = new Socket(ip, 4444);
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());

                TextInputDialog usernameDialog = new TextInputDialog();
                usernameDialog.setTitle("Username");
                usernameDialog.setHeaderText(null);
                usernameDialog.setContentText("Enter your username:");
                Optional<String> usernameResult = usernameDialog.showAndWait();
                if (usernameResult.isPresent()) {
                    username = usernameResult.get();
                    dos.writeUTF("login#" + username);
                }

                Thread receiveThread = new Thread(new ReceiveHandler());
                receiveThread.start();
            } catch (IOException e) {
                showAlert("Error", "Could not connect to server: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private class SendFileHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (chosenFile != null) {
                try {
                    long fileSize = chosenFile.length();
                    dos.writeUTF("file#" + chosenFile.getName() + "#" + fileSize);

                    FileInputStream fis = new FileInputStream(chosenFile);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead =

                            bis.read(buffer)) > 0) {
                        dos.write(buffer, 0, bytesRead);
                    }
                    bis.close();
                    fis.close();
                    sendStatusLabel.setText("File sent successfully!");
                } catch (IOException e) {
                    showAlert("Error", "Could not send file: " + e.getMessage());
                }
            } else {
                showAlert("Error", "Please choose a file to send.");
            }
        }
    }

    private class ChooseFileHandler implements EventHandler<ActionEvent> {
        FileChooser fileChooser = new FileChooser();
        //fileChooser.setTitle("Choose File to Send");
        @Override
        public void handle(ActionEvent event) {
            chosenFile = fileChooser.showOpenDialog(null);
            if (chosenFile != null) {
                chosenFileLabel.setText(chosenFile.getName());
            }
        }
    }

    private class ReceiveHandler implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    String message = dis.readUTF();
                    if (message.startsWith("message#")) {
                        String[] parts = message.split("#");
                        String username = parts[1];
                        String text = parts[2];
                        chatLog.appendText(username + ": " + text + "\n");
                    } else if (message.startsWith("file#")) {
                        String[] parts = message.split("#");
                        String filename = parts[1];
                        long fileSize = Long.parseLong(parts[2]);


                        FileOutputStream fos = new FileOutputStream(filename);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        byte[] buffer = new byte[4096];
                        long bytesReceived = 0;
                        int bytesRead;
                        while (bytesReceived < fileSize) {
                            bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - bytesReceived));
                            bos.write(buffer, 0, bytesRead);
                            bytesReceived += bytesRead;
                        }
                        bos.close();
                        fos.close();
                        chatLog.appendText("File received: " + filename + "\n");
                    } else if (message.equals("logout")) {
                        showAlert("Logout", "You have been logged out by the server.");
                        socket.close();
                        dis.close();
                        dos.close();
                        Platform.exit();
                    }
                } catch (IOException e) {
                    showAlert("Error", "Error receiving message: " + e.getMessage());
                }
            }
        }

    }
}
