package com.github.vti.amcrm.infra.registry;

import java.util.Objects;

import javax.sql.DataSource;

import com.github.vti.amcrm.domain.RepositoryRegistry;
import com.github.vti.amcrm.domain.customer.CustomerRepository;
import com.github.vti.amcrm.domain.user.UserRepository;
import com.github.vti.amcrm.infra.customer.DatabaseCustomerRepository;
import com.github.vti.amcrm.infra.user.DatabaseUserRepository;

public class DatabaseRepositoryRegistry implements RepositoryRegistry {
    private final DataSource dataSource;

    public DatabaseRepositoryRegistry(DataSource dataSource) {
        Objects.requireNonNull(dataSource);

        this.dataSource = dataSource;
    }

    @Override
    public CustomerRepository getCustomerRepository() {
        return new DatabaseCustomerRepository(dataSource);
    }

    @Override
    public UserRepository getUserRepository() {
        return new DatabaseUserRepository(dataSource);
    }
}
