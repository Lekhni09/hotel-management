package com.hotel.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConfig — Single Responsibility: manages DB connection only.
 *
 * SOLID: S — this class does one thing: provide a DB connection.
 *        D — other classes depend on this abstraction, not on DriverManager directly.
 */
public class DatabaseConfig {

    private static final String URL = "jdbc:mysql://localhost:3306/hotel_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    // Private constructor — no one should instantiate this utility class
    private DatabaseConfig() {}

    /**
     * Returns a new JDBC connection.
     * Callers must close the connection after use (use try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}