package com.github.vti.amcrm.infra.user;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.TestFactory;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.domain.user.UserId;
import com.github.vti.amcrm.domain.user.UserRepository;
import com.github.vti.amcrm.domain.user.exception.UserExistsException;
import com.github.vti.amcrm.infra.OptimisticLockException;
import com.github.vti.amcrm.infra.TestDatabase;

public class DatabaseUserRepositoryTest {

    private UserRepository userRepository;

    @TempDir Path tmpDir;

    @BeforeEach
    void setUp() throws Exception {
        DataSource dataSource = TestDatabase.setupDatabase(tmpDir);
        userRepository = new DatabaseUserRepository(dataSource);
    }

    @Test
    void returnsEmptyOnUnknownId() {
        assertEquals(false, userRepository.load(UserId.of(TestData.getRandomId())).isPresent());
    }

    @Test
    void loadsUser() throws Exception {
        User user = TestFactory.newUser();

        userRepository.store(user);

        Optional<User> userOptional = userRepository.load(user.getId());

        assertEquals(true, userOptional.isPresent());
        assertEquals(user.getName(), userOptional.get().getName());
    }

    @Test
    void storesUser() throws Exception {
        User user = TestFactory.newUser();

        userRepository.store(user);

        User loadedUser = userRepository.load(user.getId()).get();

        assertEquals(user.getId(), loadedUser.getId());
        assertEquals(1L, loadedUser.getVersion());
        assertEquals(user.getName(), loadedUser.getName());
        assertEquals(user.isAdmin(), loadedUser.isAdmin());
    }

    @Test
    void storingClearsAllEvents() throws Exception {
        User user = TestFactory.newUser();

        assertTrue(user.getEvents().size() != 0);

        userRepository.store(user);

        assertEquals(0, user.getEvents().size());
    }

    @Test
    void storesUpdatedUser() throws Exception {
        ActorId actorId = ActorId.of(TestData.getRandomId());
        User user = TestFactory.newUser();

        userRepository.store(user);

        user.toggleAdminStatus(actorId);

        userRepository.store(user);

        Optional<User> userOptional = userRepository.load(user.getId());

        assertTrue(userOptional.get().isAdmin());
        assertTrue(userOptional.get().getUpdatedBy().equals(actorId));
    }

    @Test
    void throwsWhenUserAlreadyExists() throws Exception {
        User user1 = TestFactory.newUserBuilder().id(UserId.of("1")).build();
        User user2 = TestFactory.newUserBuilder().id(UserId.of("1")).build();

        userRepository.store(user1);

        assertThrows(UserExistsException.class, () -> userRepository.store(user2));
    }

    @Test
    void optimisticLockingPreventsCorruptionDuringUpdate() throws Exception {
        User user = TestFactory.newUser();

        assertEquals(0, user.getVersion());

        userRepository.store(user);

        assertEquals(1, user.getVersion());

        User user1 = userRepository.load(user.getId()).get();
        User user2 = userRepository.load(user.getId()).get();

        assertEquals(1, user1.getVersion());
        assertEquals(1, user2.getVersion());

        userRepository.store(user1);

        assertEquals(2, user1.getVersion());
        assertEquals(1, user2.getVersion());

        assertThrows(OptimisticLockException.class, () -> userRepository.store(user2));
    }

    @Test
    void softDeletes() throws Exception {
        ActorId actorId = ActorId.of(TestData.getRandomId());
        User user = TestFactory.newUser();

        user.delete(actorId);

        userRepository.store(user);

        User loadedUser = userRepository.load(user.getId()).get();

        assertTrue(loadedUser.isDeleted());
        assertEquals(actorId, loadedUser.getDeletedBy());
    }
}
