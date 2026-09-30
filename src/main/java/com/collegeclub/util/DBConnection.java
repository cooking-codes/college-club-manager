package com.collegeclub.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Utility class for managing MySQL JDBC Database Connections.
 * Uses pure JDBC with standard DriverManager.
 * Credentials can be configured via db.properties or environment variables.
 */
public class DBConnection {

    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        // Load default connection values
        Properties props = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] Note: db.properties not found, falling back to defaults/env.");
        }

        // Check environment variables first, then properties file, then localhost defaults
        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USER");
        String envPass = System.getenv("DB_PASSWORD");

        dbUrl = (envUrl != null && !envUrl.trim().isEmpty()) ? envUrl :
                props.getProperty("db.url", "jdbc:mysql://localhost:3306/college_club_manager?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8");

        dbUser = (envUser != null && !envUser.trim().isEmpty()) ? envUser :
                props.getProperty("db.user", "root");

        dbPassword = (envPass != null) ? envPass :
                props.getProperty("db.password", "");

        // Load the MySQL JDBC Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[DBConnection] MySQL JDBC Driver registered successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] CRITICAL: MySQL JDBC Driver not found in classpath!");
            e.printStackTrace();
        }
    }

    /**
     * Obtains a new database connection.
     * @return active java.sql.Connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    /**
     * Safely closes Connection, Statement, and ResultSet resources.
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {}
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {}
        }
    }

    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }

    public static void close(Statement stmt) {
        close(null, stmt, null);
    }
}
