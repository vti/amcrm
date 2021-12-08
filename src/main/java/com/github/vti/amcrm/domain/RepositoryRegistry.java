package com.github.vti.amcrm.domain;

import com.github.vti.amcrm.domain.customer.CustomerRepository;
import com.github.vti.amcrm.domain.session.SessionRepository;
import com.github.vti.amcrm.domain.user.UserRepository;

public interface RepositoryRegistry {
    CustomerRepository getCustomerRepository();

    UserRepository getUserRepository();

    SessionRepository getSessionRepository();
}
