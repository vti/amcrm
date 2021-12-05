package com.github.vti.amcrm.infra.registry;

import com.github.vti.amcrm.domain.RepositoryRegistry;
import com.github.vti.amcrm.domain.customer.CustomerRepository;
import com.github.vti.amcrm.infra.MemoryStorage;
import com.github.vti.amcrm.infra.customer.MemoryCustomerRepository;

public class MemoryRepositoryRegistry implements RepositoryRegistry {
    private final MemoryStorage storage;

    public MemoryRepositoryRegistry(MemoryStorage storage) {
        this.storage = storage;
    }

    @Override
    public CustomerRepository getCustomerRepository() {
        return new MemoryCustomerRepository(storage.getCustomerStorage());
    }
}
