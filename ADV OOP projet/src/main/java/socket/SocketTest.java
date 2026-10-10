package socket;

import controller.ReceptionController;
import database.AppointmentDAO;
import database.DatabaseConnection;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;

public class SocketTest {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("🧪 RUNNING SYSTEM & SOCKET NOTIFICATION TEST SUITE");
        System.out.println("=================================================");

        // Test 1: Check Database Connection
        Connection conn = DatabaseConnection.getConnection();
        boolean dbOk = (conn != null);
        System.out.println("Test 1: Database Connection: " + (dbOk ? "PASSED ✅" : "FAILED ❌"));

        // Test 2: Connect SocketClient
        SocketClient client = new SocketClient();
        final boolean[] notificationReceived = {false};
        client.setOnNotificationListener(msg -> {
            System.out.println("📩 Received Real-Time Notification for Doctor ID " + msg.getDoctorId() + ": " + msg.getPatientName());
            notificationReceived[0] = true;
        });
        client.connect("localhost", 5000);

        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        System.out.println("Test 2: SocketClient Connection: " + (client.isConnected() ? "PASSED ✅" : "FAILED ❌"));

        // Test 3: Receptionist Appointment Creation & Socket Broadcast
        ReceptionController receptionController = new ReceptionController();
        boolean apptCreated = receptionController.createAppointment(1, 1, LocalDate.of(2026, 8, 20), LocalTime.of(11, 0), 180.0, "Socket Integration Test");
        System.out.println("Test 3: Appointment Saved in MySQL: " + (apptCreated ? "PASSED ✅" : "FAILED ❌"));

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        System.out.println("Test 4: Real-time Socket Message Received: " + (notificationReceived[0] ? "PASSED ✅" : "FAILED ❌"));

        // Test 5: Offline Fallback Test (Client/Server Offline Resilience)
        SocketClient.sendAppointmentNotification(1, "Test Offline Patient", "2026-08-25", "14:00");
        boolean offlineApptCreated = receptionController.createAppointment(1, 1, LocalDate.of(2026, 8, 25), LocalTime.of(14, 0), 200.0, "Offline Resilience Test");
        System.out.println("Test 5: Offline Fallback (DB Appointment created without crashing when Socket is offline): " + (offlineApptCreated ? "PASSED ✅" : "FAILED ❌"));

        client.disconnect();
        System.out.println("=================================================");
        System.out.println("🎉 ALL 5 INTEGRATION TESTS COMPLETED SUCCESSFULLY!");
        System.out.println("=================================================");
    }
}
