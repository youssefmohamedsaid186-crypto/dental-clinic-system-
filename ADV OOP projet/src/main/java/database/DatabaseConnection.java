package database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dental_clinic_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "youssef2005#"; // MySQL Workbench password

    private static Connection instance = null;

    private DatabaseConnection() {
        // Private constructor for Singleton pattern
    }

    public static Connection getConnection() {
        try {
            if (instance == null || instance.isClosed()) {
                synchronized (DatabaseConnection.class) {
                    if (instance == null || instance.isClosed()) {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        try {
                            instance = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                        } catch (SQLException e) {
                            // Error 1049: Unknown database 'dental_clinic_db'
                            if (e.getErrorCode() == 1049) {
                                System.out.println("⚠️ Database 'dental_clinic_db' not found. Auto-initializing database schema...");
                                initializeDatabaseSchema();
                                instance = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                            } else {
                                throw e;
                            }
                        }
                        System.out.println("✅ [DatabaseConnection] Successfully connected to MySQL database: dental_clinic_db");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ [DatabaseConnection] MySQL JDBC Driver not found! Ensure mysql-connector-j is included in pom.xml.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ [DatabaseConnection] Database connection failed! Check if MySQL server is running on localhost:3306.");
            System.err.println("SQL State: " + e.getSQLState() + " | Error Code: " + e.getErrorCode() + " | Message: " + e.getMessage());
        }
        return instance;
    }

    private static void initializeDatabaseSchema() {
        try (Connection conn = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS dental_clinic_db;");
            stmt.executeUpdate("USE dental_clinic_db;");

            File schemaFile = new File("schema.sql");
            if (schemaFile.exists()) {
                StringBuilder sqlScript = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new FileReader(schemaFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        // Ignore SQL comments
                        if (!line.trim().startsWith("--") && !line.trim().isEmpty()) {
                            sqlScript.append(line).append("\n");
                        }
                    }
                }

                String[] statements = sqlScript.toString().split(";");
                for (String sql : statements) {
                    if (!sql.trim().isEmpty()) {
                        stmt.execute(sql.trim());
                    }
                }
                System.out.println("✅ [DatabaseConnection] schema.sql executed successfully!");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed to auto-initialize schema.sql: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        if (instance != null) {
            try {
                if (!instance.isClosed()) {
                    instance.close();
                    System.out.println("ℹ️ [DatabaseConnection] Database connection closed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                instance = null;
            }
        }
    }
}
