package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * SocketClient manages client-side socket connection to SocketServer.
 * Uses background threads for listening and sending messages safely without blocking JavaFX UI thread.
 */
public class SocketClient {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5000;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Thread listenerThread;
    private boolean isConnected = false;

    private Consumer<NotificationMessage> onNotificationListener;

    public SocketClient() {
    }

    public void setOnNotificationListener(Consumer<NotificationMessage> listener) {
        this.onNotificationListener = listener;
    }

    /**
     * Asynchronously connects to the Socket Server on a background thread.
     */
    public void connect(String host, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(host != null ? host : DEFAULT_HOST, port > 0 ? port : DEFAULT_PORT);
                writer = new PrintWriter(socket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                isConnected = true;
                System.out.println("🟢 [SocketClient] Connected to SocketServer on port " + port);

                // Start background listener loop
                startListening();
            } catch (IOException e) {
                System.out.println("ℹ️ [SocketClient] Could not connect to SocketServer. Real-time notifications will be offline.");
                isConnected = false;
            }
        }).start();
    }

    public void connect() {
        connect(DEFAULT_HOST, DEFAULT_PORT);
    }

    private void startListening() {
        listenerThread = new Thread(() -> {
            try {
                String line;
                while (isConnected && (line = reader.readLine()) != null) {
                    if (line.startsWith("NOTIFICATION|")) {
                        NotificationMessage msg = NotificationMessage.parsePayload(line);
                        if (msg != null && onNotificationListener != null) {
                            onNotificationListener.accept(msg);
                        }
                    }
                }
            } catch (IOException e) {
                // Connection closed or dropped
            } finally {
                disconnect();
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    /**
     * Static helper method to send appointment notification safely.
     * If Socket Server is offline, fails silently without throwing an exception.
     */
    public static void sendAppointmentNotification(int doctorId, String patientName, String date, String time) {
        new Thread(() -> {
            try (Socket tempSocket = new Socket(DEFAULT_HOST, DEFAULT_PORT);
                 PrintWriter tempWriter = new PrintWriter(tempSocket.getOutputStream(), true)) {
                
                NotificationMessage msg = new NotificationMessage(doctorId, patientName, date, time);
                tempWriter.println(msg.toSocketPayload());
                System.out.println("📤 [SocketClient] Sent real-time notification to SocketServer for Doctor ID: " + doctorId);
            } catch (IOException e) {
                // SocketServer is offline - database operation is unaffected
                System.out.println("ℹ️ [SocketClient] SocketServer is offline. Notification bypassed safely.");
            }
        }).start();
    }

    public void disconnect() {
        isConnected = false;
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ℹ️ [SocketClient] Disconnected from SocketServer.");
    }

    public boolean isConnected() {
        return isConnected;
    }
}
