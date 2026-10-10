package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SocketServer handles real-time notification broadcasting between Receptionist and Doctor clients.
 * Uses standard Java ServerSocket on port 5000.
 */
public class SocketServer {

    public static final int PORT = 5000;
    private final List<ClientHandler> connectedClients = Collections.synchronizedList(new ArrayList<>());
    private ServerSocket serverSocket;
    private volatile boolean isRunning = false;

    public static void main(String[] args) {
        SocketServer server = new SocketServer();
        // Register JVM Shutdown Hook to release port 5000 cleanly when process is stopped
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.start();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            isRunning = true;
            System.out.println("=================================================");
            System.out.println("🟢 [SocketServer] Started successfully on port " + PORT);
            System.out.println("🟢 [SocketServer] Waiting for clients...");
            System.out.println("=================================================");

            // Continuous loop to accept incoming client connections
            while (isRunning && serverSocket != null && !serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("🔹 [SocketServer] New client connected from: " + clientSocket.getRemoteSocketAddress());
                    
                    ClientHandler handler = new ClientHandler(clientSocket);
                    connectedClients.add(handler);
                    new Thread(handler).start();
                } catch (SocketException e) {
                    if (!isRunning) {
                        break; // Server socket closed intentionally
                    }
                }
            }
        } catch (BindException e) {
            System.err.println("⚠️ [SocketServer] Port " + PORT + " is ALREADY in use by another running SocketServer instance.");
            System.err.println("ℹ️ Please close the active process on port " + PORT + " before running a new instance.");
        } catch (IOException e) {
            if (isRunning) {
                System.err.println("❌ [SocketServer] Server error: " + e.getMessage());
            }
        } finally {
            if (isRunning) {
                stop();
            }
        }
    }

    public synchronized void stop() {
        if (!isRunning) return;
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            // Silently handle close exception
        }
        System.out.println("🔴 [SocketServer] Server stopped.");
    }

    public synchronized void broadcastNotification(String message, ClientHandler sender) {
        System.out.println("📢 [SocketServer] Broadcasting Notification: " + message);
        List<ClientHandler> disconnected = new ArrayList<>();
        
        synchronized (connectedClients) {
            for (ClientHandler client : connectedClients) {
                if (client != sender) {
                    boolean sent = client.sendMessage(message);
                    if (!sent) {
                        disconnected.add(client);
                    }
                }
            }
            connectedClients.removeAll(disconnected);
        }
    }

    // Inner class handling individual client connection socket I/O
    private class ClientHandler implements Runnable {
        private final Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.writer = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                System.err.println("❌ [ClientHandler] Initialization error: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                String line;
                while (isRunning && reader != null && (line = reader.readLine()) != null) {
                    System.out.println("📩 [SocketServer] Received message: " + line);
                    if (line.startsWith("NOTIFICATION|")) {
                        broadcastNotification(line, this);
                    }
                }
            } catch (IOException e) {
                // Client disconnected
            } finally {
                closeConnection();
            }
        }

        public boolean sendMessage(String msg) {
            if (writer != null) {
                writer.println(msg);
                return !writer.checkError();
            }
            return false;
        }

        private void closeConnection() {
            connectedClients.remove(this);
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                // Ignore exception on close
            }
            System.out.println("🔹 [SocketServer] Client disconnected.");
        }
    }
}
