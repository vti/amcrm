package com.github.vti.amcrm.infra;

import java.util.HashMap;
import java.util.Map;

import com.github.vti.amcrm.domain.customer.Customer;
import com.github.vti.amcrm.domain.customer.CustomerId;

public class MemoryStorage {

    Map<CustomerId, Customer> customerStorage = new HashMap<>();

    public Map<CustomerId, Customer> getCustomerStorage() {
        return customerStorage;
    }
}
