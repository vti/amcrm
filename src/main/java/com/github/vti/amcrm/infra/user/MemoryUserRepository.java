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
    public Optional<User> loadByName(String name) {
        return storage.entrySet().stream()
                .filter(e -> e.getValue().getName().equals(name))
                .findFirst()
                .map(e -> e.getValue());
    }

    @Override
    public void store(User user) throws UserExistsException {
        if (storage.containsKey(user.getId())) {
            User existingUser = storage.get(user.getId());

            if (!existingUser.getVersion().equals(user.getVersion())) {
                throw new UserExistsException();
            }
        }

        storage.put(user.getId(), user);

        user.incrementVersion();
    }

    @Override
    public boolean isEmpty() {
        return storage.isEmpty();
    }
}
