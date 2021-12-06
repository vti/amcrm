package com.github.vti.amcrm.domain.user.command;

import java.util.Objects;

import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.domain.user.UserId;
import com.github.vti.amcrm.domain.user.UserRepository;
import com.github.vti.amcrm.domain.user.exception.UserExistsException;
import com.github.vti.amcrm.domain.user.exception.UserNotFoundException;

public class DeleteUserCommand {
    private UserRepository userRepository;
    private UserId userId;
    private UserId id;

    private DeleteUserCommand(Builder builder) {
        this.userRepository = Objects.requireNonNull(builder.userRepository);
        this.userId = Objects.requireNonNull(builder.userId);
        this.id = Objects.requireNonNull(builder.id);
    }

    public void execute() throws UserNotFoundException {
        User user = this.userRepository.load(this.id).orElseThrow(UserNotFoundException::new);

        if (user.isDeleted()) {
            throw new UserNotFoundException();
        }

        user.delete(this.userId);

        try {
            this.userRepository.store(user);
        } catch (UserExistsException e) {
            throw new RuntimeException(e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UserRepository userRepository;
        private UserId userId;
        private UserId id;

        public Builder userRepository(UserRepository userRepository) {
            this.userRepository = userRepository;
            return this;
        }

        public Builder userId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public Builder id(UserId id) {
            this.id = id;
            return this;
        }

        public DeleteUserCommand build() {
            return new DeleteUserCommand(this);
        }
    }
}
