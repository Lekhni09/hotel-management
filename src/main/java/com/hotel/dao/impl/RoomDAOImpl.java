package com.hotel.dao.impl;

import com.hotel.config.DatabaseConfig;
import com.hotel.dao.RoomDAO;
import com.hotel.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RoomDAOImpl — Concrete implementation of RoomDAO using JDBC.
 *
 * SOLID: S — only handles room DB operations.
 *        O — new behaviour can be added without modifying this class.
 *        L — can be swapped with any other RoomDAO implementation.
 */
public class RoomDAOImpl implements RoomDAO {

    // -------------------------------------------------------
    // ADD a new room
    // -------------------------------------------------------
    @Override
    public void addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, type, price_per_night, is_available) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getType().name());
            stmt.setDouble(3, room.getPricePerNight());
            stmt.setBoolean(4, room.isAvailable());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error adding room: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // GET all rooms
    // -------------------------------------------------------
    @Override
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rooms.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching rooms: " + e.getMessage());
        }
        return rooms;
    }

    // -------------------------------------------------------
    // GET only available rooms
    // -------------------------------------------------------
    @Override
    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE is_available = TRUE ORDER BY type";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rooms.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching available rooms: " + e.getMessage());
        }
        return rooms;
    }

    // -------------------------------------------------------
    // GET room by ID
    // -------------------------------------------------------
    @Override
    public Room getRoomById(int id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("Error fetching room: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------
    // UPDATE room availability (called on check-in / check-out)
    // -------------------------------------------------------
    @Override
    public void updateRoomAvailability(int roomId, boolean isAvailable) {
        String sql = "UPDATE rooms SET is_available = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, isAvailable);
            stmt.setInt(2, roomId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating room availability: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // DELETE room
    // -------------------------------------------------------
    @Override
    public void deleteRoom(int id) {
        String sql = "DELETE FROM rooms WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting room: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Helper — maps a ResultSet row to a Room object
    // -------------------------------------------------------
    private Room mapRow(ResultSet rs) throws SQLException {
        return new Room(
                rs.getInt("id"),
                rs.getString("room_number"),
                Room.Type.valueOf(rs.getString("type")),
                rs.getDouble("price_per_night"),
                rs.getBoolean("is_available")
        );
    }
}