package com.github.vti.amcrm.domain;

import com.github.vti.amcrm.domain.customer.CustomerRepository;

public interface RepositoryRegistry {
    public CustomerRepository getCustomerRepository();
}
