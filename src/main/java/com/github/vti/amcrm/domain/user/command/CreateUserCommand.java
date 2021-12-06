package com.github.vti.amcrm.domain.user.command;

import java.util.Objects;

import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.domain.user.UserId;
import com.github.vti.amcrm.domain.user.UserRepository;
import com.github.vti.amcrm.domain.user.exception.UserExistsException;

public class CreateUserCommand {
    private UserRepository userRepository;
    private UserId userId;
    private UserId id;
    private Boolean isAdmin;
    private String name;

    private CreateUserCommand(Builder builder) {
        this.userRepository = Objects.requireNonNull(builder.userRepository, "userRepository");
        this.userId = Objects.requireNonNull(builder.userId, "userId");
        this.id = Objects.requireNonNull(builder.id, "id");
        this.isAdmin = Objects.requireNonNull(builder.isAdmin, "id");
        this.name = Objects.requireNonNull(builder.name, "name");
    }

    public void execute() throws UserExistsException {
        User user =
                User.builder()
                        .id(this.id)
                        .name(this.name)
                        .isAdmin(this.isAdmin)
                        .createdBy(this.userId)
                        .build();

        this.userRepository.store(user);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UserRepository userRepository;
        private UserId userId;
        private Boolean isAdmin = false;
        private UserId id;
        private String name;

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

        public Builder isAdmin(Boolean isAdmin) {
            this.isAdmin = isAdmin;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public CreateUserCommand build() {
            return new CreateUserCommand(this);
        }
    }
}