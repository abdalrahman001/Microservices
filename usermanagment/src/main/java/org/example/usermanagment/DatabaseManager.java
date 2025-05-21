package org.example.usermanagment;
import jakarta.inject.Singleton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/user_management?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "G4@67&*mQnY!";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found.", e);
        }
    }

    // ==== Initialization (optional if already created manually) ====
    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            String createCompanies = """
                CREATE TABLE IF NOT EXISTS companies (
                    id CHAR(36) PRIMARY KEY,
                    name VARCHAR(100) UNIQUE NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )""";

            String createAdmins = """
                CREATE TABLE IF NOT EXISTS admins (
                    id CHAR(36) PRIMARY KEY,
                    username VARCHAR(100) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL
                )""";

            String createSellers = """
                CREATE TABLE IF NOT EXISTS sellers (
                    id CHAR(36) PRIMARY KEY,
                    company_id CHAR(36) NOT NULL,
                    username VARCHAR(100) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (company_id) REFERENCES companies(id)
                )""";

            String createCustomers = """
                CREATE TABLE IF NOT EXISTS customers (
                    id CHAR(36) PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    phone VARCHAR(20),
                    location VARCHAR(100),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )""";

            stmt.execute(createCompanies);
            stmt.execute(createAdmins);
            stmt.execute(createSellers);
            stmt.execute(createCustomers);

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Database init error: " + e.getMessage());
        }
    }


    // ==== Company Management ====

    public static boolean addCompany(String name) {
        String sql = "INSERT INTO companies (id, name) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, name);
            stmt.executeUpdate();
            System.out.println("Company added: " + name);
            return true;

        } catch (SQLException e) {
            System.err.println("Add company error: " + e.getMessage());
            return false;
        }
    }

    public static boolean removeCompany(String companyId) {
        String sql = "DELETE FROM companies WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, companyId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Company removed: " + companyId);
                return true;
            } else {
                System.err.println("Company not found: " + companyId);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Remove company error: " + e.getMessage());
            return false;
        }
    }
    public static boolean updateCompany(String companyId, String newName) {
        String sql = "UPDATE companies SET name = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newName);
            stmt.setString(2, companyId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Company updated: " + companyId);
                return true;
            } else {
                System.err.println("Company not found: " + companyId);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Update company error: " + e.getMessage());
            return false;
        }
    }

    // ==== Admin Registration/Login ====
    public static boolean registerAdmin(String username, String password) {
        return registerUser("admins", username, null, password);
    }

    public static boolean loginAdmin(String username, String password) {
        return loginUser("admins", "username", username, password);
    }

    // ==== Seller Registration/Login ====
    public static boolean registerSeller(String companyId, String username, String password) {
        return registerUser("sellers", username, companyId, password);
    }

    public static boolean loginSeller(String username, String password) {
        return loginUser("sellers", "username", username, password);
    }

    public static List<String> listSellerAccounts() {
            List<String> sellers = new ArrayList<>();
            String sql = """
                SELECT s.id AS seller_id, s.username, s.created_at,
                    c.id AS company_id, c.name AS company_name
                FROM sellers s
                JOIN companies c ON s.company_id = c.id
            """;

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String sellerInfo = String.format(
                        "Seller ID: %s, Username: %s, Company ID: %s, Company Name: %s, Created At: %s",
                        rs.getString("seller_id"),
                        rs.getString("username"),
                        rs.getString("company_id"),
                        rs.getString("company_name"),
                        rs.getTimestamp("created_at")
                    );
                    sellers.add(sellerInfo);
                }

            } catch (SQLException e) {
                System.err.println("List seller accounts error: " + e.getMessage());
            }

            return sellers;
        
    }


    // ==== Customer Registration/Login ====
    public static boolean registerCustomer(String name, String email, String password, String phone, String location) {
        String sql = "INSERT INTO customers (id, name, email, password, phone, location) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.setString(4, hashPassword(password));
            stmt.setString(5, phone);
            stmt.setString(6, location);

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Customer registration error: " + e.getMessage());
            return false;
        }
    }

    public static boolean loginCustomer(String email, String password) {
        return loginUser("customers", "email", email, password);
    }

    public static List<String> listCustomers() {
        List<String> customers = new ArrayList<>();
        String sql = "SELECT id, name, email, phone, location, created_at FROM customers";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String customerInfo = String.format(
                    "ID: %s, Name: %s, Email: %s, Phone: %s, Location: %s, Created At: %s",
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("location"),
                    rs.getTimestamp("created_at")
                );
                customers.add(customerInfo);
            }

            } catch (SQLException e) {
                System.err.println("List customers error: " + e.getMessage());
            }

            return customers;
        
    }


    // ==== Shared Methods ====
    private static boolean registerUser(String table, String username, String companyId, String password) {
        String sql = switch (table) {
            case "admins" -> "INSERT INTO admins (id, username, password) VALUES (?, ?, ?)";
            case "sellers" -> "INSERT INTO sellers (id, company_id, username, password) VALUES (?, ?, ?, ?)";
            default -> throw new IllegalArgumentException("Invalid table");
        };

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, UUID.randomUUID().toString());
            if ("sellers".equals(table)) {
                stmt.setString(2, companyId);
                stmt.setString(3, username);
                stmt.setString(4, hashPassword(password));
            } else {
                stmt.setString(2, username);
                stmt.setString(3, hashPassword(password));
            }

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Registration error for " + table + ": " + e.getMessage());
            return false;
        }
    }

    private static boolean loginUser(String table, String userField, String userValue, String password) {
        String sql = "SELECT password FROM " + table + " WHERE " + userField + " = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userValue);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    return storedHash.equals(hashPassword(password));
                }
            }
        } catch (SQLException e) {
            System.err.println("Login error for " + table + ": " + e.getMessage());
        }
        return false;
    }

    public static boolean addAdminNotification(String orderId, String reason) {
    String sql = "INSERT INTO admin_notifications (order_id, reason) VALUES (?, ?)";

    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, orderId);
        stmt.setString(2, reason);
        stmt.executeUpdate();
        return true;

    } catch (SQLException e) {
        System.err.println("Failed to add admin notification: " + e.getMessage());
        return false;
    }
}

public static List<String> getAdminNotifications() {
    List<String> notifications = new ArrayList<>();
    String sql = "SELECT id, order_id, reason, created_at FROM admin_notifications ORDER BY created_at DESC";

    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            String info = String.format(
                "Notification #%d: Order ID %d, Reason: %s, Time: %s",
                rs.getInt("id"),
                rs.getString("order_id"),
                rs.getString("reason"),
                rs.getTimestamp("created_at")
            );
            notifications.add(info);
        }

    } catch (SQLException e) {
        System.err.println("Failed to get admin notifications: " + e.getMessage());
    }

    return notifications;
}

public static boolean addCustomerNotification(String customerId, String notification) {
        String query = "INSERT INTO customer_notifications (customer_id, notification) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customerId);
            stmt.setString(2, notification);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getCustomerNotifications(String customerId) {
        List<String> notifications = new ArrayList<>();
        String query = "SELECT notification FROM customer_notifications WHERE customer_id = ? ORDER BY created_at DESC";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                notifications.add(rs.getString("notification"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    public static List<String> getAllCustomerNotifications() {
        List<String> notifications = new ArrayList<>();
        String query = "SELECT customer_id, notification FROM customer_notifications ORDER BY created_at DESC";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String entry = "Customer " + rs.getString("customer_id") + ": " + rs.getString("notification");
                notifications.add(entry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    // -------------------- System Logs --------------------

    public static boolean addSystemLog(String severity, String serviceName, String message) {
        String query = "INSERT INTO system_logs (severity, service_name, message) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, severity);
            stmt.setString(2, serviceName);
            stmt.setString(3, message);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getSystemLogs() {
        List<String> logs = new ArrayList<>();
        String query = "SELECT severity, service_name, message, logged_at FROM system_logs ORDER BY logged_at DESC";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String logEntry = "[" + rs.getTimestamp("logged_at") + "] " +
                        rs.getString("severity") + " - " +
                        rs.getString("service_name") + ": " +
                        rs.getString("message");
                logs.add(logEntry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }


    // ==== Password Hashing ====
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing error", e);
        }
    }

    public static void main(String[] args) {
        initDatabase();
        registerAdmin("adminn", "admin123");
        addCompany("Tech Corpp");
        //registerSeller("e64ebcb2-74c1-49ad-a3cc-cbc22c59fa21", "seller", "seller123");
    }

    @Singleton
    public static class DatabaseSingletonBean {
        public void init() {
            System.out.println("Database Singleton initialized");
        }
    }
}
