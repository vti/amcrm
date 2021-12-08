package com.github.vti.amcrm.infra.registry;

import com.github.vti.amcrm.domain.RepositoryRegistry;
import com.github.vti.amcrm.domain.customer.CustomerRepository;
import com.github.vti.amcrm.domain.session.SessionRepository;
import com.github.vti.amcrm.domain.user.UserRepository;
import com.github.vti.amcrm.infra.MemoryStorage;
import com.github.vti.amcrm.infra.customer.MemoryCustomerRepository;
import com.github.vti.amcrm.infra.session.MemorySessionRepository;
import com.github.vti.amcrm.infra.user.MemoryUserRepository;

public class MemoryRepositoryRegistry implements RepositoryRegistry {
    private final MemoryStorage storage;

    public MemoryRepositoryRegistry(MemoryStorage storage) {
        this.storage = storage;
    }

    @Override
    public CustomerRepository getCustomerRepository() {
        return new MemoryCustomerRepository(storage.getCustomerStorage());
    }

    @Override
    public UserRepository getUserRepository() {
        return new MemoryUserRepository(storage.getUserStorage());
    }

    @Override
    public SessionRepository getSessionRepository() {
        return new MemorySessionRepository(storage.getSessionStorage());
    }
}
