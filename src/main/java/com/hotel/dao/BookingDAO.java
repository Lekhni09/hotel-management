package com.hotel.dao;

import com.hotel.model.Booking;
import java.util.List;

/**
 * BookingDAO — Interface for booking database operations.
 *
 * SOLID: D — depend on abstraction, not implementation.
 *        I — only booking-specific operations.
 */
public interface BookingDAO {
    int            addBooking(Booking booking);     // returns generated ID
    List<Booking>  getAllBookings();
    List<Booking>  getActiveBookings();
    Booking        getBookingById(int id);
    void           updateBookingStatus(int bookingId, Booking.Status status);
    List<Booking>  getBookingsByCustomerId(int customerId);
    double         getTotalRevenue();
    int            getTotalBookingsCount();
}