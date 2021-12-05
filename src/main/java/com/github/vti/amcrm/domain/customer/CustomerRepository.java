package com.github.vti.amcrm.domain.customer;

import java.util.Optional;

import com.github.vti.amcrm.domain.customer.exception.CustomerExistsException;

public interface CustomerRepository {
    Optional<Customer> load(CustomerId id);

    void store(Customer customer) throws CustomerExistsException;
}
