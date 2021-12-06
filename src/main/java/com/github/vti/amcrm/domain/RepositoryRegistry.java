package com.github.vti.amcrm.domain;

import com.github.vti.amcrm.domain.customer.CustomerRepository;
import com.github.vti.amcrm.domain.user.UserRepository;

public interface RepositoryRegistry {
    public CustomerRepository getCustomerRepository();

    public UserRepository getUserRepository();
}
