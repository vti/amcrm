package com.github.vti.amcrm.domain.user.command;

import java.util.Objects;

import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.domain.user.UserId;
import com.github.vti.amcrm.domain.user.UserRepository;
import com.github.vti.amcrm.domain.user.exception.UserExistsException;
import com.github.vti.amcrm.domain.user.exception.UserNotFoundException;

public class ToggleUserAdminStatusCommand {
    private UserRepository userRepository;
    private ActorId actorId;
    private UserId id;

    private ToggleUserAdminStatusCommand(Builder builder) {
        this.userRepository = Objects.requireNonNull(builder.userRepository);
        this.actorId = Objects.requireNonNull(builder.actorId);
        this.id = Objects.requireNonNull(builder.id);
    }

    public void execute() throws UserNotFoundException {
        User user = this.userRepository.load(this.id).orElseThrow(UserNotFoundException::new);

        user.toggleAdminStatus(actorId);

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
        private ActorId actorId;
        private UserId id;

        public Builder userRepository(UserRepository userRepository) {
            this.userRepository = userRepository;
            return this;
        }

        public Builder actorId(ActorId actorId) {
            this.actorId = actorId;
            return this;
        }

        public Builder id(UserId id) {
            this.id = id;
            return this;
        }

        public ToggleUserAdminStatusCommand build() {
            return new ToggleUserAdminStatusCommand(this);
        }
    }
}
