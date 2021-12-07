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
import com.github.vti.amcrm.infra.pager.Page;
import com.github.vti.amcrm.infra.pager.Pager;
import com.github.vti.amcrm.infra.user.dto.UserSummary;

public class DatabaseUserViewTest {

    private UserRepository userRepository;
    private DatabaseUserView userView;

    @TempDir Path tmpDir;

    @BeforeEach
    void setUp() throws Exception {
        DataSource dataSource = TestDatabase.setupDatabase(tmpDir);
        userRepository = new DatabaseUserRepository(dataSource);
        userView = new DatabaseUserView(dataSource);
    }

    @Test
    void returnsEmptyDetailsResult() {
        assertFalse(userView.load(TestData.getRandomId()).isPresent());
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
        assertEquals(0, userView.find(new Pager()).getItems().size());
    }

    @Test
    void returnsSummaryList() {
        createUser();

        assertEquals(1, userView.find(new Pager()).getItems().size());
    }

    @Test
    void returnsSummaryListPaginated() {
        createUser();
        createUser();
        createUser();

        Page<UserSummary> page1 = userView.find(new Pager(2));

        assertEquals(2, page1.getPager().getLimit());
        assertEquals(2, page1.getPager().getOffset());
        assertEquals(2, page1.getItems().size());

        Page<UserSummary> page2 = userView.find(page1.getPager());

        assertEquals(2, page2.getPager().getLimit());
        assertEquals(4, page2.getPager().getOffset());
        assertEquals(1, page2.getItems().size());

        Page<UserSummary> page3 = userView.find(page2.getPager());

        assertEquals(2, page3.getPager().getLimit());
        assertEquals(6, page3.getPager().getOffset());
        assertEquals(0, page3.getItems().size());
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
