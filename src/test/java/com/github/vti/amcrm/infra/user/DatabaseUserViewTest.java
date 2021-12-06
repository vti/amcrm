package com.github.vti.amcrm.infra.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.TestFactory;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.domain.user.UserRepository;
import com.github.vti.amcrm.domain.user.exception.UserExistsException;
import com.github.vti.amcrm.infra.TestDatabase;
import com.github.vti.amcrm.infra.user.dto.UserSummary;

class DatabaseUserViewTest {

    private DataSource dataSource;
    private UserRepository userRepository;
    private DatabaseUserView userView;

    @TempDir Path tmpDir;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = TestDatabase.setupDatabase(tmpDir);
        userRepository = new DatabaseUserRepository(dataSource);
        userView = new DatabaseUserView(dataSource);
    }

    @Test
    void returnsEmptyDetailsResult() {
        assertEquals(false, userView.load(TestData.getRandomId()).isPresent());
    }

    @Test
    void returnsDetails() {
        User user = createUser();

        UserSummary details = userView.load(user.getId().value()).get();

        assertEquals(user.getId().value(), details.getId());
        assertEquals(user.getName(), details.getName());
    }

    @Test
    void doesNotReturnDeleted() throws Exception {
        User user = createUser();

        user.delete(ActorId.of(TestData.getRandomId()));

        userRepository.store(user);

        assertFalse(userView.load(user.getId().value()).isPresent());
    }

    @Test
    void returnsEmptySummaryList() {
        assertEquals(0, userView.find().size());
    }

    @Test
    void returnsSummaryList() {
        User user = createUser();

        assertEquals(1, userView.find().size());
    }

    private User createUser() {
        User user = TestFactory.newUser();

        try {
            userRepository.store(user);
        } catch (UserExistsException e) {
            throw new RuntimeException(e);
        }

        return user;
    }
}
