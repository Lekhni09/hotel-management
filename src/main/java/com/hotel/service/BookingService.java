package com.hotel.service;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.CustomerDAO;
import com.hotel.dao.impl.BookingDAOImpl;
import com.hotel.dao.impl.CustomerDAOImpl;
import com.hotel.model.Booking;
import com.hotel.model.Customer;
import com.hotel.model.Room;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * BookingService — Business logic for booking operations.
 *
 * SOLID: S — handles booking rules only.
 *        D — depends on DAO interfaces, not implementations.
 */
public class BookingService {

    private final BookingDAO  bookingDAO;
    private final CustomerDAO customerDAO;
    private final RoomService roomService;

    public BookingService() {
        this.bookingDAO  = new BookingDAOImpl();
        this.customerDAO = new CustomerDAOImpl();
        this.roomService = new RoomService();
    }

    // -------------------------------------------------------
    // Create a new booking — full business logic here
    // -------------------------------------------------------
    public String createBooking(String customerName, String customerPhone,
                                String customerEmail, String idProof,
                                int roomId, LocalDate checkIn, LocalDate checkOut) {

        // 1. Validate dates
        if (checkIn == null || checkOut == null) {
            return "ERROR: Dates cannot be empty.";
        }
        if (!checkOut.isAfter(checkIn)) {
            return "ERROR: Check-out date must be after check-in date.";
        }
        if (checkIn.isBefore(LocalDate.now())) {
            return "ERROR: Check-in date cannot be in the past.";
        }

        // 2. Validate room is available
        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            return "ERROR: Room not found.";
        }
        if (!room.isAvailable()) {
            return "ERROR: Room " + room.getRoomNumber() + " is already booked.";
        }

        // 3. Calculate total amount
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalAmount = nights * room.getPricePerNight();

        // 4. Add customer (or reuse existing by phone)
        Customer existingCustomer = customerDAO.getCustomerByPhone(customerPhone);
        int customerId;
        if (existingCustomer != null) {
            customerId = existingCustomer.getId();
        } else {
            Customer newCustomer = new Customer();
            newCustomer.setName(customerName);
            newCustomer.setPhone(customerPhone);
            newCustomer.setEmail(customerEmail);
            newCustomer.setIdProof(idProof);
            customerId = customerDAO.addCustomer(newCustomer);
        }

        if (customerId == -1) {
            return "ERROR: Failed to save customer.";
        }

        // 5. Create booking
        Booking booking = new Booking(roomId, customerId, checkIn, checkOut, totalAmount);
        int bookingId = bookingDAO.addBooking(booking);

        if (bookingId == -1) {
            return "ERROR: Failed to create booking.";
        }

        // 6. Mark room as unavailable
        roomService.markRoomAsBooked(roomId);

        return "SUCCESS: Booking #" + bookingId + " created! Total: ₹" + totalAmount;
    }

    // -------------------------------------------------------
    // Check-out — marks booking complete, frees the room
    // -------------------------------------------------------
    public String checkOut(int bookingId) {
        Booking booking = bookingDAO.getBookingById(bookingId);

        if (booking == null) {
            return "ERROR: Booking not found.";
        }
        if (booking.getStatus() != Booking.Status.ACTIVE) {
            return "ERROR: Booking is not active.";
        }

        // Mark booking as completed
        bookingDAO.updateBookingStatus(bookingId, Booking.Status.COMPLETED);

        // Free the room
        roomService.markRoomAsAvailable(booking.getRoomId());

        return "SUCCESS: Checked out successfully. Room " + booking.getRoomNumber() + " is now available.";
    }

    // -------------------------------------------------------
    // Cancel a booking
    // -------------------------------------------------------
    public String cancelBooking(int bookingId) {
        Booking booking = bookingDAO.getBookingById(bookingId);

        if (booking == null) {
            return "ERROR: Booking not found.";
        }
        if (booking.getStatus() != Booking.Status.ACTIVE) {
            return "ERROR: Only active bookings can be cancelled.";
        }

        bookingDAO.updateBookingStatus(bookingId, Booking.Status.CANCELLED);
        roomService.markRoomAsAvailable(booking.getRoomId());

        return "SUCCESS: Booking #" + bookingId + " cancelled.";
    }

    // -------------------------------------------------------
    // Get all bookings
    // -------------------------------------------------------
    public List<Booking> getAllBookings() {
        return bookingDAO.getAllBookings();
    }

    // -------------------------------------------------------
    // Get active bookings only
    // -------------------------------------------------------
    public List<Booking> getActiveBookings() {
        return bookingDAO.getActiveBookings();
    }

    // -------------------------------------------------------
    // Get booking by ID (for billing)
    // -------------------------------------------------------
    public Booking getBookingById(int id) {
        return bookingDAO.getBookingById(id);
    }

    // -------------------------------------------------------
    // Get bookings by customer ID
    // -------------------------------------------------------
    public List<Booking> getBookingsByCustomer(int customerId) {
        return bookingDAO.getBookingsByCustomerId(customerId);
    }

    // -------------------------------------------------------
    // Dashboard stats
    // -------------------------------------------------------
    public double getTotalRevenue() {
        return bookingDAO.getTotalRevenue();
    }

    public int getTotalBookingsCount() {
        return bookingDAO.getTotalBookingsCount();
    }

    // -------------------------------------------------------
    // Calculate total amount for given room and dates (for UI preview)
    // -------------------------------------------------------
    public double calculateTotalAmount(int roomId, LocalDate checkIn, LocalDate checkOut) {
        Room room = roomService.getRoomById(roomId);
        if (room == null || checkIn == null || checkOut == null) return 0;
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        return nights * room.getPricePerNight();
    }
}