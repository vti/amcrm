package com.github.vti.amcrm.infra;

import java.util.HashMap;
import java.util.Map;

import com.github.vti.amcrm.domain.customer.Customer;
import com.github.vti.amcrm.domain.customer.CustomerId;
import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.domain.user.UserId;

public class MemoryStorage {

    final Map<CustomerId, Customer> customerStorage = new HashMap<>();
    final Map<UserId, User> userStorage = new HashMap<>();

    public Map<CustomerId, Customer> getCustomerStorage() {
        return customerStorage;
    }

    public Map<UserId, User> getUserStorage() {
        return userStorage;
    }
}
