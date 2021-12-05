package com.github.vti.amcrm.infra.customer;

import java.util.Map;
import java.util.Optional;

import com.github.vti.amcrm.domain.customer.Customer;
import com.github.vti.amcrm.domain.customer.CustomerId;
import com.github.vti.amcrm.domain.customer.CustomerRepository;
import com.github.vti.amcrm.domain.customer.exception.CustomerExistsException;

public class MemoryCustomerRepository implements CustomerRepository {
    private final Map<CustomerId, Customer> storage;

    public MemoryCustomerRepository(Map<CustomerId, Customer> storage) {
        this.storage = storage;
    }

    @Override
    public Optional<Customer> load(CustomerId id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void store(Customer customer) throws CustomerExistsException {
        if (storage.containsKey(customer.getId())) {
            Customer existingCustomer = storage.get(customer.getId());

            if (existingCustomer.getVersion() != customer.getVersion()) {
                throw new CustomerExistsException();
            }
        }

        storage.put(customer.getId(), customer);

        customer.incrementVersion();
    }
}
