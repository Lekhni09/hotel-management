package com.hotel.dao;

import com.hotel.model.Customer;
import java.util.List;

/**
 * CustomerDAO — Interface for customer database operations.
 *
 * SOLID: D — depend on abstraction, not implementation.
 *        I — only customer-specific operations.
 */
public interface CustomerDAO {
    int          addCustomer(Customer customer);   // returns generated ID
    List<Customer> getAllCustomers();
    Customer     getCustomerById(int id);
    Customer     getCustomerByPhone(String phone);
    void         updateCustomer(Customer customer);
    void         deleteCustomer(int id);
}