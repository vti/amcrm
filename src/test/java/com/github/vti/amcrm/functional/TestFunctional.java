package com.github.vti.amcrm.functional;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.RestAssured;

import com.github.vti.amcrm.Config;
import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.api.Api;
import com.github.vti.amcrm.api.DefaultObjectMapper;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.session.Session;
import com.github.vti.amcrm.domain.session.SessionId;
import com.github.vti.amcrm.domain.user.UserId;
import com.github.vti.amcrm.domain.user.command.CreateUserCommand;
import com.github.vti.amcrm.domain.user.exception.UserExistsException;
import com.github.vti.amcrm.functional.model.CustomerRequestModel;
import com.github.vti.amcrm.functional.model.UserRequestModel;
import com.github.vti.amcrm.infra.registry.RegistryFactory;

public class TestFunctional {
    private static SessionId defaultAdminSessionId;
    private static SessionId defaultUserSessionId;

    public static int findOpenPort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("No free port available");
        }
    }

    public static SessionId createUserSessionId(RegistryFactory registryFactory) {
        UserId userId = UserId.of(UUID.randomUUID().toString());

        CreateUserCommand command =
                CreateUserCommand.builder()
                        .userRepository(registryFactory.getRepositoryRegistry().getUserRepository())
                        .actorId(ActorId.of(UUID.randomUUID().toString()))
                        .id(userId)
                        .name(TestData.getRandomName())
                        .build();

        try {
            command.execute();
        } catch (UserExistsException e) {
            throw new RuntimeException("Failed to create a user", e);
        }

        Session session =
                Session.builder()
                        .id(SessionId.of(UUID.randomUUID().toString()))
                        .actorId(ActorId.of(userId.value()))
                        .expiresAt(Instant.now().plusSeconds(TimeUnit.HOURS.toSeconds(1)))
                        .build();
        registryFactory.getRepositoryRegistry().getSessionRepository().store(session);

        return session.getId();
    }

    public static Api buildApi(Path tmpDir) {
        Config config = Config.builder().port(TestFunctional.findOpenPort()).build();

        String baseUrl = config.getBaseUrl().toString();

        RestAssured.baseURI = baseUrl;

        Api api = new Api(config);

        defaultAdminSessionId = api.makeSureAtLeastAdminExists().get();
        defaultUserSessionId = createUserSessionId(api.getRegistryFactory());

        return api;
    }

    public static Map<String, String> getAuthenticatedUserHeaders() {
        return new HashMap<String, String>() {
            {
                put("Authorization", defaultUserSessionId.value());
            }
        };
    }

    public static Map<String, String> getAuthenticatedAdminHeaders() {
        return new HashMap<String, String>() {
            {
                put("Authorization", defaultAdminSessionId.value());
            }
        };
    }

    public static String createCustomer() throws JsonProcessingException {
        CustomerRequestModel customer = new CustomerRequestModel();
        customer.id = TestData.getRandomId();
        customer.name = TestData.getRandomName();
        customer.surname = TestData.getRandomSurname();

        given().headers(getAuthenticatedUserHeaders())
                .body(DefaultObjectMapper.get().writeValueAsBytes(customer))
                .when()
                .post(Api.Resource.CUSTOMERS.toString())
                .then()
                .statusCode(200);

        return customer.id;
    }

    public static String createUser() throws JsonProcessingException {
        UserRequestModel user = new UserRequestModel();
        user.id = TestData.getRandomId();
        user.admin = false;
        user.name = TestData.getRandomName();

        given().headers(getAuthenticatedAdminHeaders())
                .body(DefaultObjectMapper.get().writeValueAsBytes(user))
                .when()
                .post(Api.Resource.USERS.toString())
                .then()
                .statusCode(200);

        return user.id;
    }

    public static String buildPath(Api.Resource resource, String... parts) {
        return String.join("/", resource.toString(), String.join("/", parts));
    }

    public enum Model {
        CUSTOMER("schema/api/model/customer.json"),
        CUSTOMER_LIST("schema/api/model/customer-list.json"),
        USER("schema/api/model/user.json"),
        USER_LIST("schema/api/model/user-list.json"),
        VALIDATION_ERROR("schema/api/model/validation-error.json");

        private final String value;

        Model(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
