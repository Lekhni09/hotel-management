package com.hotel.dao.impl;

import com.hotel.config.DatabaseConfig;
import com.hotel.dao.BookingDAO;
import com.hotel.model.Booking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BookingDAOImpl — Concrete implementation of BookingDAO using JDBC.
 *
 * SOLID: S — only handles booking DB operations.
 */
public class BookingDAOImpl implements BookingDAO {

    // -------------------------------------------------------
    // ADD booking — returns generated ID
    // -------------------------------------------------------
    @Override
    public int addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (room_id, customer_id, check_in, check_out, total_amount, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, booking.getRoomId());
            stmt.setInt(2, booking.getCustomerId());
            stmt.setDate(3, Date.valueOf(booking.getCheckIn()));
            stmt.setDate(4, Date.valueOf(booking.getCheckOut()));
            stmt.setDouble(5, booking.getTotalAmount());
            stmt.setString(6, booking.getStatus().name());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);

        } catch (SQLException e) {
            System.err.println("Error adding booking: " + e.getMessage());
        }
        return -1;
    }

    // -------------------------------------------------------
    // GET all bookings — JOIN with rooms and customers for display
    // -------------------------------------------------------
    @Override
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
                SELECT b.*, c.name AS customer_name, r.room_number, r.type AS room_type
                FROM bookings b
                JOIN customers c ON b.customer_id = c.id
                JOIN rooms r     ON b.room_id = r.id
                ORDER BY b.created_at DESC
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching bookings: " + e.getMessage());
        }
        return bookings;
    }

    // -------------------------------------------------------
    // GET only active bookings
    // -------------------------------------------------------
    @Override
    public List<Booking> getActiveBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
                SELECT b.*, c.name AS customer_name, r.room_number, r.type AS room_type
                FROM bookings b
                JOIN customers c ON b.customer_id = c.id
                JOIN rooms r     ON b.room_id = r.id
                WHERE b.status = 'ACTIVE'
                ORDER BY b.check_in
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching active bookings: " + e.getMessage());
        }
        return bookings;
    }

    // -------------------------------------------------------
    // GET booking by ID
    // -------------------------------------------------------
    @Override
    public Booking getBookingById(int id) {
        String sql = """
                SELECT b.*, c.name AS customer_name, r.room_number, r.type AS room_type
                FROM bookings b
                JOIN customers c ON b.customer_id = c.id
                JOIN rooms r     ON b.room_id = r.id
                WHERE b.id = ?
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("Error fetching booking: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------
    // UPDATE booking status (ACTIVE → COMPLETED or CANCELLED)
    // -------------------------------------------------------
    @Override
    public void updateBookingStatus(int bookingId, Booking.Status status) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, bookingId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating booking status: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // GET bookings by customer ID
    // -------------------------------------------------------
    @Override
    public List<Booking> getBookingsByCustomerId(int customerId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
                SELECT b.*, c.name AS customer_name, r.room_number, r.type AS room_type
                FROM bookings b
                JOIN customers c ON b.customer_id = c.id
                JOIN rooms r     ON b.room_id = r.id
                WHERE b.customer_id = ?
                ORDER BY b.created_at DESC
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching customer bookings: " + e.getMessage());
        }
        return bookings;
    }

    // -------------------------------------------------------
    // GET total revenue from completed bookings
    // -------------------------------------------------------
    @Override
    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM bookings WHERE status = 'COMPLETED'";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) return rs.getDouble(1);

        } catch (SQLException e) {
            System.err.println("Error fetching revenue: " + e.getMessage());
        }
        return 0;
    }

    // -------------------------------------------------------
    // GET total bookings count
    // -------------------------------------------------------
    @Override
    public int getTotalBookingsCount() {
        String sql = "SELECT COUNT(*) FROM bookings";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("Error fetching bookings count: " + e.getMessage());
        }
        return 0;
    }

    // -------------------------------------------------------
    // Helper — maps a ResultSet row to a Booking object
    // -------------------------------------------------------
    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        booking.setRoomId(rs.getInt("room_id"));
        booking.setCustomerId(rs.getInt("customer_id"));
        booking.setCheckIn(rs.getDate("check_in").toLocalDate());
        booking.setCheckOut(rs.getDate("check_out").toLocalDate());
        booking.setTotalAmount(rs.getDouble("total_amount"));
        booking.setStatus(Booking.Status.valueOf(rs.getString("status")));
        booking.setCustomerName(rs.getString("customer_name"));
        booking.setRoomNumber(rs.getString("room_number"));
        booking.setRoomType(rs.getString("room_type"));
        return booking;
    }
}