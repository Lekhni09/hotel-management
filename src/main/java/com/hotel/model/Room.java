package com.hotel.model;

/**
 * Room — plain Java model (POJO).
 *
 * SOLID: S — only holds room data, no DB or UI logic.
 */
public class Room {

    public enum Type { SINGLE, DOUBLE, SUITE }

    private int    id;
    private String roomNumber;
    private Type   type;
    private double pricePerNight;
    private boolean available;

    public Room() {}

    public Room(int id, String roomNumber, Type type, double pricePerNight, boolean available) {
        this.id            = id;
        this.roomNumber    = roomNumber;
        this.type          = type;
        this.pricePerNight = pricePerNight;
        this.available     = available;
    }

    // Getters and setters
    public int     getId()            { return id; }
    public void    setId(int id)      { this.id = id; }

    public String  getRoomNumber()               { return roomNumber; }
    public void    setRoomNumber(String n)       { this.roomNumber = n; }

    public Type    getType()                     { return type; }
    public void    setType(Type type)            { this.type = type; }

    public double  getPricePerNight()            { return pricePerNight; }
    public void    setPricePerNight(double p)    { this.pricePerNight = p; }

    public boolean isAvailable()                 { return available; }
    public void    setAvailable(boolean a)       { this.available = a; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " [" + type + "] ₹" + pricePerNight + "/night";
    }
}