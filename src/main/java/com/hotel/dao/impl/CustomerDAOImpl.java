package com.hotel.dao.impl;

import com.hotel.config.DatabaseConfig;
import com.hotel.dao.CustomerDAO;
import com.hotel.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CustomerDAOImpl — Concrete implementation of CustomerDAO using JDBC.
 *
 * SOLID: S — only handles customer DB operations.
 */
public class CustomerDAOImpl implements CustomerDAO {

    // -------------------------------------------------------
    // ADD customer — returns the auto-generated ID
    // -------------------------------------------------------
    @Override
    public int addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (name, phone, email, id_proof) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getIdProof());
            stmt.executeUpdate();

            // Get the auto-generated ID back
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);

        } catch (SQLException e) {
            System.err.println("Error adding customer: " + e.getMessage());
        }
        return -1;
    }

    // -------------------------------------------------------
    // GET all customers
    // -------------------------------------------------------
    @Override
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY name";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching customers: " + e.getMessage());
        }
        return customers;
    }

    // -------------------------------------------------------
    // GET customer by ID
    // -------------------------------------------------------
    @Override
    public Customer getCustomerById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("Error fetching customer: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------
    // GET customer by phone (useful for quick lookup at check-in)
    // -------------------------------------------------------
    @Override
    public Customer getCustomerByPhone(String phone) {
        String sql = "SELECT * FROM customers WHERE phone = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("Error fetching customer by phone: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------
    // UPDATE customer
    // -------------------------------------------------------
    @Override
    public void updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET name = ?, phone = ?, email = ?, id_proof = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getIdProof());
            stmt.setInt(5, customer.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // DELETE customer
    // -------------------------------------------------------
    @Override
    public void deleteCustomer(int id) {
        String sql = "DELETE FROM customers WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Helper — maps a ResultSet row to a Customer object
    // -------------------------------------------------------
    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("id_proof")
        );
    }
}