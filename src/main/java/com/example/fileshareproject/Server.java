package com.example.fileshareproject;



import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static ArrayList<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(4444);
        System.out.println("Server started.");

        while (true) {
            Socket s = null;
            try {
                s = ss.accept();
                System.out.println("New client connected: " + s);

                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Creating a new handler for the client.");
                ClientHandler handler = new ClientHandler(s, dis, dos);

                clients.add(handler);
                handler.start();
            } catch (Exception e) {
                s.close();
                e.printStackTrace();
            }
        }
    }

    static class ClientHandler extends Thread {
        final Socket s;
        final DataInputStream dis;
        final DataOutputStream dos;

        public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
            this.s = s;
            this.dis = dis;
            this.dos = dos;
        }

        @Override
        public void run() {
            String received;
            while (true) {
                try {
                    received = dis.readUTF();

                    if (received.equals("logout")) {
                        System.out.println("Client disconnected: " + s);
                        this.s.close();
                        break;
                    }

                    String[] arr = received.split("#");

                    String type = arr[0];


                    if (type.equals("file")) {
                        String filename = arr[1];
//                        FileOutputStream fos = new FileOutputStream("received/" + filename);

                        long fileSize = Long.parseLong(arr[2]);

                        byte[] buffer = new byte[4096];
                        int read = 0;
                        int totalRead = 0;
                        long remaining = fileSize;

//                        FileOutputStream fos = new FileOutputStream("Received\\" + filename);
                        // extract file extension
                        String fileExtension = filename.substring(filename.lastIndexOf("."));
                        // create output file name with original file extension
                        String filenameWithoutExtension = filename.substring(0, filename.lastIndexOf('.'));
                        FileOutputStream fos = new FileOutputStream("Received\\" + filenameWithoutExtension + fileExtension);

                        BufferedOutputStream bos = new BufferedOutputStream(fos);

                        while ((read = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {
                            totalRead += read;
                            remaining -= read;
                            bos.write(buffer, 0, read);
                        }

                        bos.flush();
                        bos.close();
                        fos.close();

                        System.out.println("File received: " + filename);
                        sendToAllClients("File received: " + filename);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                this.dis.close();
                this.dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendToAllClients(String message) {
            for (ClientHandler client : clients) {
                try {
                    client.dos.writeUTF(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
