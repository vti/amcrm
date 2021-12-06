package com.github.vti.amcrm.domain.user.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.domain.user.UserId;
import com.github.vti.amcrm.domain.user.UserRepository;
import com.github.vti.amcrm.domain.user.exception.UserExistsException;
import com.github.vti.amcrm.domain.user.exception.UserNotFoundException;
import com.github.vti.amcrm.infra.user.MemoryUserRepository;

public class DeleteUserCommandTest {
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        Map<UserId, User> storage = new HashMap<>();
        userRepository = new MemoryUserRepository(storage);
    }

    @Test
    public void deletesExistingUser() throws UserExistsException, UserNotFoundException {
        UserId userId = createUser();
        ActorId actorId = ActorId.of(TestData.getRandomId());

        DeleteUserCommand command =
                DeleteUserCommand.builder()
                        .userRepository(userRepository)
                        .actorId(actorId)
                        .id(userId)
                        .build();

        command.execute();

        User user = userRepository.load(userId).get();

        assertTrue(user.isDeleted());
    }

    @Test
    public void throwsOnUnknownUser() {
        ActorId actorId = ActorId.of(TestData.getRandomId());

        ToggleUserAdminStatusCommand command =
                ToggleUserAdminStatusCommand.builder()
                        .userRepository(userRepository)
                        .actorId(actorId)
                        .id(UserId.of(TestData.getRandomId()))
                        .build();

        assertThrows(UserNotFoundException.class, command::execute);
    }

    public UserId createUser() throws UserExistsException {
        UserId userId = UserId.of(TestData.getRandomId());
        ActorId actorId = ActorId.of(TestData.getRandomId());

        CreateUserCommand command =
                CreateUserCommand.builder()
                        .userRepository(userRepository)
                        .actorId(actorId)
                        .id(userId)
                        .name(TestData.getRandomName())
                        .build();

        command.execute();

        return userId;
    }
}
