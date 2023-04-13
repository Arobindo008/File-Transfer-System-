package demo.codes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class demoSever {
    private static final int PORT = 5000;

    // List of all connected clients
    private List<PrintWriter> clientList = new ArrayList<>();

    public void start() throws IOException {
        // Create a server socket and start listening for client connections
        try (ServerSocket listener = new ServerSocket(PORT)) {
            System.out.println("Chat server is running...");
            while (true) {
                // Accept a new client connection
                Socket clientSocket = listener.accept();
                System.out.println("New client connected: " + clientSocket);

                // Create a new thread to handle the client connection
                new ClientHandler(clientSocket).start();
            }
        }
    }

    /**
     * Sends a message to all connected clients.
     */
    private synchronized void broadcast(String message) {
        System.out.println("Broadcasting message: " + message);
        for (PrintWriter out : clientList) {
            out.println(message);
        }
    }

    /**
     * Handles a single client connection.
     */
    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                // Create input and output streams for the client socket
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Add the output stream to the list of connected clients
                synchronized (clientList) {
                    clientList.add(out);
                }

                // Read messages from the client and broadcast them to all connected clients
                String input;
                while ((input = in.readLine()) != null) {
                    System.out.println("Received message from " + clientSocket + ": " + input);
                    broadcast(input);
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e);
            } finally {
                // Remove the output stream from the list of connected clients
                if (out != null) {
                    synchronized (clientList) {
                        clientList.remove(out);
                    }
                }
                try {
                    clientSocket.close();
                    System.out.println("Client disconnected: " + clientSocket);
                } catch (IOException e) {
                    System.out.println("Error closing client socket: " + e);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Server().start();
    }
}
