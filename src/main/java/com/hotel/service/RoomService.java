package com.hotel.service;

import com.hotel.dao.RoomDAO;
import com.hotel.dao.impl.RoomDAOImpl;
import com.hotel.model.Room;

import java.util.List;

/**
 * RoomService — Business logic for room operations.
 *
 * SOLID: S — only handles room business rules.
 *        D — depends on RoomDAO interface, not the concrete class.
 */
public class RoomService {

    // Depends on interface, not implementation (Dependency Inversion)
    private final RoomDAO roomDAO;

    public RoomService() {
        this.roomDAO = new RoomDAOImpl();
    }

    // -------------------------------------------------------
    // Add a new room (with validation)
    // -------------------------------------------------------
    public boolean addRoom(String roomNumber, String type, double pricePerNight) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            System.err.println("Room number cannot be empty.");
            return false;
        }
        if (pricePerNight <= 0) {
            System.err.println("Price must be greater than zero.");
            return false;
        }

        Room room = new Room();
        room.setRoomNumber(roomNumber.trim());
        room.setType(Room.Type.valueOf(type.toUpperCase()));
        room.setPricePerNight(pricePerNight);
        room.setAvailable(true);

        roomDAO.addRoom(room);
        return true;
    }

    // -------------------------------------------------------
    // Get all rooms
    // -------------------------------------------------------
    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    // -------------------------------------------------------
    // Get only available rooms
    // -------------------------------------------------------
    public List<Room> getAvailableRooms() {
        return roomDAO.getAvailableRooms();
    }

    // -------------------------------------------------------
    // Get room by ID
    // -------------------------------------------------------
    public Room getRoomById(int id) {
        return roomDAO.getRoomById(id);
    }

    // -------------------------------------------------------
    // Mark room as unavailable (called on check-in)
    // -------------------------------------------------------
    public void markRoomAsBooked(int roomId) {
        roomDAO.updateRoomAvailability(roomId, false);
    }

    // -------------------------------------------------------
    // Mark room as available again (called on check-out)
    // -------------------------------------------------------
    public void markRoomAsAvailable(int roomId) {
        roomDAO.updateRoomAvailability(roomId, true);
    }

    // -------------------------------------------------------
    // Delete room
    // -------------------------------------------------------
    public void deleteRoom(int id) {
        roomDAO.deleteRoom(id);
    }

    // -------------------------------------------------------
    // Count available rooms (for dashboard)
    // -------------------------------------------------------
    public long getAvailableRoomCount() {
        return roomDAO.getAllRooms().stream()
                .filter(Room::isAvailable)
                .count();
    }

    // -------------------------------------------------------
    // Count occupied rooms (for dashboard)
    // -------------------------------------------------------
    public long getOccupiedRoomCount() {
        return roomDAO.getAllRooms().stream()
                .filter(r -> !r.isAvailable())
                .count();
    }
}