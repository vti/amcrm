package com.github.vti.amcrm.domain.user;

import java.util.Optional;

import com.github.vti.amcrm.domain.user.exception.UserExistsException;

public interface UserRepository {
    Optional<User> load(UserId id);

    Optional<User> loadByName(String name);

    void store(User customer) throws UserExistsException;

    boolean isEmpty();
}
