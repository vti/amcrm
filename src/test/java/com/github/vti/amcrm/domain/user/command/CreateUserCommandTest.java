package com.github.vti.amcrm.domain.user.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.domain.user.UserId;
import com.github.vti.amcrm.domain.user.UserRepository;
import com.github.vti.amcrm.domain.user.exception.UserExistsException;
import com.github.vti.amcrm.infra.user.MemoryUserRepository;

class CreateUserCommandTest {

    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        Map<UserId, User> storage = new HashMap<>();
        userRepository = new MemoryUserRepository(storage);
    }

    @Test
    public void createsUser() throws UserExistsException {
        UserId userId = createUser();

        Optional<User> user = userRepository.load(userId);

        assertTrue(user.isPresent());
    }

    public UserId createUser() throws UserExistsException {
        UserId userId = UserId.of(TestData.getRandomId());
        UserId actorId = UserId.of(TestData.getRandomId());

        CreateUserCommand command =
                CreateUserCommand.builder()
                        .userRepository(userRepository)
                        .userId(actorId)
                        .id(userId)
                        .name(TestData.getRandomName())
                        .build();

        command.execute();

        return userId;
    }
}
