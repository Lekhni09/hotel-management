package com.hotel.dao;

import com.hotel.model.Room;
import java.util.List;

/**
 * RoomDAO — Interface for room database operations.
 *
 * SOLID: D — higher layers depend on this interface, not the concrete class.
 *        I — only room-specific operations, nothing else.
 */
public interface RoomDAO {
    void         addRoom(Room room);
    List<Room>   getAllRooms();
    List<Room>   getAvailableRooms();
    Room         getRoomById(int id);
    void         updateRoomAvailability(int roomId, boolean isAvailable);
    void         deleteRoom(int id);
}