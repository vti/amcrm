package com.github.vti.amcrm.api.service;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.linecorp.armeria.common.HttpMethod;
import com.linecorp.armeria.common.HttpRequest;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.TestFactory;
import com.github.vti.amcrm.api.Client;
import com.github.vti.amcrm.api.exception.ConflictException;
import com.github.vti.amcrm.api.exception.NotFoundException;
import com.github.vti.amcrm.api.service.request.CreateUserRequest;
import com.github.vti.amcrm.domain.RepositoryRegistry;
import com.github.vti.amcrm.infra.TestDatabase;
import com.github.vti.amcrm.infra.registry.DatabaseRepositoryRegistry;
import com.github.vti.amcrm.infra.registry.DatabaseViewRegistry;
import com.github.vti.amcrm.infra.registry.ViewRegistry;
import com.github.vti.amcrm.infra.user.dto.UserSummary;

@Tag("integration")
public class UserServiceTest {

    private UserService service;
    private Client client;
    private final String baseUrl = "http://localhost:4567";

    @TempDir Path tmpDir;

    @BeforeEach
    void setUp() throws Exception {
        DataSource dataSource = TestDatabase.setupDatabase(tmpDir);
        RepositoryRegistry repositoryRegistry = new DatabaseRepositoryRegistry(dataSource);
        ViewRegistry viewRegistry = new DatabaseViewRegistry(dataSource, baseUrl);

        service = Mockito.spy(new UserService(repositoryRegistry, viewRegistry));

        client = Client.user(TestData.getRandomId());
        Mockito.doReturn(client).when(service).getClient();
    }

    @Test
    void throwsOnUnknownId() {
        HttpRequest req = HttpRequest.of(HttpMethod.GET, "/users/" + TestData.getRandomId());
        assertThrows(NotFoundException.class, () -> service.getUserSummary(TestData.getRandomId()));
    }

    @Test
    void returnsUserSummary() {
        CreateUserRequest request = TestFactory.newCreateUserRequest();
        service.createUser(request);

        UserSummary user = service.getUserSummary(request.getId());

        assertEquals(request.getId(), user.getId());
        assertFalse(user.isAdmin());
        assertEquals(request.getName(), user.getName());
    }

    @Test
    void returnsEmptyList() {
        List<UserSummary> users = service.getUserList(Optional.empty(), Optional.empty());

        assertEquals(0, users.size());
    }

    @Test
    void returnsList() {
        CreateUserRequest request1 = TestFactory.newCreateUserRequest();
        service.createUser(request1);

        CreateUserRequest request2 = TestFactory.newCreateUserRequest();
        service.createUser(request2);

        CreateUserRequest request3 = TestFactory.newCreateUserRequest();
        service.createUser(request3);

        List<UserSummary> users = service.getUserList(Optional.empty(), Optional.empty());

        assertEquals(3, users.size());
    }

    @Test
    void returnsListLimited() {
        CreateUserRequest request1 = TestFactory.newCreateUserRequest();
        service.createUser(request1);

        CreateUserRequest request2 = TestFactory.newCreateUserRequest();
        service.createUser(request2);

        CreateUserRequest request3 = TestFactory.newCreateUserRequest();
        service.createUser(request3);

        List<UserSummary> users = service.getUserList(Optional.of(2), Optional.empty());

        assertEquals(2, users.size());
    }

    @Test
    void createsNewUser() {
        CreateUserRequest request = TestFactory.newCreateUserRequest();

        UserSummary user = service.createUser(request);

        assertEquals(request.getId(), user.getId());
        assertEquals(request.getName(), user.getName());
    }

    @Test
    void throwsWhenUserAlreadyExists() {
        CreateUserRequest request = TestFactory.newCreateUserRequest();

        service.createUser(request);

        assertThrows(ConflictException.class, () -> service.createUser(request));
    }

    @Test
    void throwsWhenToggleAdminStatusUnknownUser() {
        assertThrows(
                NotFoundException.class,
                () -> service.toggleUserAdminStatus(TestData.getRandomId()));
    }

    @Test
    void togglesUserAdminStatus() {
        CreateUserRequest createRequest = TestFactory.newCreateUserRequest();
        service.createUser(createRequest);

        UserSummary user = service.toggleUserAdminStatus(createRequest.getId());

        assertEquals(createRequest.getId(), user.getId());
        assertTrue(user.isAdmin());
    }

    @Test
    void throwsWhenDeletingUnknownUser() {
        assertThrows(NotFoundException.class, () -> service.deleteUser(TestData.getRandomId()));
    }

    @Test
    void deletesUser() {
        CreateUserRequest createRequest = TestFactory.newCreateUserRequest();
        service.createUser(createRequest);

        service.deleteUser(createRequest.getId());

        assertThrows(NotFoundException.class, () -> service.getUserSummary(createRequest.getId()));
    }

    @Test
    void throwsWhenDeletingAlreadyDeletedUser() {
        CreateUserRequest createRequest = TestFactory.newCreateUserRequest();
        service.createUser(createRequest);

        service.deleteUser(createRequest.getId());

        assertThrows(NotFoundException.class, () -> service.deleteUser(createRequest.getId()));
    }
}
