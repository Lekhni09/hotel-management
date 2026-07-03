package com.hotel.config;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn != null) {
                System.out.println("✅ Connected to MySQL successfully!");
            }
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        }
    }
}