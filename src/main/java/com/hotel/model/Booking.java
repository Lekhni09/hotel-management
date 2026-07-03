package com.hotel.model;

import java.time.LocalDate;

/**
 * Booking — plain Java model (POJO).
 *
 * SOLID: S — only holds booking data.
 */
public class Booking {

    public enum Status { ACTIVE, COMPLETED, CANCELLED }

    private int       id;
    private int       roomId;
    private int       customerId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double    totalAmount;
    private Status    status;

    // Joined fields — populated by DAO queries for display purposes
    private String    customerName;
    private String    roomNumber;
    private String    roomType;

    public Booking() {}

    public Booking(int roomId, int customerId, LocalDate checkIn, LocalDate checkOut, double totalAmount) {
        this.roomId      = roomId;
        this.customerId  = customerId;
        this.checkIn     = checkIn;
        this.checkOut    = checkOut;
        this.totalAmount = totalAmount;
        this.status      = Status.ACTIVE;
    }

    public int       getId()                     { return id; }
    public void      setId(int id)               { this.id = id; }

    public int       getRoomId()                 { return roomId; }
    public void      setRoomId(int r)            { this.roomId = r; }

    public int       getCustomerId()             { return customerId; }
    public void      setCustomerId(int c)        { this.customerId = c; }

    public LocalDate getCheckIn()                { return checkIn; }
    public void      setCheckIn(LocalDate d)     { this.checkIn = d; }

    public LocalDate getCheckOut()               { return checkOut; }
    public void      setCheckOut(LocalDate d)    { this.checkOut = d; }

    public double    getTotalAmount()            { return totalAmount; }
    public void      setTotalAmount(double a)    { this.totalAmount = a; }

    public Status    getStatus()                 { return status; }
    public void      setStatus(Status s)         { this.status = s; }

    public String    getCustomerName()           { return customerName; }
    public void      setCustomerName(String n)   { this.customerName = n; }

    public String    getRoomNumber()             { return roomNumber; }
    public void      setRoomNumber(String n)     { this.roomNumber = n; }

    public String    getRoomType()               { return roomType; }
    public void      setRoomType(String t)       { this.roomType = t; }
}