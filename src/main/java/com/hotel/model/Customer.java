package com.hotel.model;

/**
 * Customer — plain Java model (POJO).
 *
 * SOLID: S — only holds customer data.
 */
public class Customer {

    private int    id;
    private String name;
    private String phone;
    private String email;
    private String idProof;

    public Customer() {}

    public Customer(int id, String name, String phone, String email, String idProof) {
        this.id      = id;
        this.name    = name;
        this.phone   = phone;
        this.email   = email;
        this.idProof = idProof;
    }

    public int    getId()              { return id; }
    public void   setId(int id)        { this.id = id; }

    public String getName()            { return name; }
    public void   setName(String n)    { this.name = n; }

    public String getPhone()           { return phone; }
    public void   setPhone(String p)   { this.phone = p; }

    public String getEmail()           { return email; }
    public void   setEmail(String e)   { this.email = e; }

    public String getIdProof()         { return idProof; }
    public void   setIdProof(String i) { this.idProof = i; }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}