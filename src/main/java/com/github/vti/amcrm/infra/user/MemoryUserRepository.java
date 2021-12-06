package com.github.vti.amcrm.infra.user;

import java.util.Map;
import java.util.Optional;

import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.domain.user.UserId;
import com.github.vti.amcrm.domain.user.UserRepository;
import com.github.vti.amcrm.domain.user.exception.UserExistsException;

public class MemoryUserRepository implements UserRepository {
    private final Map<UserId, User> storage;

    public MemoryUserRepository(Map<UserId, User> storage) {
        this.storage = storage;
    }

    @Override
    public Optional<User> load(UserId id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void store(User customer) throws UserExistsException {
        if (storage.containsKey(customer.getId())) {
            User existingUser = storage.get(customer.getId());

            if (existingUser.getVersion() != customer.getVersion()) {
                throw new UserExistsException();
            }
        }

        storage.put(customer.getId(), customer);

        customer.incrementVersion();
    }
}
